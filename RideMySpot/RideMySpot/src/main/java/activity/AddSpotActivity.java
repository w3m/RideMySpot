package activity;


import model.Spot;
import account.SessionManager;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
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

import database.SQLiteSpot;
import entity.Rmsendpoint;
import entity.model.Spots;

public class AddSpotActivity extends FragmentActivity implements OnTouchListener, OnClickListener, OnCheckedChangeListener{
	
	public final static String EXTRA_POSITION = "POSITION";
	
	private GoogleMap mMap;
	
	private ScrollView mScrollView;
	private ImageView mTransparentImage;
	
	private Button mValidate;
	private Button mCancel;

	private CheckBox mCheckRoller;
	private CheckBox mCheckBmx;
	private CheckBox mCheckSkate;
	private CheckBox mCheckSkatepark;
	
	private RatingBar mRatingBar;

	private EditText mEditName;
	private EditText mEditDescription;
	
	private Marker mMarkerSpot;
	private int mType = 0;

	private SessionManager mSessionManager;
	private String mIdUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_spot);
		
		initializeView();
		
		Intent intent = getIntent();
		LatLng position = (LatLng) intent.getExtras().get(EXTRA_POSITION);
		
		mMarkerSpot = mMap.addMarker(new MarkerOptions()
        .position(position)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map)));
		mMarkerSpot.setDraggable(true);
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
		
		mTransparentImage.setOnTouchListener(this);
		
		mSessionManager = new SessionManager(this);
		mIdUser = mSessionManager.getUserDetails().get(SessionManager.KEY_ID);
	}

	private void initializeView() {
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_spot_map)).getMap();
		mScrollView = (ScrollView) findViewById(R.id.add_spot_scrollview);
		mTransparentImage = (ImageView) findViewById(R.id.add_spot_transparent_image);

		mValidate = (Button) findViewById(R.id.add_spot_valider);
		mCancel = (Button) findViewById(R.id.add_spot_annuler);
		mValidate.setOnClickListener(this);
		mCancel.setOnClickListener(this);

		mCheckRoller = (CheckBox) findViewById(R.id.add_spot_roller);
		mCheckBmx = (CheckBox) findViewById(R.id.add_spot_bmx);
		mCheckSkate = (CheckBox) findViewById(R.id.add_spot_skate);
		mCheckSkatepark = (CheckBox) findViewById(R.id.add_spot_skatepark);
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
				v.performClick();
				return true;
			case MotionEvent.ACTION_MOVE:
				mScrollView.requestDisallowInterceptTouchEvent(true);
				return false;
			default: 
				return true;
		} 
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_spot_annuler:
			finish();
			break;
		case R.id.add_spot_valider: 
			String check = "";
			if(mEditName.getText().toString().length() < 10 ){
				check += getString(R.string.add_spot_minimum_name);
			}
			if(mType == 0 ){
				check += getString(R.string.add_spot_minimum_type);
			} 
			if(mEditDescription.getText().toString().length() < 20){
				check += getString(R.string.add_spot_minimum_description);
			} 
			if (mRatingBar.getRating() == 0 ){
				check += getString(R.string.add_spot_minimum_rating);
			}
			if(!"".equals(check)){
				Toast.makeText(this, getString(R.string.add_spot_minimum_error_text) + check, Toast.LENGTH_LONG).show();
			} else {
				LatLng params = new LatLng(mMarkerSpot.getPosition().latitude, mMarkerSpot.getPosition().longitude);
				new AddSpot(this).execute(params);
			}
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
			mProgressDialog.setMessage(getString(R.string.add_spot_loading));
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
				
				spot.setIdUser(Long.parseLong(mIdUser));
				spot.setName(mEditName.getText().toString());
				spot.setDescription(mEditDescription.getText().toString());
				spot.setLatitude(params[0].latitude);
				spot.setLongitude(params[0].longitude);
				spot.setTotalNote(note);
				spot.setType(mType);
				
				response = service.insertSpots(spot).execute();
				
			} catch (Exception e){
				Log.d(getString(R.string.add_spot_loading_error_log), e.getMessage(), e);
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Spots spot) {
			mProgressDialog.dismiss();

			if(spot != null){
				SQLiteSpot databaseSpot = new SQLiteSpot(mContext);
				databaseSpot.OpenDB();
				databaseSpot.insertEntitySpots(spot);
				databaseSpot.CloseDB();
				finish();
			} else {
				Toast.makeText(getBaseContext(), getString(R.string.add_spot_loading_error), Toast.LENGTH_LONG).show();
			}
		}
	}
	
}
