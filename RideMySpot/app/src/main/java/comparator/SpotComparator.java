package comparator;

import java.util.Comparator;

import android.location.Location;
import model.Spot;

public class SpotComparator implements Comparator<Spot>{

	public static final int COMPARE_BY_NAME = 0;
	public static final int COMPARE_BY_NOTE = 1;
	public static final int COMPARE_BY_NB_NOTE = 2;
	public static final int COMPARE_BY_DISTANCE= 3;
	public static final int COMPARE_BY_FAV = 4;
	
	public int mMode = COMPARE_BY_NAME;
	
	private Location mLocation;
	
	public SpotComparator(){
	}
	
	public SpotComparator(int mode, Location location){
		mMode = mode;
		mLocation = location;
	}

	@Override
	public int compare(Spot spot1, Spot spot2) {
		switch (mMode) {
		case COMPARE_BY_FAV:
			if(!spot1.isFavorite()){
				if(spot2.isFavorite()){
					return 1;
				}
			} else {
				if(!spot2.isFavorite()){
					return -1;
				}
			}
		case COMPARE_BY_NAME:
			return spot1.getName().compareTo(spot2.getName());
		case COMPARE_BY_NOTE:
			return (spot2.getGlobalNote() > spot1.getGlobalNote() ? 1 : (spot2.getGlobalNote() < spot1.getGlobalNote() ? -1 : 0));
		case COMPARE_BY_NB_NOTE:
			return (spot2.getNbNote() > spot1.getNbNote() ? 1 : (spot2.getNbNote() < spot1.getNbNote() ? -1 : 0));
		case COMPARE_BY_DISTANCE:
			Location location1 = new Location("");
			location1.setLatitude(spot1.getPosition_lat());
			location1.setLongitude(spot1.getPosition_long());
			
			Location location2 = new Location("");
			location2.setLatitude(spot2.getPosition_lat());
			location2.setLongitude(spot2.getPosition_long());

			return (mLocation.distanceTo(location2) > mLocation.distanceTo(location1) ? 1 : (mLocation.distanceTo(location2) < mLocation.distanceTo(location1) ? -1 : 0));
		default:
			return 0;
		}
	}
}
