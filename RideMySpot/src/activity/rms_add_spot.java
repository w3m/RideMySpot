package activity;


import model.Spot;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.w3m.ridemyspot.R;

import entity.Rmsendpoint;
import entity.model.Spots;

public class rms_add_spot extends FragmentActivity implements OnTouchListener, OnClickListener, OnCheckedChangeListener{
	
	private GoogleMap m_map;
	
	private ScrollView m_scrollView;
	private ImageView m_transparentImage;
	
	private Button m_validate;
	private Button m_cancel;

	private RadioButton m_checkRoller;
	private RadioButton m_checkBmx;
	private RadioButton m_checkSkate;
	private RadioButton m_checkSkatepark;
	
	private RatingBar m_ratingBar;

	private EditText m_editName;
	private EditText m_editDescription;
	
	private Marker m_spot;
	private int m_type = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_spot);
		
		initializeView();
		
		Intent intent = getIntent();
		LatLng position = (LatLng) intent.getExtras().get("position");
		
		m_spot = m_map.addMarker(new MarkerOptions()
        .position(position)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map)));
		m_spot.setDraggable(true);
		
		m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
		
		m_transparentImage.setOnTouchListener(this);
		
	}

	private void initializeView() {
		m_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_spot_map)).getMap();
		m_scrollView = (ScrollView) findViewById(R.id.add_spot_scrollview);
		m_transparentImage = (ImageView) findViewById(R.id.add_spot_transparent_image);

		m_validate = (Button) findViewById(R.id.add_spot_valider);
		m_cancel = (Button) findViewById(R.id.add_spot_annuler);
		m_validate.setOnClickListener(this);
		m_cancel.setOnClickListener(this);

		m_checkRoller = (RadioButton) findViewById(R.id.add_spot_roller);
		m_checkBmx = (RadioButton) findViewById(R.id.add_spot_bmx);
		m_checkSkate = (RadioButton) findViewById(R.id.add_spot_skate);
		m_checkSkatepark = (RadioButton) findViewById(R.id.add_spot_skatepark);
		m_checkRoller.setOnCheckedChangeListener(this);
		m_checkBmx.setOnCheckedChangeListener(this);
		m_checkSkate.setOnCheckedChangeListener(this);
		m_checkSkatepark.setOnCheckedChangeListener(this);
		
		m_ratingBar = (RatingBar) findViewById(R.id.add_spot_rating);

		m_editName = (EditText) findViewById(R.id.add_spot_edit_name);
		m_editDescription = (EditText) findViewById(R.id.add_spot_edit_description);
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Disallow ScrollView to intercept touch events.
				m_scrollView.requestDisallowInterceptTouchEvent(true);
				// Disable touch on transparent view
				return false;
			case MotionEvent.ACTION_UP:
				// Allow ScrollView to intercept touch events.
				m_scrollView.requestDisallowInterceptTouchEvent(false);
				return true;
			case MotionEvent.ACTION_MOVE:
				m_scrollView.requestDisallowInterceptTouchEvent(true);
				return false;
			default: 
				return true;
		} 
	}
	
	private class AddSpot extends AsyncTask<LatLng, Void, Spots>{
		private Context m_context;
		private ProgressDialog m_progressDialog;
		
		public AddSpot(Context context){
			this.m_context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			m_progressDialog = new ProgressDialog(m_context);
			m_progressDialog.setMessage("Ajout du spot..."); //TODO getressource
			m_progressDialog.show();
		}
		
		@Override
		protected Spots doInBackground(LatLng... params) {
			Spots response = null;
			try{
				
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				
				Spots spot = new Spots();
				
				float note = m_ratingBar.getRating();
				
				spot.setName(m_editName.getText().toString());
				spot.setDescription(m_editDescription.getText().toString());
				spot.setLatitude(params[0].latitude);
				spot.setLongitude(params[0].longitude);
				spot.setTotalNote(note);
				spot.setType(m_type);
				
				response = service.insertSpots(spot).execute();
				
			} catch (Exception e){
				Log.d("impossible d'ajouter le spot", e.getMessage(), e);//TODO getressource
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Spots spot) {
			m_progressDialog.dismiss();

			if(spot != null){
				finish();
			} else {
				Toast.makeText(getBaseContext(), "Le Spot n'a pas été ajouté!", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_spot_annuler:
			finish();
			break;
		case R.id.add_spot_valider: 
			//TODO Vérif tout renseigné
			LatLng params = new LatLng(m_spot.getPosition().latitude, m_spot.getPosition().longitude);
			new AddSpot(this).execute(params);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int choice;
		switch (buttonView.getId()) {
		case R.id.add_spot_roller:
			choice = Spot.ROLLER;
			break;
		case R.id.add_spot_bmx:
			choice = Spot.BMX;
			break;
		case R.id.add_spot_skate:
			choice = Spot.SKATE;
			break;
		case R.id.add_spot_skatepark:
			choice = Spot.SKATEPARK;
			break;
		default:
			choice = 0;
			break;
		}
		
		if(isChecked){
			m_type  += choice;
		} else {
			m_type -= choice;
		}
	}
	
}
