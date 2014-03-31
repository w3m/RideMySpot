package account;

import java.util.HashMap;

import activity.AddUserActivity;
import activity.MapActivity;
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

import entity.Rmsendpoint;
import entity.model.CollectionResponseUsers;
import entity.model.Users;
 
public class SessionManager {
    SharedPreferences pref;
     
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
     
    // Constructor
    public SessionManager(Context context){
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }
     
    /**
     * Create login session
     * */
    public void createLoginSession(String id, String name, String email, String type){
        mEditor = pref.edit();
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.putString(KEY_ID, id);
        mEditor.putString(KEY_NAME, name);
        mEditor.putString(KEY_EMAIL, email);
        mEditor.putString(KEY_TYPE, type);
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
	    		//TODO Laisser l'utilisateur choisir son mail pour se connecter
	    		//AlertDialog
	    		mDialog = new Dialog(mContext);
	            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	            View convertView = (View) inflater.inflate(R.layout.email, null);
	            mDialog.setTitle("Choisissez votre compte");
	            mDialog.setContentView(convertView);
	            ListView lv = (ListView) convertView.findViewById(R.id.listView1);
	            lv.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,listAdress));
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
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_TYPE, pref.getString(KEY_TYPE, null));
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
     
    /**
     * Quick check for login
     * **/
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
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
				Log.d("impossible de récupérer les users", e.getMessage(), e);//TODO getressource
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
