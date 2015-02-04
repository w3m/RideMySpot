package activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.MultiSpinner;
import model.MultiSpinner.MultiSpinnerListener;
import model.Spot;
import account.SessionManager;
import adapter.InfoSpotAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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
import entity.model.CollectionResponseSpots;

public class MapActivity extends ActionBarActivity implements LocationListener, OnMapLongClickListener, OnMapClickListener, OnMarkerClickListener, MultiSpinnerListener, OnClickListener, OnInfoWindowClickListener{

	private GoogleMap mMap;
	private LocationManager mLocationManager;
	private SessionManager mSessionManager;

	private Marker addSpot;
	private Marker user;
	
	private MultiSpinner multiSpinner;
	private MenuItem mRefresh;
	
	private SQLiteSpot mDatabaseSpot;
	
	public List<Spot> mSpot = new ArrayList<Spot>();
	public HashMap<String, Spot> mHmSpot = new HashMap<String, Spot>();
	
	private AdView mAdView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		mSessionManager = new SessionManager(this);
		if(!mSessionManager.isLoggedIn()){
			Intent intent = new Intent(this, SplashScreenActivity.class);
		    startActivity(intent);
		}
		
		//Location Initialization
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

		//Maps Initialization
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setOnMapLongClickListener(this);
		mMap.setOnMapClickListener(this);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		
		//Filter Initialization
		List<String> Liste = Arrays.asList(getResources().getStringArray(R.array.maps_filter_list));
		multiSpinner = (MultiSpinner) findViewById(R.id.map_multi_spinner);
		multiSpinner.setItems(Liste, getResources().getString(R.string.text_all_spot), this);
		
		findViewById(R.id.map_location).setOnClickListener(this);

		mDatabaseSpot = new SQLiteSpot(this);
		
		// Recherchez AdView comme ressource et chargez une demande.
	    mAdView = (AdView)this.findViewById(R.id.map_adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    mAdView.loadAd(adRequest);
	}
	
	private void populateMap() {
		
		mDatabaseSpot.OpenDB();
		if(!mSpot.isEmpty()){
			mSpot.clear();
		}
		mSpot = mDatabaseSpot.getListSpot();
		mDatabaseSpot.CloseDB();
		
		mMap.clear();    //On peut aussi jouer sur la visibilité... optimise le fais de pas avoir à recréer les marker!!
							//Du coup un populatemap() pour tout les points et un filtermarker() pour le filtre (le faire sur le clicklistener du filtre!)
		
		List<String> type = Arrays.asList(multiSpinner.getSelectedItem().toString().split(", "));
		if(type.contains(getResources().getString(R.string.text_all_spot))){
			type = Arrays.asList(getResources().getStringArray(R.array.maps_filter_list));
		}
			
		if(!mHmSpot.isEmpty()){
			mHmSpot.clear();
		}
		
		for (Spot spot : mSpot) {
			//If the spot is in the type scope
			if(containsAny(type, spot.getStringTypes(), spot.isFavorite())){
				//We add it to the map and retrieve his ID
				String markerID = mMap.addMarker(new MarkerOptions()
	    			.position(spot.getPosition())
	    			.title(spot.getName())
	    			.snippet(String.valueOf(spot.getID()))
	    			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map))
				).getId();
				//We set to the spot the marker's ID to retrieve the connection when it will hit
				mHmSpot.put(markerID,spot);
			}
		}
		
		//Redraw user's last know location
		Location userLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		if(userLocation != null){
			user = mMap.addMarker(new MarkerOptions()
		    	.position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
		    	.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
		    );
		}
		

		mMap.setInfoWindowAdapter(new InfoSpotAdapter(this, mHmSpot));
	}
	
	private boolean containsAny(List<String> type, List<String> stringTypes, boolean fav) {
		for (String text : type){
			if(stringTypes.contains(text) || fav)
				return true;
		}
		return false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		//mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	
	@Override
	protected void onStop() {
	    mLocationManager.removeUpdates(this);
		super.onStop();
	}
	
	@Override
	protected void onPause() {
	    mLocationManager.removeUpdates(this);
	    mAdView.pause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		removeExistingAddSpot();
		populateMap();
		mAdView.resume();
	}

	@Override
	protected void onDestroy() {
		mAdView.destroy();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_maps, menu);	
		mRefresh = (MenuItem) menu.findItem(R.id.menu_refresh_spot);
		return super.onCreateOptionsMenu(menu);
	}
	
//	@Override   //TODO faire laoding au d�marrage et faire tourner le mrefresh!!
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		new ListSpots(this).execute();
//		return super.onPrepareOptionsMenu(menu);
//	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh_spot:
			new ListSpots(this).execute();
			return true;
		case R.id.menu_list:
			Intent intent = new Intent(MapActivity.this, ListSpotActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_add:
			//Get the user location to add the new spot here
			add_spot(mMap.getCameraPosition().target);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onMapLongClick(LatLng marker) {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(200);
		removeExistingAddSpot();
			
		addSpot = mMap.addMarker(new MarkerOptions()
        .position(marker)
        .icon(BitmapDescriptorFactory
        .fromResource(R.drawable.map)));
		addSpot.setDraggable(true);
	}
	
	@Override
	public void onMapClick(LatLng point) {
		removeExistingAddSpot();				
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.hideInfoWindow();
		if(marker.equals(addSpot))
			add_spot(marker.getPosition());
		else
			removeExistingAddSpot();
		return false;
	}

	private void add_spot(LatLng position){
		Intent intent = new Intent(MapActivity.this, AddSpotActivity.class);
		intent.putExtra("position", position);
		startActivity(intent);
	}

	@Override
	public void onItemsSelected(boolean[] selected) {
		populateMap();
	}

	@Override
	public void onLocationChanged(Location location) {
		if(user!=null)
			if(user.isVisible())
				user.remove();
		
		user = mMap.addMarker(new MarkerOptions()
        	.position(new LatLng(location.getLatitude(), location.getLongitude()))
        	.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
        );
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),14));
		//mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
	    mLocationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.map_location:
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			break;
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(MapActivity.this, SpotActivity.class);
		intent.putExtra("spot", mHmSpot.get(marker.getId()));
		startActivity(intent);
	}
	
	public void removeExistingAddSpot(){
		if(addSpot!=null)
			if(addSpot.isVisible())
				addSpot.remove();
	}
	
	private class ListSpots extends AsyncTask<Void, Void, CollectionResponseSpots>{
		private Context m_context;
		
		public ListSpots(Context context){
			m_context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mRefresh != null){
				((AnimationDrawable)mRefresh.getIcon()).start();
			}
		}
		
		@Override
		protected CollectionResponseSpots doInBackground(Void... params) {
			CollectionResponseSpots spots = null;
			try{
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				spots = service.listSpots().setPIdUser(Long.parseLong(mSessionManager.getUserDetails().get(SessionManager.KEY_ID))).execute();
			} catch (Exception e){
				Log.d("impossible de r�cup�rer les spots", e.getMessage(), e);//TODO getressource
			}
			return spots;
		}
		
		@Override
		protected void onPostExecute(CollectionResponseSpots spots) {
			super.onPostExecute(spots);

			if(spots != null && spots.getItems() != null){
			    
				mDatabaseSpot.OpenDB();
				mDatabaseSpot.insertListEntitySpots(spots.getItems());
				mDatabaseSpot.CloseDB();

				if(mRefresh != null){
					((AnimationDrawable)mRefresh.getIcon()).stop();
					mRefresh.setIcon(m_context.getResources().getDrawable(R.drawable.map_loader));
				}
				populateMap();
			}
		}
	}

}


