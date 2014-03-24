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

public class rms_add_user extends Activity{

	public String m_adress;
	private String m_type;
	
	private EditText m_name;
	private Button m_validate;
	
	private SessionManager m_sessionManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_user);
		initializeView();

		m_sessionManager = new SessionManager(this);
		m_adress = getIntent().getStringExtra(SessionManager.KEY_EMAIL);
	}

	private void initializeView() {
		m_name = (EditText) findViewById(R.id.add_user_name);
		m_validate = (Button) findViewById(R.id.add_user_validate);
		m_validate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AddUser().execute();
			}
		});
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
			m_type = "Roller";
			break;
		case R.id.add_user_bmx:
			m_type = "BMX";
			break;
		case R.id.add_user_skate:
			m_type = "Skate";
			break;
		default:
			break;
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
				
				User.setAdress(m_adress);
				User.setName(m_name.getText().toString());
				User.setType(String.valueOf(m_type));
				
				response = service.insertUsers(User).execute();
				
			} catch (Exception e){
				Log.d("impossible d'ajouter le user", e.getMessage(), e);//TODO getressource
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Users User) {

			if(User != null){
				m_sessionManager.createLoginSession(User.getId().toString(), User.getName(), User.getAdress(), User.getType());
				 // user is not logged in redirect him to Login Activity
	            Intent intent = new Intent(rms_add_user.this, rms_maps.class);
	            // Closing all the Activities
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            // Add new Flag to start new Activity
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
	            finish();
			} else {
				Toast.makeText(getBaseContext(), "L'utilisateur n'a pas été ajouté!", Toast.LENGTH_LONG).show();
			}
			
		}
	}
}
