package activity;

import java.util.ArrayList;
import java.util.List;

import model.Comment;
import model.Spot;
import account.SessionManager;
import adapter.List_comment;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.w3m.ridemyspot.R;

import entity.Rmsendpoint;
import entity.model.CollectionResponseComments;
import entity.model.Comments;

public class rms_spot extends ActionBarActivity implements OnItemClickListener, OnClickListener{

	private SessionManager m_sessionManager;
	
	private Spot m_spot;
	private ArrayList<Comment> m_comments;

	private Intent intent;
	
	private ListView m_listComment;
	private EditText m_dialog_com;
	private RatingBar m_dialog_rate;
	
	private boolean m_fav;
	
	private String m_idUser;
	
	
	/*Changement de rotation changer l'ordre 
	 * des layout pour les diffÃ©rents Ã©crans
	 * ecran nexus 4 description en haut Ã  droite
	 * split galery / comm'?...s
	*/
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot);
	
		Intent intent = getIntent();
		m_spot = intent.getParcelableExtra("spot");
		m_fav = m_spot.isFavorite();
		
		((RatingBar) findViewById(R.id.spot_globalnote)).setRating(m_spot.getGlobalNote());

		((TextView) findViewById(R.id.spot_text_name)).setText(m_spot.getName());
		((TextView) findViewById(R.id.spot_text_adress)).setText(m_spot.getAdress());
		((TextView) findViewById(R.id.spot_text_desciption)).setText(m_spot.getDescription());

		m_listComment = (ListView) findViewById(R.id.spot_list_comment);
		m_listComment.setEmptyView(findViewById(R.id.spot_loading));
		
		m_listComment.setOnItemClickListener(this);
		
		m_sessionManager = new SessionManager(this);
		m_idUser = m_sessionManager.getUserDetails().get(SessionManager.KEY_ID);

		getSupportActionBar().setTitle(m_spot.getStringTypes().toString());
		new ListComments(this).execute();
	}
	
	private void populateComment() {
		//m_comments = new ArrayList<Comment>();
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
	public void onBackPressed() {
		super.onBackPressed();
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
				new RemoveFavorite().execute();
				m_fav = false;
			} else {
				item.setIcon(R.drawable.heart_full);
				new AddFavorite().execute();
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
	
			builder.setTitle(m_sessionManager.getUserDetails().get(SessionManager.KEY_NAME));
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
				new AddComment(this).execute();
			break;

		default:
			break;
		}
		
	}
	
	private class ListComments extends AsyncTask<Void, Void, CollectionResponseComments>{
		private Context m_context;
		
		public ListComments(Context context){
			this.m_context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected CollectionResponseComments doInBackground(Void... params) {
			CollectionResponseComments comments = null;
			try{
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				comments = service.listComments().setPIdSpot(m_spot.getID()).execute();
			} catch (Exception e){
				Log.d("impossible de récupérer les commmentaires", e.getMessage(), e);//TODO getressource
			}
			return comments;
		}
		
		@Override
		protected void onPostExecute(CollectionResponseComments comments) {
			super.onPostExecute(comments);

			if(m_comments == null){
				m_comments = new ArrayList<Comment>();
			} else {
				m_comments.clear();
			}
	        List<Comments> _list = comments.getItems();
		    if(_list != null){
		        for (Comments comment : _list) {
		        	Comment item = new Comment(
		        			comment.getIdSpot(),
		        			comment.getIdUser(),
		        			comment.getUser(),
		        			comment.getText(),
		        			comment.getNote()
		        			);
		        	m_comments.add(item);
					Log.d("###########", "" + comment.getUser());
		        }
	        }
		    populateComment();
		}
	}
		
	private class AddComment extends AsyncTask<Void, Void, Comments>{
		private Context m_context;
		private ProgressDialog m_progressDialog;
		
		public AddComment(Context context){
			this.m_context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			m_progressDialog = new ProgressDialog(m_context);
			m_progressDialog.setMessage("Ajout du spot..."); //TODO getressource
			m_progressDialog.show();
		}
		
		@Override
		protected Comments doInBackground(Void... params) {
			Comments response = null;
			try{
				
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				
				Comments comment = new Comments();
				
				comment.setIdSpot(m_spot.getID());
				comment.setIdUser(Long.parseLong(m_idUser));
				comment.setNote(m_dialog_rate.getRating());
				comment.setText(m_dialog_com.getText().toString());
				
				response = service.insertComments(comment).execute();
				
			} catch (Exception e){
				Log.d("impossible d'ajouter le commentaire", e.getMessage(), e);//TODO getressource
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Comments comment) {
			m_progressDialog.dismiss();

			if(comment != null){
				m_comments.add(
					new Comment(
						comment.getIdSpot(), 
						comment.getIdUser(),
						m_sessionManager.getUserDetails().get(SessionManager.KEY_NAME), //Non renvoyer par le serveur au moment de l'ajout!
						comment.getText(), 
						comment.getNote())
				);
				//TODO Mise à jour de la note global du spot creer asynctask pour récupération de la note?...
				populateComment();
				
				
			} else {
				Toast.makeText(getBaseContext(), "Le commentaire n'a pas été ajouté!", Toast.LENGTH_LONG).show();
			}
			
		}
	}

	
	private class AddFavorite extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				service.addFavorite(
						Long.parseLong(m_idUser),
						String.valueOf(m_spot.getID())).execute();
				
			} catch (Exception e){
				Log.d("impossible d'ajouter le favori", e.getMessage(), e);//TODO getressource
			}
			return null;
		}
	}
	
	private class RemoveFavorite extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				service.removeFavorite(
						Long.parseLong(m_idUser),
						String.valueOf(m_spot.getID())).execute();
				
			} catch (Exception e){
				Log.d("impossible de supprimer le favori", e.getMessage(), e);//TODO getressource
			}
			return null;
		}
	}
	
}
