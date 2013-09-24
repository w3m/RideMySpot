package adapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.w3m.ridemyspot.R;

import model.Comment;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class List_comment extends BaseAdapter{

	private ArrayList<Comment> m_comments;
	private LayoutInflater m_layoutInflater;

	private ImageView m_image;
	private TextView m_name;
	private RatingBar m_rate;
	private TextView m_text;
	
	public List_comment(Context context, ArrayList<Comment> list){
		m_comments = new ArrayList<Comment>();
		m_comments.addAll(list);
		m_layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		if (!m_comments.isEmpty()){
			return m_comments.size()+1;
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
		if (m_comments.size() != 0)
			return m_comments.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final View view = m_layoutInflater.inflate(R.layout.comment, null);
		
		m_image = (ImageView) view.findViewById(R.id.comment_image);
		m_name = (TextView) view.findViewById(R.id.comment_name);
		m_rate = (RatingBar) view.findViewById(R.id.comment_rate);
		m_text = (TextView) view.findViewById(R.id.comment_text);
		
		
		if(!m_comments.isEmpty() && m_comments.size()>position){
			Log.d("rms_debug", m_comments.get(position).getUser());
			Comment comment = m_comments.get(position);
			m_name.setText(comment.getUser());
			m_rate.setRating((float) comment.getNote());
			m_text.setText(comment.getText());
			
			view.setClickable(false);
			
		} else {
			m_image.setVisibility(View.GONE);
			m_name.setVisibility(View.GONE);
			m_rate.setVisibility(View.GONE);
			
			m_text.setText("Ajouter un commentaire"); //TODO getRessource(R.string....)
			m_text.setTextSize(30);
			m_text.setGravity(Gravity.CENTER);
		}
		
		return view;
	}
	

}
