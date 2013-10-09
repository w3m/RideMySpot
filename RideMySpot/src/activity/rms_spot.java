package activity;

import java.util.ArrayList;

import com.w3m.ridemyspot.R;

import model.Comment;
import model.Spot;
import adapter.List_comment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class rms_spot extends ActionBarActivity implements OnItemClickListener, OnClickListener{

	private Spot m_spot;
	private ArrayList<Comment> m_comments;

	private Intent intent;
	
	private ListView m_listComment;
	private EditText m_dialog_com;
	private RatingBar m_dialog_rate;
	
	private boolean m_fav;
	
	
	/*Changement de rotation changer l'ordre 
	 * des layout pour les différents écrans
	 * ecran nexus 4 description en haut à droite
	 * split galery / comm'?...s
	*/
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot);
	
		Intent intent = getIntent();
		m_spot = intent.getParcelableExtra("spot");
		m_fav = intent.getBooleanExtra("fav", false);
		
		((RatingBar) findViewById(R.id.spot_globalnote)).setRating(m_spot.getGlobalNote());

		((TextView) findViewById(R.id.spot_text_name)).setText(m_spot.getName());
		((TextView) findViewById(R.id.spot_text_adress)).setText(m_spot.getAdress());
		((TextView) findViewById(R.id.spot_text_desciption)).setText(m_spot.getDescription());

		m_listComment = (ListView) findViewById(R.id.spot_list_comment);
		m_listComment.setEmptyView(findViewById(R.id.spot_loading));
		
		m_listComment.setOnItemClickListener(this);
		//((ListView) findViewById(R.id.spot_list_comment)).setOnItemClickListener(this);

		
		getComment();
		
		getSupportActionBar().setTitle(m_spot.getStringTypes().toString());
	}
	
	private void getComment() {

		m_comments = new ArrayList<Comment>();
		
		Comment test = new Comment(1,1,"w3m", "Ouais pas mal", 3.5);
		m_comments.add(test);
		Comment test_2 = new Comment(2,2,"zIz", "T'as gueule!", 1);
		m_comments.add(test_2);
		
		List_comment listComment = new List_comment(this, m_comments);
		m_listComment.setAdapter(listComment);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(m_fav)
			menu.findItem(R.id.menu_fav).setIcon(R.drawable.heart_full);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_spot, menu);	
		//menu.getItem(R.id.menu_fav);//
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//add or delete from fav if changed
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_fav:
			if(m_fav){
				item.setIcon(R.drawable.heart_empty);
				m_fav = false;
			} else {
				item.setIcon(R.drawable.heart_full);
				m_fav = true;
			}
			break;
		case R.id.menu_nav:
			LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			Location userLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			
			String uri = "http://maps.google.com/maps?" +
					"saddr="+(userLocation.getLatitude())+","+(userLocation.getLongitude())+
					"&daddr="+m_spot.getPosition_lat()+","+m_spot.getPosition_long();
			intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
			break;
		case R.id.menu_stview:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse ("google.streetview:cbll=" 
					+ m_spot.getPosition_lat() + "," + m_spot.getPosition_long() + 
					"&cbp=1,180,,0,1.0")); 
			startActivity(intent);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public Spot getSpot() {
		return m_spot;
	}

	public void setSpot(Spot spot) {
		this.m_spot = spot;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			if(parent.getAdapter().getCount()-1 != position)
				return;
		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog alertDialog;
			final View alertView = LayoutInflater.from(this).inflate(R.layout.add_comment, null);
	
			builder.setTitle("w3m");
			builder.setView(alertView);
			builder.setPositiveButton("Valider", this);
			builder.setNegativeButton("Annuler", this);
			
			m_dialog_com = (EditText) alertView.findViewById(R.id.add_comment_text);
			m_dialog_rate = (RatingBar) alertView.findViewById(R.id.add_comment_rate);
			
			alertDialog = builder.create();
			alertDialog.show(); 
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		//Add the comment to the server
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
				m_dialog_com.getText();
				m_dialog_rate.getRating();
			break;

		default:
			break;
		}
		
	}
	
}
