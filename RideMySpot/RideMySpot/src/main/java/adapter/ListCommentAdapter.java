package adapter;

import java.util.ArrayList;

import model.Comment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.w3m.ridemyspot.R;

public class ListCommentAdapter extends BaseAdapter{

	private ArrayList<Comment> mListComments;
	private LayoutInflater mLayoutInflater;

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

	static class ViewHolder {
		ImageView mImage;
		TextView mName;
		RatingBar mRate;
		TextView mText;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.comment, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.mImage = (ImageView) convertView.findViewById(R.id.comment_image);
			viewHolder.mName = (TextView) convertView.findViewById(R.id.comment_name);
			viewHolder.mRate = (RatingBar) convertView.findViewById(R.id.comment_rate);
			viewHolder.mText = (TextView) convertView.findViewById(R.id.comment_text);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(!mListComments.isEmpty() && mListComments.size()>position){
			Comment comment = mListComments.get(position);
			viewHolder.mName.setText(comment.getUser());
			viewHolder.mRate.setRating((float) comment.getNote());
			viewHolder.mText.setText(comment.getText());
		} else {
			convertView = (TextView) mLayoutInflater.inflate(R.layout.add_comment_item, parent, false);
		}
		
		return convertView;
	}
	

}
