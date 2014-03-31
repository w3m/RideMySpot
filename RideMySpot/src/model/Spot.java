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
	private String m_name;
	private String m_adress;
	private String m_description;
	private double m_position_lat;
	private double m_position_long;
	private int m_type;
	private float m_totalNote;
	private int m_nbNote;
	private int m_score;
	private boolean m_favorite;
	private boolean m_hasScore;
	
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
		return (m_totalNote/m_nbNote);
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
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public String getAdress() {
		return m_adress;
	}

	public void setAdress(String adress) {
		this.m_adress = adress;
	}

	public String getDescription() {
		return m_description;
	}

	public void setDescription(String description) {
		this.m_description = description;
	}

	public double getPosition_lat() {
		return m_position_lat;
	}

	public void setPosition_lat(double position_lat) {
		this.m_position_lat = position_lat;
	}
	
	public double getPosition_long() {
		return m_position_long;
	}

	public void setPosition_long(double position_long) {
		this.m_position_long = position_long;
	}

	public int getType() {
		return m_type;
	}

	public void setType(int type) {
		this.m_type = type;
	}

	public float getTotalNote() {
		return m_totalNote;
	}

	public void setTotalNote(float totalNote) {
		this.m_totalNote = totalNote;
	}

	public int getNbNote() {
		return m_score;
	}

	public void setNbNote(int nbNote) {
		this.m_nbNote = nbNote;
	}

	public int getScore() {
		return m_score;
	}

	public void setScore(int score) {
		this.m_score = score;
	}
	
	public LatLng getPosition(){
		return new LatLng(m_position_lat, m_position_long);
	}

	public boolean isFavorite() {
		return m_favorite;
	}

	public void setFavorite(boolean favorite) {
		this.m_favorite = favorite;
	}

	public boolean isHasScrore() {
		return m_hasScore;
	}

	public void setHasScore(boolean hasScore) {
		this.m_hasScore = hasScore;
	}

	public List<String> getStringTypes(){
		List<String> type = new ArrayList<String>();
		type.addAll(dudu_s_tricks());
		return type;
	}
		
	private List<String> dudu_s_tricks(){
		List<String> tricks = new ArrayList<String>();
		
		if((m_type & ROLLER) == (ROLLER)){
			tricks.add("Roller");
		}
		if((m_type & SKATE) == (SKATE)){
			tricks.add("Skate");
		}
		if((m_type & BMX) == (BMX)){
			tricks.add("BMX");
		}
		if((m_type & SKATEPARK) == (SKATEPARK)){
			tricks.add("Skatepark");
		}
		
		return tricks;
	}
		


    protected Spot(Parcel in) {
        ID = in.readLong();
        MarkerID = in.readString();
        m_name = in.readString();
        m_adress = in.readString();
        m_description = in.readString();
        m_position_lat = in.readDouble();
        m_position_long = in.readDouble();
        m_type = in.readInt();
        m_totalNote = in.readFloat();
        m_nbNote = in.readInt();
        m_score = in.readInt();
        m_favorite = in.readByte() != 0;
        m_hasScore = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ID);
        dest.writeString(MarkerID);
        dest.writeString(m_name);
        dest.writeString(m_adress);
        dest.writeString(m_description);
        dest.writeDouble(m_position_lat);
        dest.writeDouble(m_position_long);
        dest.writeInt(m_type);
        dest.writeFloat(m_totalNote);
        dest.writeInt(m_nbNote);
        dest.writeInt(m_score);
        dest.writeByte((byte) (m_favorite ? 1 : 0));
        dest.writeByte((byte) (m_hasScore ? 1 : 0)); 
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
		if(m_position_lat == rounded_lat && m_position_long == rounded_long)
			return true;
		return false;
	}
}

