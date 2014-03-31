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

public class AddSpotActivity extends FragmentActivity implements OnTouchListener, OnClickListener, OnCheckedChangeListener{
	
	private GoogleMap mMap;
	
	private ScrollView mScrollView;
	private ImageView mTransparentImage;
	
	private Button mValidate;
	private Button mCancel;

	private RadioButton mCheckRoller;
	private RadioButton mCheckBmx;
	private RadioButton mCheckSkate;
	private RadioButton mCheckSkatepark;
	
	private RatingBar mRatingBar;

	private EditText mEditName;
	private EditText mEditDescription;
	
	private Marker mSpot;
	private int mType = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_spot);
		
		initializeView();
		
		Intent intent = getIntent();
		LatLng position = (LatLng) intent.getExtras().get("position");
		
		mSpot = mMap.addMarker(new MarkerOptions()
        .position(position)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map)));
		mSpot.setDraggable(true);
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
		
		mTransparentImage.setOnTouchListener(this);
		
	}

	private void initializeView() {
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_spot_map)).getMap();
		mScrollView = (ScrollView) findViewById(R.id.add_spot_scrollview);
		mTransparentImage = (ImageView) findViewById(R.id.add_spot_transparent_image);

		mValidate = (Button) findViewById(R.id.add_spot_valider);
		mCancel = (Button) findViewById(R.id.add_spot_annuler);
		mValidate.setOnClickListener(this);
		mCancel.setOnClickListener(this);

		mCheckRoller = (RadioButton) findViewById(R.id.add_spot_roller);
		mCheckBmx = (RadioButton) findViewById(R.id.add_spot_bmx);
		mCheckSkate = (RadioButton) findViewById(R.id.add_spot_skate);
		mCheckSkatepark = (RadioButton) findViewById(R.id.add_spot_skatepark);
		mCheckRoller.setOnCheckedChangeListener(this);
		mCheckBmx.setOnCheckedChangeListener(this);
		mCheckSkate.setOnCheckedChangeListener(this);
		mCheckSkatepark.setOnCheckedChangeListener(this);
		
		mRatingBar = (RatingBar) findViewById(R.id.add_spot_rating);

		mEditName = (EditText) findViewById(R.id.add_spot_edit_name);
		mEditDescription = (EditText) findViewById(R.id.add_spot_edit_description);
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
				mScrollView.requestDisallowInterceptTouchEvent(true);
				// Disable touch on transparent view
				return false;
			case MotionEvent.ACTION_UP:
				// Allow ScrollView to intercept touch events.
				mScrollView.requestDisallowInterceptTouchEvent(false);
				return true;
			case MotionEvent.ACTION_MOVE:
				mScrollView.requestDisallowInterceptTouchEvent(true);
				return false;
			default: 
				return true;
		} 
	}
	
	private class AddSpot extends AsyncTask<LatLng, Void, Spots>{
		private Context mContext;
		private ProgressDialog mProgressDialog;
		
		public AddSpot(Context context){
			this.mContext = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Ajout du spot..."); //TODO getressource
			mProgressDialog.show();
		}
		
		@Override
		protected Spots doInBackground(LatLng... params) {
			Spots response = null;
			try{
				
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				
				Spots spot = new Spots();
				
				float note = mRatingBar.getRating();
				
				spot.setName(mEditName.getText().toString());
				spot.setDescription(mEditDescription.getText().toString());
				spot.setLatitude(params[0].latitude);
				spot.setLongitude(params[0].longitude);
				spot.setTotalNote(note);
				spot.setType(mType);
				
				response = service.insertSpots(spot).execute();
				
			} catch (Exception e){
				Log.d("impossible d'ajouter le spot", e.getMessage(), e);//TODO getressource
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Spots spot) {
			mProgressDialog.dismiss();

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
			LatLng params = new LatLng(mSpot.getPosition().latitude, mSpot.getPosition().longitude);
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
			mType  += choice;
		} else {
			mType -= choice;
		}
	}
	
}
