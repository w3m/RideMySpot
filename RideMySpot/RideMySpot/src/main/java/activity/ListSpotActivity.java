package activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.w3m.ridemyspot.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapter.ListSpotAdapter;
import comparator.SpotComparator;
import database.SQLiteSpot;
import model.Spot;

public class ListSpotActivity extends AppCompatActivity implements OnItemClickListener, LocationListener{
	
	private SQLiteSpot mDatabaseSpot;
	private List<Spot> mListSpot;
	
	private LocationManager mLocationManager;
	private Location mLocation;
	
	private ListView mListView;
	private PopupWindow mPopupWindow;
	
	private ListView mListViewSort;
	
	private AdView mAdView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_spot);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.list_toolbar);
		setSupportActionBar(myToolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//Location Initialization
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		
		//Retrieve spot from local database
		mDatabaseSpot = new SQLiteSpot(this);
		mListSpot = new ArrayList<Spot>();
		mDatabaseSpot.OpenDB();
		mListSpot = mDatabaseSpot.getListSpot();
		mDatabaseSpot.CloseDB();

		Collections.sort(mListSpot, new SpotComparator(SpotComparator.COMPARE_BY_NAME, mLocation));
		ListSpotAdapter adapter = new ListSpotAdapter(this, mListSpot, mLocation);
		mListView = (ListView) findViewById(R.id.listview_spot);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
		
		// Recherchez AdView comme ressource et chargez une demande.
	    mAdView = (AdView)this.findViewById(R.id.listview_spot_adview);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    mAdView.loadAd(adRequest);
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
		mAdView.pause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mAdView.resume();
	}
	
	@Override
	protected void onDestroy() {
		mAdView.destroy();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_list, menu);	
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int index = 0;
		switch (item.getItemId()) {
			case R.id.menu_map:
			case android.R.id.home:
				Intent intent = new Intent(ListSpotActivity.this, MapActivity.class);
				startActivity(intent);
				finish();
				return true;
			case R.id.menu_sort_name:
				index = SpotComparator.COMPARE_BY_NAME;
				break;
			case R.id.menu_sort_note:
				index = SpotComparator.COMPARE_BY_NOTE;
				break;
			case R.id.menu_sort_nb_note:
				index = SpotComparator.COMPARE_BY_NB_NOTE;
				break;
			case R.id.menu_sort_distance:
				index = SpotComparator.COMPARE_BY_DISTANCE;
				break;
			case R.id.menu_sort_fav:
				index = SpotComparator.COMPARE_BY_FAV;
				break;
			default:
		}
		Collections.sort(mListSpot, new SpotComparator(index, mLocation));
		ListSpotAdapter adapter = new ListSpotAdapter(this, mListSpot, mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
		adapter.notifyDataSetChanged();
		mListView.setAdapter(adapter);
		mPopupWindow.dismiss();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	public void showSortList(MenuItem menuItem){
		View view = findViewById(menuItem.getItemId());
		mPopupWindow = new PopupWindow(this);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_OUTSIDE:
						mPopupWindow.dismiss();
						return false;
					case MotionEvent.ACTION_UP:
						v.performClick();
						return true;
					default: 
						return true;
				} 
			}
		});
		mPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setContentView(mListViewSort);
		mPopupWindow.showAsDropDown(view, 0, 0);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(parent.getId() == R.id.listview_spot){
			Intent intent = new Intent(this, SpotActivity.class);
			intent.putExtra(SpotActivity.EXTRA_SPOT, mListSpot.get(position));
			startActivity(intent);
		} else {
			
		}
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
