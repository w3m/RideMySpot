package activity;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.w3m.ridemyspot.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ScrollView;

public class rms_add_spot extends FragmentActivity implements OnTouchListener{
	
	private GoogleMap m_map;
	
	private ScrollView m_scrollView;
	private ImageView m_transparentImage;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_spot);
		
		m_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_spot_map)).getMap();
		m_scrollView = (ScrollView) findViewById(R.id.add_spot_scrollview);
		m_transparentImage = (ImageView) findViewById(R.id.add_spot_transparent_image);
		
		Intent intent = getIntent();
		LatLng position = (LatLng) intent.getExtras().get("position");
		
		Marker spot = m_map.addMarker(new MarkerOptions()
        .position(position)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
		spot.setDraggable(true);
		
		m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
		m_map.animateCamera(CameraUpdateFactory.zoomTo(30), 2000, null);
		
		m_transparentImage.setOnTouchListener(this);
		
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
	
}
