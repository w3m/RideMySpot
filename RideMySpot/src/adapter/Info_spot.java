package adapter;

import model.Spot;
import android.content.Context;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.w3m.ridemyspot.R;

public class Info_spot implements InfoWindowAdapter{
	
	private LayoutInflater m_layoutInflater;
	private Spot m_spot;

	public Info_spot(Context context, Spot spot) {
		m_layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_spot = spot;
	}

	@Override
	public View getInfoContents(Marker marker) {
		View view = m_layoutInflater.inflate(R.layout.info_spot, null);
		
		((TextView) view.findViewById(R.id.info_spot_name)).setText(marker.getTitle());
		String note = marker.getSnippet();
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
