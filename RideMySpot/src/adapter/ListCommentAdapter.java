package adapter;

import java.util.ArrayList;

import model.Comment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.w3m.ridemyspot.R;

public class ListCommentAdapter extends BaseAdapter{

	private ArrayList<Comment> mListComments;
	private LayoutInflater mLayoutInflater;

	private ImageView mImage;
	private TextView mName;
	private RatingBar mRate;
	private TextView mText;
	
	private Context mContext;
	
	public ListCommentAdapter(Context context, ArrayList<Comment> list){
		mContext = context;
		mListComments = new ArrayList<Comment>();
		mListComments.addAll(list);
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		if (!mListComments.isEmpty()){
			return mListComments.size()+1;
		}
		return 1;// for the add comment item button
	}

	@Override
	public Object getItem(int position) {
		if (mListComments.size() != 0)
			return mListComments.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout view = (LinearLayout) convertView;
		if(view == null){
			view = (LinearLayout) mLayoutInflater.inflate(R.layout.comment, view);
		} else {
			view = (LinearLayout) convertView;
		}
		
		mImage = (ImageView) view.findViewById(R.id.comment_image);
		mName = (TextView) view.findViewById(R.id.comment_name);
		mRate = (RatingBar) view.findViewById(R.id.comment_rate);
		mText = (TextView) view.findViewById(R.id.comment_text);
		
		
		if(!mListComments.isEmpty() && mListComments.size()>position){
			Comment comment = mListComments.get(position);
			mName.setText(comment.getUser());
			mRate.setRating((float) comment.getNote());
			mText.setText(comment.getText());
			
			view.setClickable(false);
			
		} else {
			mImage.setVisibility(View.GONE);
			mName.setVisibility(View.GONE);
			mRate.setVisibility(View.GONE);
			mText.setVisibility(View.GONE);
			
			mLayoutInflater.inflate(R.layout.add_comment_item, view);
		}
		
		return view;
	}
	

}
