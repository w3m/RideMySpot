package adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import model.Spot;
import android.content.Context;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.w3m.ridemyspot.R;

public class ListSpotAdapter extends BaseAdapter{

	private ArrayList<Spot> mListSpots;
	private LayoutInflater mLayoutInflater;

	private Location mLocation;
	private float mHeading;
	private float mBearing;
	
	private Context mContext;
	
//	private SensorManager mSensorManager;
//	private GeomagneticField mUserGeoPoint;
	private float[] mMatrixOrientation = new float[16];
	private float[] mOrientation = new float[3];	
		
	public ListSpotAdapter(Context context, List<Spot> list, Location location){
		mContext = context;
		
		mLocation = location;
		if(mLocation != null){
//			mUserGeoPoint = new GeomagneticField(
//					Double.valueOf(location.getLatitude()).floatValue(), 
//					Double.valueOf(location.getLongitude()).floatValue(),
//					Double.valueOf(location.getAltitude()).floatValue(),
//					System.currentTimeMillis()
//			);
		}
		
		mListSpots = new ArrayList<Spot>();
		mListSpots.addAll(list);
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		SensorManager.getOrientation(mMatrixOrientation, mOrientation);
	}
	
	@Override
	public int getCount() {
		if (!mListSpots.isEmpty()){
			return mListSpots.size();
		}
		return 0;// for the add Spot item button
	}

	public boolean isLast(int position)
	{
		if(position == getCount()-1)
			return true;
		return false;
	}
	
	@Override
	public Object getItem(int position) {
		if (mListSpots.size() != 0)
			return mListSpots.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
//private ImageView mIcon;
		TextView mName;
		TextView mNbRate;
		RatingBar mRate;
		TextView mDescription;
		ImageView mFavorite;
		TextView mNbKm;
//		ImageView mPointer;
		}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.list_spot_item, parent, false);
		
			viewHolder = new ViewHolder();
			viewHolder.mName = (TextView) convertView.findViewById(R.id.list_spot_name);
			viewHolder.mNbRate = (TextView) convertView.findViewById(R.id.list_spot_nbrate);
			viewHolder.mRate = (RatingBar) convertView.findViewById(R.id.list_spot_globalnote);
			viewHolder.mDescription = (TextView) convertView.findViewById(R.id.list_spot_description);
			viewHolder.mFavorite = (ImageView) convertView.findViewById(R.id.list_spot_favorite);
			viewHolder.mNbKm = (TextView) convertView.findViewById(R.id.list_spot_nbkm);
			
//			viewHolder.mPointer = mNbKm.getBackground(); //TODO get drawable to draw the rotatematrix
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		
		
		
		if(!mListSpots.isEmpty() && mListSpots.size()>position){

			Spot spot = mListSpots.get(position);
			
			viewHolder.mName.setText(spot.getName());
			viewHolder.mNbRate.setText(String.valueOf(spot.getNbNote()));
			viewHolder.mRate.setRating((float) spot.getGlobalNote());
			viewHolder.mDescription.setText(spot.getDescription());
			if(spot.isFavorite()){
				viewHolder.mFavorite.setImageResource(R.drawable.heart_full_x16);
			}

			
			Location location = new Location("");
			location.setLatitude(spot.getPosition_lat());
			location.setLongitude(spot.getPosition_long());
			

			if(mLocation != null){
				viewHolder.mNbKm.setText(convertDistance(mLocation.distanceTo(location)));
				
				mBearing = mLocation.bearingTo(location);
				mHeading = mOrientation[0];
//				mHeading += mUserGeoPoint.getDeclination();
				mHeading = (mBearing - mHeading) * -1; //TODO Ameliorer precision
//				
				Matrix matrix = new Matrix();
				matrix.postRotate(normalizeDegree(mHeading), 16f, 16f);
//				viewHolder.mPointer.setScaleType(ScaleType.MATRIX);
//				viewHolder.mPointer.setImageMatrix(matrix);
			} else {
				viewHolder.mNbKm.setVisibility(View.GONE);
				//viewHolder.mPointer.setVisibility(View.GONE);
			}
			
			
		}
		
		return convertView;
	}
	
	private String convertDistance(float distance){
		String result;
		if(distance > 1000){
			double value = distance / 1000;
			result = String.valueOf(round(value, 2)) + mContext.getString(R.string.text_long_distance);
		} else {
			result = String.valueOf(distance) + mContext.getString(R.string.text_small_distance);
		}
		return result;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private float normalizeDegree(float value){
		if(value >= 0.0f && value <= 180.0f){
			return value;
		} else {
			return 180 + (180 + value);
		}
	}
	

}
