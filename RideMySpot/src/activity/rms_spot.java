package activity;

import java.util.ArrayList;

import com.w3m.ridemyspot.R;

import model.Comment;
import model.Spot;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

public class rms_spot extends ActionBarActivity{

	private Spot m_spot;
	private ArrayList<Comment> m_comment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot);
	
		m_spot = getIntent().getParcelableExtra("spot");
		
		getComment();
		RatingBar note = (RatingBar) findViewById(R.id.spot_globalnote);

		((TextView) findViewById(R.id.spot_text_name)).setText(m_spot.getName());
		((TextView) findViewById(R.id.spot_text_adress)).setText(m_spot.getAdress());
		((TextView) findViewById(R.id.spot_text_desciption)).setText(m_spot.getDescription());

		getSupportActionBar().setTitle(m_spot.getStringTypes().toString());
	}
	
	private void getComment() {
		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//icone favoris (ici ou oncreate de rms_spot)
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_spot, menu);	
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_nav:
			String uri = "http://maps.google.com/maps?" +
					"saddr="+(m_spot.getPosition_lat()-0.1)+","+(m_spot.getPosition_long())+
					"&daddr="+m_spot.getPosition_lat()+","+m_spot.getPosition_long();
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
			return true;
		case R.id.menu_stview:
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse ("google.streetview:cbll=" 
					+ m_spot.getPosition_lat() + 
					"," 
					+ m_spot.getPosition_long() + 
					"&cbp=1,180,,0,1.0")); 
			startActivity(myIntent); 
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public Spot getSpot() {
		return m_spot;
	}

	public void setSpot(Spot spot) {
		this.m_spot = spot;
	}
	
}
