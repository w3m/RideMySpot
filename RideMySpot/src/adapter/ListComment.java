package adapter;

import java.util.ArrayList;

import model.Comment;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.w3m.ridemyspot.R;

public class ListComment extends BaseAdapter{

	private ArrayList<Comment> mComments;
	private LayoutInflater mLayoutInflater;

	private ImageView mImage;
	private TextView mName;
	private RatingBar mRate;
	private TextView mText;
	
	public ListComment(Context context, ArrayList<Comment> list){
		mComments = new ArrayList<Comment>();
		mComments.addAll(list);
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		if (!mComments.isEmpty()){
			return mComments.size()+1;
		}
		return 1;// for the add comment item button
	}

	public boolean isLast(int position)
	{
		if(position == getCount()-1)
			return true;
		return false;
	}
	
	@Override
	public Object getItem(int position) {
		if (mComments.size() != 0)
			return mComments.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final View view = mLayoutInflater.inflate(R.layout.comment, null);
		
		mImage = (ImageView) view.findViewById(R.id.comment_image);
		mName = (TextView) view.findViewById(R.id.comment_name);
		mRate = (RatingBar) view.findViewById(R.id.comment_rate);
		mText = (TextView) view.findViewById(R.id.comment_text);
		
		
		if(!mComments.isEmpty() && mComments.size()>position){
			Comment comment = mComments.get(position);
			mName.setText(comment.getUser());
			mRate.setRating((float) comment.getNote());
			mText.setText(comment.getText());
			
			view.setClickable(false);
			
		} else {
			mImage.setVisibility(View.GONE);
			mName.setVisibility(View.GONE);
			mRate.setVisibility(View.GONE);
			
			mText.setText("Ajouter un commentaire"); //TODO getRessource(R.string....)
			mText.setTextSize(30);
			mText.setGravity(Gravity.CENTER);
		}
		
		return view;
	}
	

}
