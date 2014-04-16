package taskLoader;

import java.util.ArrayList;
import java.util.List;

import model.Comment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import entity.Rmsendpoint;
import entity.model.CollectionResponseComments;
import entity.model.Comments;

public class CommentTaskLoader extends AsyncTaskLoader<List<Comment>> {
	
	public static final String ID_SPOT = "ID_SPOT";
	
	private List<Comment> mComments;
	private Long mIdSpot;
	
	public CommentTaskLoader(Context context, Bundle args){
		super(context);
		mIdSpot = args.getLong(ID_SPOT);
	}

	@Override
	public List<Comment> loadInBackground() {
		CollectionResponseComments comments = null;
		try{
			Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
			Rmsendpoint service = builder.build();
			comments = service.listComments().setPIdSpot(mIdSpot).execute();
		} catch (Exception e){
			Log.d("impossible de récupérer les commmentaires", e.getMessage(), e);//TODO getressource
			Toast.makeText(getContext(), "Un problème c'est produit avec le chargement des commentaires. Nouveau chargement en cours !", Toast.LENGTH_SHORT).show();
		}
		
		if(mComments == null){
			mComments = new ArrayList<Comment>();
		} else {
			mComments.clear();
		}
		if(comments != null){
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
		        	mComments.add(item);
		        }
	        }
		}
		
		return mComments;
	}
	
	@Override
	public void deliverResult(List<Comment> data) {
		if(isReset()){
			releaseResources(data);
		}
		
		List<Comment> oldData = mComments;
		mComments = data;
		
		if(isStarted()){
			super.deliverResult(data);
		}
		
		if (oldData != null && oldData != data) {
			releaseResources(oldData);
		}
	}
	
	@Override
	protected void onStartLoading() {
		if(mComments != null){
			deliverResult(mComments);
		}
		
		if(takeContentChanged() || mComments == null){
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	protected void onReset() {
		onStopLoading();
		
		if(mComments != null){
			releaseResources(mComments);
			mComments = null;
		}
	}
	
	@Override
	public void onCanceled(List<Comment> data) {
		super.onCanceled(data);
		releaseResources(data);
	}
	
	private void releaseResources(List<Comment> data) {
	    // For a simple List, there is nothing to do. For something like a Cursor, we 
	    // would close it in this method. All resources associated with the Loader
	    // should be released here.
	  }
}
