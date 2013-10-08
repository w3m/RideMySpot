package activity;

import java.util.ArrayList;
import java.util.Arrays;

import model.MultiSpinner;
import model.MultiSpinner.MultiSpinnerListener;
import model.Spot;
import adapter.Info_spot;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.w3m.ridemyspot.R;

public class rms_maps extends ActionBarActivity implements LocationListener, OnMapLongClickListener, OnMarkerClickListener, MultiSpinnerListener, OnClickListener, OnInfoWindowClickListener{

	private GoogleMap m_map;
	private LocationManager m_locationManager;

	private Marker addSpot;
	private Marker user;
	
	private MultiSpinner multiSpinner;
	
	public ArrayList<Spot> m_spot = new ArrayList<Spot>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		
		//Maps Initialization
		m_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		m_map.setOnMapLongClickListener(this);
		m_map.setOnMarkerClickListener(this);
		m_map.setOnInfoWindowClickListener(this);
		

		//Location Initialization
		m_locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		//Filter Initialization
		ArrayList<String> Liste = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.maps_filter_list)));
		multiSpinner = (MultiSpinner) findViewById(R.id.multi_spinner);
		multiSpinner.setItems(Liste, getResources().getString(R.string.text_all_spot), this);
		
		findViewById(R.id.map_location).setOnClickListener(this);

		Spot spot;
		spot = new Spot("Ile de la Cité","17 Rue Chanoinesse","REAL Sample spot for test",48.853717,2.350277,(Spot.ROLLER |Spot.BMX), (float) 3.5, 5);
		m_spot.add(spot);
		
		m_map.setInfoWindowAdapter(new Info_spot(this, spot));
		
		populateMap();
	}
	
	private void populateMap() {
		m_map.clear();
		
		ArrayList<String> type = new ArrayList<String>(Arrays.asList(multiSpinner.getSelectedItem().toString().split(", ")));
		if(type.contains(getResources().getString(R.string.text_all_spot)))
			type = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.maps_filter_list)));
			
		for (Spot spot : m_spot) {
			if(containsAny(type, spot.getStringTypes()))
				m_map.addMarker(new MarkerOptions()
	    			.position(spot.getPosition())
	    			.title(spot.getName())
	    			.snippet(String.valueOf(spot.getGlobalNote()))
	    			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map))
				);
		}
		m_locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location userLocation = m_locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		
		user = m_map.addMarker(new MarkerOptions()
	    	.position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
	    	.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
	    	.snippet("user")
	    );
	}
	
	private boolean containsAny(ArrayList<String> type, ArrayList<String> stringTypes) {
		for (String text : type){
			if(stringTypes.contains(text))
				return true;
		}
		return false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
	
	
	private void view_spot(Marker marker) {
		Intent intent = new Intent(rms_maps.this, rms_spot.class);
		intent.putExtra("spot", m_spot.get(0));
		startActivity(intent);
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
		if(marker.getTitle().equalsIgnoreCase("user"))
			return;
		view_spot(marker);
	}

	

	
}


