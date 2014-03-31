package activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.MultiSpinner;
import model.MultiSpinner.MultiSpinnerListener;
import model.Spot;
import account.SessionManager;
import adapter.Info_spot;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
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

import entity.Rmsendpoint;
import entity.model.CollectionResponseSpots;
import entity.model.Spots;

public class rms_maps extends ActionBarActivity implements LocationListener, OnMapLongClickListener, OnMarkerClickListener, MultiSpinnerListener, OnClickListener, OnInfoWindowClickListener{

	private GoogleMap m_map;
	private LocationManager m_locationManager;
	private SessionManager m_sessionManager;

	private Marker addSpot;
	private Marker user;
	
	private MultiSpinner multiSpinner;
	
	public List<Spot> m_spot = new ArrayList<Spot>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		
		//Maps Initialization
		m_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		m_map.setOnMapLongClickListener(this);
		m_map.setOnMarkerClickListener(this);
		m_map.setOnInfoWindowClickListener(this);
		m_map.setInfoWindowAdapter(new Info_spot(this));

		//Location Initialization
		m_locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		//Filter Initialization
		List<String> Liste = Arrays.asList(getResources().getStringArray(R.array.maps_filter_list));
		multiSpinner = (MultiSpinner) findViewById(R.id.multi_spinner);
		multiSpinner.setItems(Liste, getResources().getString(R.string.text_all_spot), this);
		
		findViewById(R.id.map_location).setOnClickListener(this);
		
		m_sessionManager = new SessionManager(this);
	}
	
	private void populateMap() {
		m_map.clear();    //On peut aussi jouer sur la visibilitÃ©... optimise le fais de pas avoir Ã  recrÃ©er les marker!!
							//Du coup un populatemap() pour tout les points et un filtermarker() pour le filtre (le faire sur le clicklistener du filtre!)
		
		List<String> type = Arrays.asList(multiSpinner.getSelectedItem().toString().split(", "));
		if(type.contains(getResources().getString(R.string.text_all_spot)))
			type = Arrays.asList(getResources().getStringArray(R.array.maps_filter_list));
			
		for (Spot spot : m_spot) {
			//If the spot is in the type scope
			if(containsAny(type, spot.getStringTypes(), spot.isFavorite())){
				//We add it to the map and retrieve his ID
				String markerID = m_map.addMarker(new MarkerOptions()
	    			.position(spot.getPosition())
	    			.title(spot.getName())
	    			.snippet(String.valueOf(spot.getGlobalNote()))
	    			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map))
				).getId();
				//We set to the spot the marker's ID to retrieve the connection when it will hit
				spot.setMarkerID(markerID);
			}
		}
		
		//Redraw user's last know location
		Location userLocation = m_locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		user = m_map.addMarker(new MarkerOptions()
	    	.position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
	    	.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
	    	.snippet("user")
	    );
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
		//m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	    m_locationManager.removeUpdates(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	    m_locationManager.removeUpdates(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		if(addSpot!=null)
			if(addSpot.isVisible())
				addSpot.remove();
		
		if(m_sessionManager.isNetworkAvailable())
			new ListSpots(this).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_maps, menu);	
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_list:
			//intent rms_list activity
			return true;
		case R.id.menu_add:
			//Get the user location to add the new spot here
			add_spot(m_map.getCameraPosition().target);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onMapLongClick(LatLng marker) {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(200);
		
		if(addSpot!=null)
			if(addSpot.isVisible())
				addSpot.remove();
			
		addSpot = m_map.addMarker(new MarkerOptions()
        .position(marker)
        .snippet("addSpot")
        .icon(BitmapDescriptorFactory
        .fromResource(R.drawable.map)));
		addSpot.setDraggable(true);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.hideInfoWindow();
		if(marker.equals(addSpot))
			add_spot(marker.getPosition());
		return false;
	}

	private void add_spot(LatLng position){
		Intent intent = new Intent(rms_maps.this, rms_add_spot.class);
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
		
		user = m_map.addMarker(new MarkerOptions()
        	.position(new LatLng(location.getLatitude(), location.getLongitude()))
        	.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
        	.snippet("user")
        );
		
		m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),14));
		//m_map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
	    m_locationManager.removeUpdates(this);
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
				m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			break;
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(rms_maps.this, rms_spot.class);
		
		int index = 0;
		for (Spot spot : m_spot){
			if(marker.getId().equalsIgnoreCase(spot.getMarkerID()))
				break;
			index++;
		}
		
		intent.putExtra("spot", m_spot.get(index));
		startActivity(intent);
	}
	
	private class ListSpots extends AsyncTask<Void, Void, CollectionResponseSpots>{
		private Context m_context;
		private ProgressDialog m_progressDialog;
		
		public ListSpots(Context context){
			this.m_context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			m_progressDialog = new ProgressDialog(m_context);
			m_progressDialog.setMessage("Récupération des spots..."); //TODO getressource
			m_progressDialog.show();
		}
		
		@Override
		protected CollectionResponseSpots doInBackground(Void... params) {
			CollectionResponseSpots spots = null;
			try{
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				spots = service.listSpots().setPIdUser(Long.parseLong(m_sessionManager.getUserDetails().get(SessionManager.KEY_ID))).execute();
			} catch (Exception e){
				Log.d("impossible de récupérer les spots", e.getMessage(), e);//TODO getressource
			}
			return spots;
		}
		
		@Override
		protected void onPostExecute(CollectionResponseSpots spots) {
			super.onPostExecute(spots);
			m_progressDialog.dismiss();

			if(spots != null && spots.getItems() != null){
				if(m_spot == null){
			        m_spot = new ArrayList<Spot>();
				} else {
					m_spot.clear();
				}
		        List<Spots> _list = spots.getItems();
			    if(_list != null){
			        for (Spots spot : _list) {
			        	if(spot != null){
				        	Spot item = new Spot(
				        			spot.getId(),
				        			spot.getName(),
				            		 "",
				            		 spot.getDescription(),
				            		 spot.getLatitude(),
				            		 spot.getLongitude(),
				            		 spot.getType(),
				            		 spot.getTotalNote(),
				            		 spot.getNbNote(),
				            		 spot.getFavorite(),
				            		 spot.getScore(),
				            		 spot.getHasScore()
				        			);
				        	m_spot.add(item);
			        	}
			        }
		        }
				populateMap();
			}
		}
	}

}


