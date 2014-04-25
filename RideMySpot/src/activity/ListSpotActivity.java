package activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Spot;
import adapter.ListSpot;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import com.w3m.ridemyspot.R;
import comparator.SpotComparator;

import database.SQLiteSpot;

public class ListSpotActivity extends ActionBarActivity implements OnItemClickListener, LocationListener{
	
	private SQLiteSpot mDatabaseSpot;
	private List<Spot> mSpot;
	
	private LocationManager mLocationManager;
	private Location mLocation;
	
	private ListView mListView;
	private PopupWindow mPopupWindow;
	
	private ListView mListViewSort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_spot);

		//Location Initialization
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		
		//Retrieve spot from local database
		mDatabaseSpot = new SQLiteSpot(this);
		mSpot = new ArrayList<Spot>();
		mDatabaseSpot.OpenDB();
		mSpot = mDatabaseSpot.getListSpot();
		mDatabaseSpot.CloseDB();

		Collections.sort(mSpot, new SpotComparator(SpotComparator.COMPARE_BY_NAME, mLocation));
		ListSpot adapter = new ListSpot(this, mSpot, mLocation);
		mListView = (ListView) findViewById(R.id.listview_spot);
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
		int index = 0;
		switch (item.getItemId()) {
		case R.id.menu_map:
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
		Collections.sort(mSpot, new SpotComparator(index, mLocation));
		ListSpot adapter = new ListSpot(this, mSpot, mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
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
				if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
					mPopupWindow.dismiss();
				}
				return false;
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
			intent.putExtra("spot", mSpot.get(position));
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
