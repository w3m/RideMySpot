package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.w3m.ridemyspot.R;

/**
 * Created by steeveguillaume on 10/03/16.
 */
public class NavigationDrawerAdapter extends BaseAdapter
{
    private final Context mContext;
    private String[] mDrawerItems;

    public NavigationDrawerAdapter(Context context)
    {
        mContext = context;
        mDrawerItems = context.getResources().getStringArray(R.array.navigation_drawer_menu);
    }

    @Override
    public int getCount() {
        return mDrawerItems.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        CompleteListViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_list_item, null);
            viewHolder = new CompleteListViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (CompleteListViewHolder) view.getTag();
        }
        //viewHolder.mIVItem.setImageResource(choice.icon);
        viewHolder.mTVItem.setText(mDrawerItems[position]);
        return view;
    }
}

class CompleteListViewHolder {
    public ImageView mIVItem;
    public TextView mTVItem;
    public CompleteListViewHolder(View base) {
        mIVItem = (ImageView) base.findViewById(R.id.navDrawerImageView);
        mTVItem = (TextView) base.findViewById(R.id.navDrawerTextView);
    }
}
