package activity;

import java.util.ArrayList;
import java.util.List;

import taskLoader.CommentTaskLoader;
import model.Comment;
import model.Spot;
import account.SessionManager;
import adapter.ListComment;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.w3m.ridemyspot.R;

import database.SQLiteSpot;
import entity.Rmsendpoint;
import entity.model.CollectionResponseComments;
import entity.model.Comments;

public class SpotActivity extends ActionBarActivity implements OnItemClickListener, OnClickListener, android.view.View.OnClickListener, LoaderManager.LoaderCallbacks<List<Comment>>{

	private static final int NB_VOTE_SCORE_MIN = 4;
	
	private SessionManager mSessionManager;
	
	private Spot mSpot;
	private ArrayList<Comment> mComments;

	private ListView mListComment;
	private EditText mDialogCom;
	private RatingBar mDialogRate;

	private ImageButton mVotePlus;
	private ImageButton mVoteMoins;
	
	private boolean mFav;
	
	private String mIdUser;
	
	private View mPopupView;
	private PopupWindow mPopupWindow;

	private SQLiteSpot mDatabaseSpot;
	
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
		mSpot = intent.getParcelableExtra("spot");
		mFav = mSpot.isFavorite();
		
		((RatingBar) findViewById(R.id.spot_globalnote)).setRating(mSpot.getGlobalNote());

		((TextView) findViewById(R.id.spot_text_name)).setText(mSpot.getName());
		((TextView) findViewById(R.id.spot_text_adress)).setText(mSpot.getAdress());
		((TextView) findViewById(R.id.spot_text_desciption)).setText(mSpot.getDescription());

		mListComment = (ListView) findViewById(R.id.spot_list_comment);
		mListComment.setEmptyView(findViewById(R.id.spot_loading));
		mListComment.setOnItemClickListener(this);

		mSessionManager = new SessionManager(this);
		mIdUser = mSessionManager.getUserDetails().get(SessionManager.KEY_ID);

		getSupportActionBar().setTitle(mSpot.getStringTypes().toString());
		//new ListComments(this).execute();
		Bundle bundle = new Bundle();
		bundle.putLong(CommentTaskLoader.ID_SPOT, mSpot.getID());
		//getLoaderManager().initLoader(0, bundle, this);
		
