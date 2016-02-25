package activity;

import account.SessionManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.w3m.ridemyspot.R;

import entity.Rmsendpoint;
import entity.model.Users;

public class AddUserActivity extends Activity implements OnClickListener{

	public String mAdress;
	private String mType;
	
	private EditText mName;
	private Button mValidate;
	
	private SessionManager mSessionManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_user);
		initializeView();

		mSessionManager = new SessionManager(this);
		mAdress = getIntent().getStringExtra(SessionManager.KEY_EMAIL);
	}

	private void initializeView() {
		mName = (EditText) findViewById(R.id.add_user_name);
		mValidate = (Button) findViewById(R.id.add_user_validate);
		mValidate.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void onRadioButtonClicked(View radioButton) {
		switch (radioButton.getId()) {
		case R.id.add_user_roller:
			mType = getString(R.string.text_roller);
			break;
		case R.id.add_user_bmx:
			mType = getString(R.string.text_bmx);
			break;
		case R.id.add_user_skate:
			mType = getString(R.string.text_skate);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		String check = "";
		if (mName.getText().toString().equals("")){
			check += getString(R.string.add_user_minimum_name);
		}
		if(mType.equals("")){
			check += getString(R.string.add_user_minimum_type);
		} 
		if(!"".equals(check)){
			Toast.makeText(this, getString(R.string.add_user_minimum_error_text) + check, Toast.LENGTH_LONG).show();
		} else {
			new AddUser().execute();
		}
	}
	
	private class AddUser extends AsyncTask<Void, Void, Users>{
		
		public AddUser(){
		}
		
		@Override
		protected Users doInBackground(Void... params) {
			Users response = null;
			try{
				
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				
				Users User = new Users();
				
				User.setAdress(mAdress);
				User.setName(mName.getText().toString());
				User.setType(String.valueOf(mType));
				
				response = service.insertUsers(User).execute();
				
			} catch (Exception e){
				Log.d(getString(R.string.add_user_loading_error_log), e.getMessage(), e);
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Users User) {

			if(User != null){
				mSessionManager.createLoginSession(User.getId().toString(), User.getName(), User.getAdress(), User.getType());
				 // user is not logged in redirect him to Login Activity
	            Intent intent = new Intent(AddUserActivity.this, MapActivity.class);
	            // Closing all the Activities
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            // Add new Flag to start new Activity
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
	            finish();
			} else {
				Toast.makeText(getBaseContext(), getString(R.string.add_user_loading_error), Toast.LENGTH_LONG).show();
			}
			
		}
	}

}
