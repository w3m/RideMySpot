package activity;

import account.AbstractGetNameTask;
import account.GetNameInForeground;
import account.SessionManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.w3m.ridemyspot.R;

/**
 * @author manish
 * 
 */
public class rms_splashscreen extends Activity {
	Context mContext = rms_splashscreen.this;
	AccountManager mAccountManager;
	String token;
	int serverCode;
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

	SessionManager mSessionManager;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Splash screen view
		setContentView(R.layout.splashscreen);
		
		mSessionManager = new SessionManager(this);
		mSessionManager.checkLogin();	
		
		//syncGoogleAccount();
	}

	
	
	private AbstractGetNameTask getTask(rms_splashscreen activity, String email,
			String scope) {
		return new GetNameInForeground(activity, email, scope);

	}

	public void syncGoogleAccount() {
		if (isNetworkAvailable() == true) {
			String[] accountarrs = mSessionManager.getAccountNames();
			if (accountarrs.length > 0) {
				//you can set here account for login
				getTask(rms_splashscreen.this, accountarrs[0], SCOPE).execute();
			} else {
				Toast.makeText(rms_splashscreen.this, "No Google Account Sync!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(rms_splashscreen.this, "No Network Service!",
					Toast.LENGTH_SHORT).show();
		}
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
}