package activity;

import account.AbstractGetNameTask;
import account.GetNameInForeground;
import account.SessionManager;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.w3m.ridemyspot.R;

/**
 * @author manish
 * 
 */
public class SplashScreenActivity extends Activity {
	Context mContext = SplashScreenActivity.this;
	AccountManager mAccountManager;
	String token;
	int serverCode;
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

	SessionManager mSessionManager;

	public static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 101;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Splash screen view
		setContentView(R.layout.splashscreen);


		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.GET_ACCOUNTS)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.GET_ACCOUNTS)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.GET_ACCOUNTS},
						MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		} else {
			checkLogin();

			//syncGoogleAccount();
		}
	}

	private void checkLogin(){
		mSessionManager = new SessionManager(this);
		mSessionManager.checkLogin();
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_GET_ACCOUNTS: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

					checkLogin();

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	
	private AbstractGetNameTask getTask(SplashScreenActivity activity, String email,
			String scope) {
		return new GetNameInForeground(activity, email, scope);

	}

	public void syncGoogleAccount() {
		if (isNetworkAvailable() == true) {
			String[] accountarrs = mSessionManager.getAccountNames();
			if (accountarrs.length > 0) {
				//you can set here account for login
				getTask(SplashScreenActivity.this, accountarrs[0], SCOPE).execute();
			} else {
				Toast.makeText(SplashScreenActivity.this, "No Google Account Sync!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(SplashScreenActivity.this, "No Network Service!",
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