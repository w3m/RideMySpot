package adapter;

import java.util.HashMap;

import model.Spot;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.w3m.ridemyspot.R;

public class InfoSpotAdapter implements InfoWindowAdapter{
	
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private HashMap<String, Spot> mHMSpot;

	public InfoSpotAdapter(Context context, HashMap<String, Spot> hmSpot) {
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mHMSpot = new HashMap<String, Spot>();
		mHMSpot.putAll(hmSpot);
	}

	@Override
	public View getInfoContents(Marker marker) {
		RelativeLayout view = new RelativeLayout(mContext); 
		mLayoutInflater.inflate(R.layout.info_spot, view);
		Spot spot = mHMSpot.get(marker.getId());
		
		if(spot == null){
			return null;
		}
		((TextView) view.findViewById(R.id.info_spot_name)).setText(marker.getTitle());
		
		((TextView) view.findViewById(R.id.info_spot_nbrate)).setText(String.valueOf(spot.getNbNote()));
		((RatingBar) view.findViewById(R.id.info_spot_globalnote)).setRating(spot.getGlobalNote());
		if(spot.isFavorite()){
			((ImageView) view.findViewById(R.id.info_spot_favorite)).setImageResource(R.drawable.heart_full_x16);
		}

		return view;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

}
