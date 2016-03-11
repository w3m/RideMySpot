package activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Comment;
import model.Spot;
import account.SessionManager;
import adapter.ListCommentAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.w3m.ridemyspot.R;

import database.SQLiteSpot;
import entity.Rmsendpoint;
import entity.model.CollectionResponseComments;
import entity.model.Comments;

public class SpotActivity extends ActionBarActivity implements OnItemClickListener, OnItemLongClickListener, OnClickListener, android.view.View.OnClickListener{

	public final static String EXTRA_SPOT = "SPOT";
	private static final int NB_VOTE_SCORE_MIN = 5;
	
	private SessionManager mSessionManager;
	
	private Spot mSpot;
	private ArrayList<Comment> mListComment;

	private ListView mListViewComment;
	private EditText mDialogCom;
	private RatingBar mDialogRate;

	private ImageButton mVotePlus;
	private ImageButton mVoteMoins;
	
	private boolean mFav;
	
	private String mIdUser;
	
	//private View mPopupView;
	private PopupWindow mPopupWindow;

	private SQLiteSpot mDatabaseSpot;
	
	private AdView mAdView;

	private Toolbar mSpotToolbar;
	
	/*Changement de rotation changer l'ordre 
	 * des layout pour les differents ecrans
	 * ecran nexus 4 description en haut a droite
	 * split galery / comm'?...s
	*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot);

		mSpotToolbar = (Toolbar) findViewById(R.id.spot_toolbar);
		setSupportActionBar(mSpotToolbar);
	
		Intent intent = getIntent();
		mSpot = intent.getParcelableExtra(EXTRA_SPOT);
		mFav = mSpot.isFavorite();
		
		initializeView();

		mSessionManager = new SessionManager(this);
		mIdUser = mSessionManager.getUserDetails().get(SessionManager.KEY_ID);

		new ListComments(this).execute();

		mDatabaseSpot = new SQLiteSpot(this);
		
		// Recherchez AdView comme ressource et chargez une demande.
	    mAdView = (AdView)this.findViewById(R.id.spot_adview);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    mAdView.loadAd(adRequest);
	}
	
	private void initializeView(){
		((RatingBar) findViewById(R.id.spot_globalnote)).setRating(mSpot.getGlobalNote());
		((TextView) findViewById(R.id.spot_text_name)).setText(mSpot.getName());
		((TextView) findViewById(R.id.spot_text_type)).setText(mSpot.getStringTypes().toString());
		((TextView) findViewById(R.id.spot_text_desciption)).setText(mSpot.getDescription());

		getAdressFromLocation(mSpot.getPosition());
		
		mListViewComment = (ListView) findViewById(R.id.spot_list_comment);
		View headerView = new View(this);
		mListViewComment.addHeaderView(headerView);
		mListViewComment.setEmptyView(findViewById(R.id.spot_loading));
		mListViewComment.setOnItemClickListener(this);
		mListViewComment.setOnItemLongClickListener(this);
	}
	
	private void getAdressFromLocation(final LatLng locations){
		new Thread(new Runnable() {
			public void run() {
				LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				List<String>  providerList = locationManager.getAllProviders();
				if(null!=locations && null!=providerList && providerList.size()>0){
					double longitude = locations.longitude;
					double latitude = locations.latitude;
					Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
					try {
						final List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(null!=listAddresses && listAddresses.size()>0){
									((TextView) findViewById(R.id.spot_text_adress)).setText(listAddresses.get(0).getAddressLine(0));
								} else {
									findViewById(R.id.spot_text_adress).setVisibility(View.GONE);
								}
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void populateComment() {
		ListCommentAdapter listCommentAdapter = new ListCommentAdapter(this, mListComment);
		mListViewComment.setAdapter(listCommentAdapter);
	}

	@SuppressLint("RtlHardcoded")
	public void showPopup(MenuItem menuItem){
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
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
		

		View view = findViewById(menuItem.getItemId());
		if(getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT){
			mPopupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, getSupportActionBar().getHeight());
		} else {
			mPopupWindow.showAsDropDown(view, 0, 0);
		}
		
		if(!mSessionManager.getChkVote()){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final View alertView = new LinearLayout(this);
			LayoutInflater.from(this).inflate(R.layout.info_vote, (ViewGroup) alertView);

			CheckBox chkBox = (CheckBox) alertView.findViewById(R.id.info_vote_checkbox);
			if(mSessionManager.getNbChkVote() < NB_VOTE_SCORE_MIN){
				chkBox.setVisibility(View.GONE);
			}
			mSessionManager.putNbChkVote(mSessionManager.getNbChkVote()+1);
			chkBox.setOnClickListener(this);
			
			builder.setTitle(getString(R.string.text_information));
			builder.setView(alertView);
			builder.setNegativeButton(getString(R.string.text_valider), this);

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
		View popupView = new LinearLayout(this);
		popupView = LayoutInflater.from(this).inflate(R.layout.vote_popup, (ViewGroup) popupView, false);
		 
		mVotePlus = (ImageButton) popupView.findViewById(R.id.vote_plus);
		mVoteMoins = (ImageButton) popupView.findViewById(R.id.vote_moins);
		mVotePlus.setOnClickListener(this);
		mVoteMoins.setOnClickListener(this);
		
		mPopupWindow = new PopupWindow(this);
		mPopupWindow.setContentView(popupView);
		
		if(mFav){
			menu.findItem(R.id.menu_fav).setIcon(R.drawable.heart_full);
		}
			
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
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View alertView = new LinearLayout(this);
		LayoutInflater.from(this).inflate(R.layout.add_comment, (ViewGroup) alertView);

		builder.setTitle(mSessionManager.getUserDetails().get(SessionManager.KEY_NAME));
		builder.setView(alertView);
		builder.setNegativeButton(R.string.text_annuler, null);

		mDialogCom = (EditText) alertView.findViewById(R.id.add_comment_text);
		mDialogRate = (RatingBar) alertView.findViewById(R.id.add_comment_rate);
		
		if(parent.getAdapter().getCount()-1 != position){
			if(mListComment.get(position-1).getID_User() == Long.parseLong(mIdUser)){
				builder.setPositiveButton(R.string.text_valider, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new UpdateComment(SpotActivity.this).execute(mListComment.get(position-1));
					}
				});
				
				mDialogCom.setText(mListComment.get(position-1).getText());
				mDialogRate.setRating(mListComment.get(position-1).getNote());
				
				builder.create().show();
			}
			return;
		}
		
		for(Comment comment : mListComment){
			if(comment.getID_User() == Long.parseLong(mIdUser)){
				Toast.makeText(this, getString(R.string.spot_already_comment_error), Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		builder.setPositiveButton(R.string.text_valider, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new AddComment(SpotActivity.this).execute();
			}
		});
		
		builder.create().show();
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		if(parent.getAdapter().getCount()-1 != position){
			if(mListComment.get(position-1).getID_User() == Long.parseLong(mIdUser)){
				
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle(getString(R.string.text_information));
				alertDialog.setMessage(getString(R.string.spot_comment_delete));
				alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.text_valider), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
				            dialog.dismiss();
							new RemoveComment(SpotActivity.this).execute(mListComment.get(position-1));
				        }
				    });
				alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.text_annuler), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
			            dialog.dismiss();
			        }
			    });
				alertDialog.show();
			}
		}
		
		return true;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.vote_plus:
		case R.id.vote_moins:
			mPopupWindow.dismiss();
			if(!mSpot.isHasScore()){
				boolean vote = false;
				if(view.getId() == R.id.vote_plus)
					vote = true;
				new AddScore().execute(vote);
			} else {
				Toast.makeText(this, getString(R.string.spot_already_vote_error), Toast.LENGTH_SHORT).show();
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
	
	private class ListComments extends AsyncTask<Void, Void, CollectionResponseComments>{
		private Context mContext;
		
		public ListComments (Context context){
			mContext = context;
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
				comments = service.listComments().setPIdSpot(mSpot.getID()).execute();
			} catch (Exception e){
				Log.d(getString(R.string.spot_loading_comment_error_log), e.getMessage(), e);
				Toast.makeText(mContext, getString(R.string.spot_loading_comment_error), Toast.LENGTH_LONG).show();
				new ListComments(mContext).execute(); //TODO monitoring error to see if create some bugs
				//return null;
			}
			return comments;
		}
		
		@Override
		protected void onPostExecute(CollectionResponseComments comments) {
			super.onPostExecute(comments);
			if(mListComment == null){
				mListComment = new ArrayList<Comment>();
			} else {
				mListComment.clear();
			}
			if(comments != null){
		        List<Comments> _list = comments.getItems();
			    if(_list != null){
			        for (Comments comment : _list) {
			        	Comment item = new Comment(
			        			comment.getId(),
			        			comment.getIdSpot(),
			        			comment.getIdUser(),
			        			comment.getUser(),
			        			comment.getText(),
			        			comment.getNote()
			        			);
			        	mListComment.add(item);
			        }
		        }
			}
		    populateComment();
		}
	}
		
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
			mProgressDialog.setMessage(getString(R.string.spot_add_comment));
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
				Log.d(getString(R.string.spot_add_comment_error_log), e.getMessage(), e);
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Comments comment) {
			mProgressDialog.dismiss();

			if(comment != null){
				mListComment.add(
					new Comment(
						comment.getId(),
						comment.getIdSpot(), 
						comment.getIdUser(),
						mSessionManager.getUserDetails().get(SessionManager.KEY_NAME), //TODO Information a verifier suite aux changements serveur Non renvoyer par le serveur au moment de l'ajout!
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
				Toast.makeText(getBaseContext(), getString(R.string.spot_add_comment_error), Toast.LENGTH_LONG).show();
			}
			
		}
	}

	private class RemoveComment extends AsyncTask<Comment, Void, Void>{
		private Context mContext;
		private ProgressDialog mProgressDialog;
		
		Comment _comment;
		
		public RemoveComment(Context context){
			this.mContext = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(getString(R.string.spot_delete_comment_error));
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Comment... params) {
			_comment = params[0];
			try{
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				service.removeComments(_comment.getID()).execute();
				
			} catch (Exception e){
				Log.d(getString(R.string.spot_delete_comment_error_log), e.getMessage(), e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			if(_comment != null){
				int index = 0;
				for(int i = 0; i < mListComment.size(); i++){
					if(mListComment.get(i).getID() == _comment.getID()){
						index = i;
					}
				}
				mListComment.remove(index);
				
				mSpot.setTotalNote(mSpot.getTotalNote()-_comment.getNote());
				mSpot.setNbNote(mSpot.getNbNote()-1);
				mDatabaseSpot.OpenDB();
				mDatabaseSpot.updateSpot(mSpot);
				mDatabaseSpot.CloseDB();
				((RatingBar) findViewById(R.id.spot_globalnote)).setRating(mSpot.getGlobalNote());
				populateComment();
				
			} else {
				Toast.makeText(getBaseContext(), getString(R.string.spot_delete_comment_error), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class UpdateComment extends AsyncTask<Comment, Void, Comments>{
		
		private Context mContext;
		private ProgressDialog mProgressDialog;
		private Comment _comment;
		
		public UpdateComment(Context context){
			this.mContext = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(getString(R.string.spot_update_comment));
			mProgressDialog.show();
		}
		
		@Override
		protected Comments doInBackground(Comment... comments) {
			Comments response = null;
			try{

				_comment = comments[0];
				
				mSpot.setTotalNote(mSpot.getTotalNote() - _comment.getNote());
				mSpot.setNbNote(mSpot.getNbNote() - 1);
				mDatabaseSpot.OpenDB();
				mDatabaseSpot.updateSpot(mSpot);
				mDatabaseSpot.CloseDB();
				
				Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Rmsendpoint service = builder.build();
				
				Comments comment = new Comments();
				
				comment.setId(_comment.getID());
				comment.setIdSpot(_comment.getID_Spot());
				comment.setIdUser(_comment.getID_User());
				comment.setUser(_comment.getUser());
				
				comment.setNote(mDialogRate.getRating());
				comment.setText(mDialogCom.getText().toString());
				
				response = service.updateComments(comment).execute();
				
			} catch (Exception e){
				Log.d(getString(R.string.spot_update_comment_error_log), e.getMessage(), e);
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(Comments comment) {
			mProgressDialog.dismiss();
			if(comment != null){
				
				for(int i = 0; i < mListComment.size(); i++){
					if(mListComment.get(i).getID() == _comment.getID()){
						mListComment.set(i,
							new Comment(
								comment.getId(),
								comment.getIdSpot(),
								comment.getIdUser(),
								mSessionManager.getUserDetails().get(SessionManager.KEY_NAME),//TODO Information a verifier suite aux changements serveur Non renvoyer par le serveur au moment de l'ajout!
								comment.getText(),
								comment.getNote())
							);
					}
				}
				
				mSpot.setTotalNote(mSpot.getTotalNote()+comment.getNote());
				mSpot.setNbNote(mSpot.getNbNote()+1);
				mDatabaseSpot.OpenDB();
				mDatabaseSpot.updateSpot(mSpot);
				mDatabaseSpot.CloseDB();
				((RatingBar) findViewById(R.id.spot_globalnote)).setRating(mSpot.getGlobalNote());
				populateComment();
				
			} else {
				mSpot.setTotalNote(mSpot.getTotalNote()+_comment.getNote());
				mSpot.setNbNote(mSpot.getNbNote()+1);
				
				Toast.makeText(getBaseContext(), getString(R.string.spot_update_comment_error), Toast.LENGTH_LONG).show();
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
				Log.d(getString(R.string.spot_add_favorite_error_log), e.getMessage(), e);
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
				Log.d(getString(R.string.spot_delete_favorite_error_log), e.getMessage(), e);
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
				Log.d(getString(R.string.spot_add_score_error_log), e.getMessage(), e);
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

	
}