		mDatabaseSpot = new SQLiteSpot(this);
	}
	
	private void populateComment() {
		//mComments = new ArrayList<Comment>();
		ListComment listComment = new ListComment(this, mComments);
		mListComment.setAdapter(listComment);
	}

	
	public void showPopup(MenuItem menuItem){
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
		mPopupWindow.setContentView(mPopupView);
		if(getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT){
			mPopupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, getSupportActionBar().getHeight());
		} else {
			mPopupWindow.showAsDropDown(view, 0, 0);
		}
		
		if(!mSessionManager.getChkVote()){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final View alertView = LayoutInflater.from(this).inflate(R.layout.info_vote, null);

			CheckBox chkBox = (CheckBox) alertView.findViewById(R.id.info_vote_checkbox);
			if(mSessionManager.getNbChkVote() < NB_VOTE_SCORE_MIN){
				chkBox.setVisibility(View.GONE);
			}
			mSessionManager.putNbChkVote(mSessionManager.getNbChkVote()+1);
			chkBox.setOnClickListener(SpotActivity.this);
			
			builder.setTitle("Information");
			builder.setView(alertView);
			builder.setNegativeButton("Valider", this);
			
			builder.create().show();
		}
	}
	
	//TODO Enlever les deprecated
	//http://stackoverflow.com/questions/2795833/check-orientation-on-android-phone
	@SuppressWarnings("deprecation")
	public int getScreenOrientation()
	{
	    Display getOrient = getWindowManager().getDefaultDisplay();
	    int orientation = Configuration.ORIENTATION_UNDEFINED;
	    if(getOrient.getWidth()==getOrient.getHeight()){
	        orientation = Configuration.ORIENTATION_SQUARE;
	    } else{ 
	        if(getOrient.getWidth() < getOrient.getHeight()){
	            orientation = Configuration.ORIENTATION_PORTRAIT;
	        }else { 
	             orientation = Configuration.ORIENTATION_LANDSCAPE;
	        }
	    }
	    return orientation;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopupView = inflater.inflate(R.layout.vote_popup, null, false);
		
		mVotePlus = (ImageButton) mPopupView.findViewById(R.id.vote_plus);
		mVoteMoins = (ImageButton) mPopupView.findViewById(R.id.vote_moins);
		mVotePlus.setOnClickListener(this);
		mVoteMoins.setOnClickListener(this);
		
		if(mFav)
			menu.findItem(R.id.menu_fav).setIcon(R.drawable.heart_full);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_spot, menu);	
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
		//add or delete from fav if changed for less call to online API
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
			if(mFav){
				item.setIcon(R.drawable.heart_empty);
				new RemoveFavorite().execute();
				mFav = false;
			} else {
				item.setIcon(R.drawable.heart_full);
				new AddFavorite().execute();
				mFav = true;
			}
			break;
		case R.id.menu_nav:
			LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			Location userLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			
			String uri = "http://maps.google.com/maps?" +
					"saddr="+(userLocation.getLatitude())+","+(userLocation.getLongitude())+
					"&daddr="+mSpot.getPosition_lat()+","+mSpot.getPosition_long();
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
			break;
		case R.id.menu_stview:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse ("google.streetview:cbll=" 
					+ mSpot.getPosition_lat() + "," + mSpot.getPosition_long() + 
					"&cbp=1,180,,0,1.0")); 
			startActivity(intent);
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public Spot getSpot() {
		return mSpot;
	}

	public void setSpot(Spot spot) {
		this.mSpot = spot;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			if(parent.getAdapter().getCount()-1 != position)
				return;
		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final View alertView = LayoutInflater.from(this).inflate(R.layout.add_comment, null);
	
			builder.setTitle(mSessionManager.getUserDetails().get(SessionManager.KEY_NAME));
			builder.setView(alertView);
			builder.setPositiveButton("Valider", this);
			builder.setNegativeButton("Annuler", this);
			
			mDialogCom = (EditText) alertView.findViewById(R.id.add_comment_text);
			mDialogRate = (RatingBar) alertView.findViewById(R.id.add_comment_rate);
			
			builder.create().show();
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
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.vote_plus:
		case R.id.vote_moins:
			
			if(!mSpot.isHasScore()){
				boolean vote = false;
				if(view.getId() == R.id.vote_plus)
					vote = true;
				new AddScore().execute(vote);
				mPopupWindow.dismiss();
			} else {
				Toast.makeText(this, "Vous avez déjà votez pour ce spot!", Toast.LENGTH_SHORT).show();
			}
			
			break;

		case R.id.info_vote_checkbox:
			if(((CheckBox) view).isChecked()){
				mSessionManager.putChkVote(true);
			} else {
				mSessionManager.putChkVote(false);
			}
			break;
		default:
			break;
		}
	}
	
