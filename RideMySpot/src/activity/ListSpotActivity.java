package activity;

import java.util.ArrayList;
import java.util.List;

import model.Spot;
import adapter.ListSpot;
import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.w3m.ridemyspot.R;

import database.SQLiteSpot;

public class ListSpotActivity extends ActionBarActivity implements OnItemClickListener, LocationListener{
	
	private SQLiteSpot mDatabaseSpot;
	private List<Spot> mSpot;
	
	private LocationManager mLocationManager;
	
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_spot);

		//Location Initialization
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		//Retrieve spot from local database
		mDatabaseSpot = new SQLiteSpot(this);
		mSpot = new ArrayList<Spot>();
		mDatabaseSpot.OpenDB();
		mSpot = mDatabaseSpot.getListSpot();
		mDatabaseSpot.CloseDB();
		
		
		
		ListSpot adapter = new ListSpot(this, mSpot, mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
		mListView = (ListView) findViewById(R.id.listview);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
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
		getMenuInflater().inflate(R.menu.menu_list, menu);	
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_map:
			Intent intent = new Intent(ListSpotActivity.this, MapActivity.class);
			startActivity(intent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, SpotActivity.class);
		intent.putExtra("spot", mSpot.get(position));
		startActivity(intent);		
	}

	@Override
	public void onLocationChanged(Location location) {
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onProviderDisabled(String provider) {
	}
	

}
