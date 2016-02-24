package account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.w3m.ridemyspot.R;

import java.util.HashMap;

import activity.AddUserActivity;
import activity.MapActivity;
import entity.Rmsendpoint;
import entity.model.CollectionResponseUsers;
import entity.model.Users;
 
public class SessionManager {
    SharedPreferences mSharedPreference;
     
    Editor mEditor;
    Context mContext;
    int PRIVATE_MODE = 0;
	AccountManager mAccountManager;
	String mAdress;
	Dialog mDialog;
	
    // Sharedpref file name
    private static final String PREF_NAME = "USER";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IS_LOGIN";

    public static final String KEY_ID = "USER_ID";
    public static final String KEY_NAME = "USER_NAME";
    public static final String KEY_EMAIL = "USER_EMAIL";
    public static final String KEY_TYPE = "USER_TYPE";
    public static final String KEY_CHK_VOTE = "USER_CHK_VOTE";
    public static final String KEY_NB_CHK_VOTE = "USER_NB_CHK_VOTE";
     
    // Constructor
    public SessionManager(Context context){
        this.mContext = context;
        mSharedPreference = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }
     
    /**
     * Create login session
     * */
    public void createLoginSession(String id, String name, String email, String type){
        mEditor = mSharedPreference.edit();
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.putString(KEY_ID, id);
        mEditor.putString(KEY_NAME, name);
        mEditor.putString(KEY_EMAIL, email);
        mEditor.putString(KEY_TYPE, type);
        mEditor.putBoolean(KEY_CHK_VOTE, false);
        mEditor.putInt(KEY_NB_CHK_VOTE, 0);
        mEditor.commit();
    }   
     
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){

        if(!this.isLoggedIn()){

	    	String listAdress[] = getAccountNames();
	    	if(listAdress.length > 1){
	    		mDialog = new Dialog(mContext);
	            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	            View convertView = (View) inflater.inflate(R.layout.email, null);
	            mDialog.setTitle("Choisissez votre compte");
	            mDialog.setContentView(convertView);
	            ListView lv = (ListView) convertView.findViewById(R.id.listView1);
	            lv.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,listAdress));
	            mDialog.setCancelable(false);
	            mDialog.show();
	            
	            lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						mAdress = parent.getItemAtPosition(position).toString();
			    		new ListUsers().execute(mAdress);
			    		mDialog.dismiss();
					}
				});
	    	} else {
	            mAdress = listAdress[0];
	    		new ListUsers().execute(mAdress);
	    	}
        } else {
        	returnToMap();
        }
    }
     
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, mSharedPreference.getString(KEY_ID, null));
        user.put(KEY_NAME, mSharedPreference.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, mSharedPreference.getString(KEY_EMAIL, null));
        user.put(KEY_TYPE, mSharedPreference.getString(KEY_TYPE, null));
        return user;
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        mEditor.clear();
        mEditor.commit();
         
        // After logout redirect user to Loging Activity
        Intent i = new Intent(mContext, AddUserActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public boolean getChkVote(){
 	   return mSharedPreference.getBoolean(KEY_CHK_VOTE, false);
    }  
    
    public void putChkVote(boolean check){
 	   mEditor = mSharedPreference.edit();
        mEditor.putBoolean(KEY_CHK_VOTE, check);
        mEditor.commit();
    }
    
    public int getNbChkVote(){
 	   return mSharedPreference.getInt(KEY_NB_CHK_VOTE, 0);
    }  
    
    public void putNbChkVote(int nbChk){
 	   mEditor = mSharedPreference.edit();
       mEditor.putInt(KEY_NB_CHK_VOTE, nbChk);
       mEditor.commit();
    }
    
    /**
     * Quick check for login
     * **/
    public boolean isLoggedIn(){
        return mSharedPreference.getBoolean(IS_LOGIN, false);
    }
    
    public String[] getAccountNames() {
		mAccountManager = AccountManager.get(mContext);
		Account[] accounts = mAccountManager
				.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		String[] names = new String[accounts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
	}

	public boolean isNetworkAvailable() {

		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.e("Network Testing", "***Available***");
			return true;
		}
		Log.e("Network Testing", "***Not Available***");
		return false;
	}
	
private class ListUsers extends AsyncTask<String, Void, CollectionResponseUsers>{
		
		@Override
		protected CollectionResponseUsers doInBackground(String... params) {
			CollectionResponseUsers User = null;
			try{
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				User = service.listUsers().setPAdress(params[0]).execute();
			} catch (Exception e){
				Log.d("impossible de récupérer les informations du compte", e.getMessage(), e);//TODO getressource
			}
			return User;
		}
		

		@Override
		protected void onPostExecute(CollectionResponseUsers User) {
			if(User != null && User.getItems() != null && User.getItems().get(0) != null){
				Users user = User.getItems().get(0);
				createLoginSession(user.getId().toString(), user.getName(), user.getAdress(), user.getType());
				returnToMap();
			}else{
				 // user is not logged in redirect him to Login Activity
	            Intent intent = new Intent(mContext, AddUserActivity.class);
	            intent.putExtra(KEY_EMAIL, mAdress);
	            // Add new Flag to start new Activity and closing all the activities
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            mContext.startActivity(intent);
			}
		}
	}

	public void returnToMap() {
		 // user is not logged in redirect him to Login Activity
		Intent intent = new Intent(mContext, MapActivity.class);
		// Closing all the Activities
		intent.putExtra(KEY_EMAIL, mAdress);
		// Add new Flag to start new Activity and closing all the activities
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    mContext.startActivity(intent);
	}
}
