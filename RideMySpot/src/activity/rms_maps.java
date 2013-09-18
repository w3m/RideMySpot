package activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.w3m.ridemyspot.R;

public class rms_maps extends ActionBarActivity implements OnMapLongClickListener, OnMarkerClickListener{

	private GoogleMap m_map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		//Maps Initialization
		m_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		m_map.setOnMapLongClickListener(this);
		m_map.setOnMarkerClickListener(this);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_maps, menu);	
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_list:
			//intent rms_list
			return true;
		case R.id.menu_add:
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
		
		Marker spot = m_map.addMarker(new MarkerOptions()
        .position(marker)
        .title("Ajouter ce point?")
        .icon(BitmapDescriptorFactory
        .fromResource(R.drawable.ic_launcher)));
		spot.setDraggable(true);
		spot.showInfoWindow();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		add_spot(marker.getPosition());
		return false;
	}
	
	
	private void add_spot(LatLng position){
		//Get the user location to add the new spot here
		
		//Then launch add_spot activity
		Intent intent = new Intent(rms_maps.this, rms_add_spot.class);
		intent.putExtra("position", position);
		startActivity(intent);
		
	}

	

	
}


