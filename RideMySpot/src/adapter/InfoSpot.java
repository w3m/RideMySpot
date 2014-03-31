package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.w3m.ridemyspot.R;

public class InfoSpot implements InfoWindowAdapter{
	
	private LayoutInflater mLayoutInflater;

	public InfoSpot(Context context) {
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getInfoContents(Marker marker) {
		View view = mLayoutInflater.inflate(R.layout.info_spot, null);
		
		((TextView) view.findViewById(R.id.info_spot_name)).setText(marker.getTitle());
		
		String note = marker.getSnippet();
		if(note.equals("addSpot") || note.equals("user"))
			return null;
		if(note.length()!=0)
			((RatingBar) view.findViewById(R.id.info_spot_globalnote)).setRating(Float.valueOf(note));
		else
			((RatingBar) view.findViewById(R.id.info_spot_globalnote)).setVisibility(View.GONE);

		return view;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		return null;
	}

}
