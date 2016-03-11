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

    public static final String KEY_TUTORIAL_MAP = "KEY_TUTORIAL_MAP";
     
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
        mEditor.apply();
    }   
     
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
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

    public void updateLogin(String name, String email, String type){
        mEditor = mSharedPreference.edit();
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.putString(KEY_NAME, name);
        mEditor.putString(KEY_EMAIL, email);
        mEditor.putString(KEY_TYPE, type);
        mEditor.apply();
    }

    public boolean getChkVote(){
 	   return mSharedPreference.getBoolean(KEY_CHK_VOTE, false);
    }  
    
    public void putChkVote(boolean check){
 	   mEditor = mSharedPreference.edit();
        mEditor.putBoolean(KEY_CHK_VOTE, check);
        mEditor.apply();
    }
    
    public int getNbChkVote(){
 	   return mSharedPreference.getInt(KEY_NB_CHK_VOTE, 0);
    }  
    
    public void putNbChkVote(int nbChk){
 	   mEditor = mSharedPreference.edit();
       mEditor.putInt(KEY_NB_CHK_VOTE, nbChk);
       mEditor.apply();
    }


    public boolean tutorialMapHasShown(){
        if(!mSharedPreference.getBoolean(KEY_TUTORIAL_MAP, false)){
            mEditor = mSharedPreference.edit();
            mEditor.putBoolean(KEY_TUTORIAL_MAP, true);
            mEditor.apply();
            return false;
        }
        return mSharedPreference.getBoolean(KEY_TUTORIAL_MAP, false);
    }
    
    /**
     * Quick check for login
     * **/
    public boolean isLoggedIn(){
        return mSharedPreference.getBoolean(IS_LOGIN, false);
    }

	/*
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
	*/
}
