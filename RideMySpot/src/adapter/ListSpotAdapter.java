package adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import model.Spot;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.SensorManager;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.w3m.ridemyspot.R;

public class ListSpotAdapter extends BaseAdapter{

	private ArrayList<Spot> mSpots;
	private LayoutInflater mLayoutInflater;
	private Context mContext;

	private ImageView mIcon;
	private TextView mName;
	private TextView mNbRate;
	private RatingBar mRate;
	private TextView mDescription;
	private ImageView mFavorite;
	private TextView mNbKm;
	private ImageView mPointer;
	
	private Location mLocation;
	private float mHeading;
	private float mBearing;
	
	private SensorManager mSensorManager;
	private GeomagneticField mUserGeoPoint;
	private float[] mMatrixOrientation = new float[16];
	private float[] mOrientation = new float[3];	
		
	public ListSpotAdapter(Context context, List<Spot> list, Location location){
		mContext = context;
		mLocation = location;
		
		if(mLocation != null){
			mUserGeoPoint = new GeomagneticField(
					Double.valueOf(location.getLatitude()).floatValue(), 
					Double.valueOf(location.getLongitude()).floatValue(),
					Double.valueOf(location.getAltitude()).floatValue(),
					System.currentTimeMillis()
			);
		}
		
		mSpots = new ArrayList<Spot>();
		mSpots.addAll(list);
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		SensorManager.getOrientation(mMatrixOrientation, mOrientation);
	}
	
	@Override
	public int getCount() {
		if (!mSpots.isEmpty()){
			return mSpots.size();
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
		if (mSpots.size() != 0)
			return mSpots.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout view = (RelativeLayout) convertView;
		if(view == null){
			view = (RelativeLayout) mLayoutInflater.inflate(R.layout.list_spot_item, view);
		} else {
			view = (RelativeLayout) convertView;
		}

		mName = (TextView) view.findViewById(R.id.list_spot_name);
		mNbRate = (TextView) view.findViewById(R.id.list_spot_nbrate);
		mRate = (RatingBar) view.findViewById(R.id.list_spot_globalnote);
		mDescription = (TextView) view.findViewById(R.id.list_spot_description);
		mFavorite = (ImageView) view.findViewById(R.id.list_spot_favorite);
		mNbKm = (TextView) view.findViewById(R.id.list_spot_nbkm);
		mPointer = (ImageView) view.findViewById(R.id.list_spot_pointer);
		
		
		if(!mSpots.isEmpty() && mSpots.size()>position){

			Spot spot = mSpots.get(position);
			
			mName.setText(spot.getName());
			mNbRate.setText(String.valueOf(spot.getNbNote()));
			mRate.setRating((float) spot.getGlobalNote());
			mDescription.setText(spot.getDescription());
			if(spot.isFavorite()){
				mFavorite.setImageResource(R.drawable.heart_full_x16);
			}

			
			Location location = new Location("");
			location.setLatitude(spot.getPosition_lat());
			location.setLongitude(spot.getPosition_long());
			

			if(mLocation != null){
				mNbKm.setText(convertDistance(mLocation.distanceTo(location)));
				
				mBearing = mLocation.bearingTo(location);
				mHeading = mOrientation[0];
//				mHeading += mUserGeoPoint.getDeclination();
				mHeading = (mBearing - mHeading) * -1; //TODO Ameliorer pr�cision
//				
				Matrix matrix = new Matrix();
				matrix.postRotate(normalizeDegree(mHeading), 16f, 16f);
//				mPointer.setScaleType(ScaleType.MATRIX);
//				mPointer.setImageMatrix(matrix);
			} else {
				mNbKm.setVisibility(View.GONE);
				mPointer.setVisibility(View.GONE);
			}
			
			
		}
		
		return view;
	}
	
	private String convertDistance(float distance){
		String result;
		if(distance > 1000){
			double value = distance / 1000;

			result = String.valueOf(round(value, 2)) + "km"; //TODO Ressource!
		} else {
			result = String.valueOf(distance) + "m"; //TODO Ressource!
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
