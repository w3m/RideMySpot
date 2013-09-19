package activity;

import com.w3m.ridemyspot.R;

import model.Spot;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.RatingBar;
import android.widget.TextView;

public class rms_spot extends ActionBarActivity{

	private Spot m_spot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot);
	
		m_spot = getIntent().getParcelableExtra("spot");
		
		TextView name = (TextView) findViewById(R.id.spot_text_name);
		TextView adress = (TextView) findViewById(R.id.spot_text_adress);
		RatingBar note = (RatingBar) findViewById(R.id.spot_globalnote);
		
		TextView description = (TextView) findViewById(R.id.spot_text_desciption); 

		name.setText(m_spot.getName());
		adress.setText(m_spot.getAdress());
		description.setText(m_spot.getDescription());
		
		getSupportActionBar().setTitle(m_spot.getName());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_spot, menu);	
		return super.onCreateOptionsMenu(menu);
	}
	
	
	public Spot getSpot() {
		return m_spot;
	}

	public void setSpot(Spot spot) {
		this.m_spot = spot;
	}
	
	
	
}