//	private class ListComments extends AsyncTask<Void, Void, CollectionResponseComments>{
//		private Context mContext;
//		
//		public ListComments (Context context){
//			mContext = context;
//		}
//		
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//		}
//		
//		@Override
//		protected CollectionResponseComments doInBackground(Void... params) {
//			CollectionResponseComments comments = null;
//			try{
//				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
//				Rmsendpoint service = builder.build();
//				comments = service.listComments().setPIdSpot(mSpot.getID()).execute();
//			} catch (Exception e){
//				Log.d("impossible de récupérer les commmentaires", e.getMessage(), e);//TODO getressource
//				Toast.makeText(mContext, "Un problème c'est produit avec le chargement des commentaires. Nouveau chargement en cours !", Toast.LENGTH_SHORT).show();
//				new ListComments(mContext).execute(); //TODO: a voir si pose pas de problème sinon reste sur return null
//				//return null;
//			}
//			return comments;
//		}
//		
//		@Override
//		protected void onPostExecute(CollectionResponseComments comments) {
//			super.onPostExecute(comments);
//			if(mComments == null){
//				mComments = new ArrayList<Comment>();
//			} else {
//				mComments.clear();
//			}
//			if(comments != null){
//		        List<Comments> _list = comments.getItems();
//			    if(_list != null){
//			        for (Comments comment : _list) {
//			        	Comment item = new Comment(
//			        			comment.getIdSpot(),
//			        			comment.getIdUser(),
//			        			comment.getUser(),
//			        			comment.getText(),
//			        			comment.getNote()
//			        			);
//			        	mComments.add(item);
//			        }
//		        }
//			}
//		    populateComment();
//		}
//	}
		
	private class AddComment extends AsyncTask<Void, Void, Comments>{
		private Context mContext;
		private ProgressDialog mProgressDialog;
		
		public AddComment(Context context){
			this.mContext = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Ajout du commentaire..."); //TODO getressource
			mProgressDialog.show();
		}
		
		@Override
		protected Comments doInBackground(Void... params) {
			Comments response = null;
			try{
				
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				
				Comments comment = new Comments();
				
				comment.setIdSpot(mSpot.getID());
				comment.setIdUser(Long.parseLong(mIdUser));
				comment.setNote(mDialogRate.getRating());
				comment.setText(mDialogCom.getText().toString());
				
				response = service.insertComments(comment).execute();
				
			} catch (Exception e){
				Log.d("impossible d'ajouter le commentaire", e.getMessage(), e);//TODO getressource
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Comments comment) {
			mProgressDialog.dismiss();

			if(comment != null){
				mComments.add(
					new Comment(
						comment.getIdSpot(), 
						comment.getIdUser(),
						mSessionManager.getUserDetails().get(SessionManager.KEY_NAME), //Non renvoyer par le serveur au moment de l'ajout!
						comment.getText(), 
						comment.getNote())
				);
				mSpot.setTotalNote(mSpot.getTotalNote()+comment.getNote());
				mSpot.setNbNote(mSpot.getNbNote()+1);
				mDatabaseSpot.OpenDB();
				mDatabaseSpot.updateSpot(mSpot);
				mDatabaseSpot.CloseDB();
				((RatingBar) findViewById(R.id.spot_globalnote)).setRating(mSpot.getGlobalNote());
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
						Long.parseLong(mIdUser),
						String.valueOf(mSpot.getID())).execute();
				
			} catch (Exception e){
				Log.d("impossible d'ajouter le favori", e.getMessage(), e);//TODO getressource
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSpot.setFavorite(true);
			mDatabaseSpot.OpenDB();
			mDatabaseSpot.updateSpot(mSpot);
			mDatabaseSpot.CloseDB();
		}
	}
	
	private class RemoveFavorite extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				service.removeFavorite(
						Long.parseLong(mIdUser),
						String.valueOf(mSpot.getID())).execute();
				
			} catch (Exception e){
				Log.d("impossible de supprimer le favori", e.getMessage(), e);//TODO getressource
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSpot.setFavorite(false);
			mDatabaseSpot.OpenDB();
			mDatabaseSpot.updateSpot(mSpot);
			mDatabaseSpot.CloseDB();
		}
	}
	
	private class AddScore extends AsyncTask<Boolean, Void, Void>{
		
		@Override
		protected Void doInBackground(Boolean... params) {
			try {
			Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
			Rmsendpoint service = builder.build();
				service.addScore(
						mSpot.getID(),
						mIdUser,
						params[0]).execute();
			} catch (Exception e) {
				Log.d("impossible d'ajouter le score", e.getMessage(), e);//TODO getressource
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSpot.setHasScore(true);
			mDatabaseSpot.OpenDB();
			mDatabaseSpot.updateSpot(mSpot);
			mDatabaseSpot.CloseDB();
		}
	}

	@Override
	public Loader<List<Comment>> onCreateLoader(int id, Bundle bundle) {
		return new CommentTaskLoader(this, bundle);
	}

	@Override
	public void onLoadFinished(Loader<List<Comment>> loader, List<Comment> listComment) {
		mComments.addAll(listComment);
		populateComment();
	}

	@Override
	public void onLoaderReset(Loader<List<Comment>> loader) {
		// TODO Auto-generated method stub
		
	}

	
}
