package model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Spot implements Parcelable {
	public final static int ROLLER = 1;
	public final static int SKATE = 1 << 1;
	public final static int BMX = 1 << 2;
	public final static int SKATEPARK= 1 << 3;

	private long ID;
	private String MarkerID;
	private String mName;
	private String mAdress;
	private String mDescription;
	private double mPosition_lat;
	private double mPosition_long;
	private int mType;
	private float mTotalNote;
	private int mNbNote;
	private int mScore;
	private boolean mFavorite;
	private boolean mHasScore;
	
	public Spot(){}

	public Spot(long id,String name, String adress, String description, double position_lat, double position_long, int type, float totalNote, int nbNote, boolean favorite, int score, boolean hasScore){
		this.setID(id);
		this.setName(name);
		this.setAdress(adress);
		this.setDescription(description);
		this.setPosition_lat(position_lat);
		this.setPosition_long(position_long);
		this.setType(type);
		this.setTotalNote(totalNote);
		this.setNbNote(nbNote);
		this.setFavorite(favorite);
		this.setScore(score);
		this.setHasScore(hasScore);
	}
	
	public float getGlobalNote(){
		return (mTotalNote/mNbNote);
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}
	
	public String getMarkerID() {
		return MarkerID;
	}

	public void setMarkerID(String markerID) {
		MarkerID = markerID;
	}
	
	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getAdress() {
		return mAdress;
	}

	public void setAdress(String adress) {
		this.mAdress = adress;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		this.mDescription = description;
	}

	public double getPosition_lat() {
		return mPosition_lat;
	}

	public void setPosition_lat(double position_lat) {
		this.mPosition_lat = position_lat;
	}
	
	public double getPosition_long() {
		return mPosition_long;
	}

	public void setPosition_long(double position_long) {
		this.mPosition_long = position_long;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		this.mType = type;
	}

	public float getTotalNote() {
		return mTotalNote;
	}

	public void setTotalNote(float totalNote) {
		this.mTotalNote = totalNote;
	}

	public int getNbNote() {
		return mScore;
	}

	public void setNbNote(int nbNote) {
		this.mNbNote = nbNote;
	}

	public int getScore() {
		return mScore;
	}

	public void setScore(int score) {
		this.mScore = score;
	}
	
	public LatLng getPosition(){
		return new LatLng(mPosition_lat, mPosition_long);
	}

	public boolean isFavorite() {
		return mFavorite;
	}

	public void setFavorite(boolean favorite) {
		this.mFavorite = favorite;
	}

	public boolean isHasScrore() {
		return mHasScore;
	}

	public void setHasScore(boolean hasScore) {
		this.mHasScore = hasScore;
	}

	public List<String> getStringTypes(){
		List<String> type = new ArrayList<String>();
		type.addAll(dudu_s_tricks());
		return type;
	}
		
	private List<String> dudu_s_tricks(){
		List<String> tricks = new ArrayList<String>();
		
		if((mType & ROLLER) == (ROLLER)){
			tricks.add("Roller");
		}
		if((mType & SKATE) == (SKATE)){
			tricks.add("Skate");
		}
		if((mType & BMX) == (BMX)){
			tricks.add("BMX");
		}
		if((mType & SKATEPARK) == (SKATEPARK)){
			tricks.add("Skatepark");
		}
		
		return tricks;
	}
		


    protected Spot(Parcel in) {
        ID = in.readLong();
        MarkerID = in.readString();
        mName = in.readString();
        mAdress = in.readString();
        mDescription = in.readString();
        mPosition_lat = in.readDouble();
        mPosition_long = in.readDouble();
        mType = in.readInt();
        mTotalNote = in.readFloat();
        mNbNote = in.readInt();
        mScore = in.readInt();
        mFavorite = in.readByte() != 0;
        mHasScore = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ID);
        dest.writeString(MarkerID);
        dest.writeString(mName);
        dest.writeString(mAdress);
        dest.writeString(mDescription);
        dest.writeDouble(mPosition_lat);
        dest.writeDouble(mPosition_long);
        dest.writeInt(mType);
        dest.writeFloat(mTotalNote);
        dest.writeInt(mNbNote);
        dest.writeInt(mScore);
        dest.writeByte((byte) (mFavorite ? 1 : 0));
        dest.writeByte((byte) (mHasScore ? 1 : 0)); 
    }

    public static final Parcelable.Creator<Spot> CREATOR = new Parcelable.Creator<Spot>() {
        public Spot createFromParcel(Parcel in) {
            return new Spot(in);
        }

        public Spot[] newArray(int size) {
            return new Spot[size];
        }
    };

	public boolean isHere(LatLng position) {
		double rounded_lat = (double) Math.round(position.latitude * 1000000.0) / 1000000.0;
		double rounded_long = (double) Math.round(position.longitude * 1000000.0) / 1000000.0;
		if(mPosition_lat == rounded_lat && mPosition_long == rounded_long)
			return true;
		return false;
	}
}

