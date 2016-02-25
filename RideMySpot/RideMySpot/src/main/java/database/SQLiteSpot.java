package database;

import java.util.ArrayList;
import java.util.List;

import model.Spot;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import entity.model.Spots;

public class SQLiteSpot {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "spot.db";

	public static final String TABLE_SPOTS = "TABLE_SPOTS";

	public static final String COL_ID = "COL_ID";
	public static final int NUM_ID = 0;
	public static final String COL_NAME = "COL_NAME";
	public static final int NUM_NAME = 1;
	public static final String COL_DESCRIPTION = "COL_DESCRIPTION";
	public static final int NUM_DESCRIPTION = 2;
	public static final String COL_LATITUDE = "COL_LATITUDE";
	public static final int NUM_LATITUDE = 3;
	public static final String COL_LONGITUDE = "COL_LONGITUDE";
	public static final int NUM_LONGITUDE = 4;
	public static final String COL_TYPE = "COL_TYPE";
	public static final int NUM_TYPE = 5;
	public static final String COL_TOTAL_NOTE = "COL_TOTAL_NOTE";
	public static final int NUM_TOTAL_NOTE = 6;
	public static final String COL_NB_NOTE = "COL_NB_NOTE";
	public static final int NUM_NB_NOTE = 7;
	public static final String COL_SCORE = "COL_SCORE";
	public static final int NUM_SCORE = 8;
	public static final String COL_FAVORITE = "COL_FAVORITE";
	public static final int NUM_FAVORITE = 9;
	public static final String COL_HAS_SCORE = "COL_HAS_SCORE";
	public static final int NUM_HAS_SCORE = 10; //TODO Stock multi boolean in integer with mask
	
	private SQLiteSpotBase mSQLiteSpotBase;
	private SQLiteDatabase mDatabase;
	
	public SQLiteSpot(Context context){
		mSQLiteSpotBase = new SQLiteSpotBase(context, DATABASE_NAME, DATABASE_VERSION);
	}
	
	public void OpenDB(){
		mDatabase = mSQLiteSpotBase.getWritableDatabase();
	}
	
	public void CloseDB(){
		mDatabase.close();
	}
	
	public List<Spot> getListSpot(){
		Cursor cursor = mDatabase.query(TABLE_SPOTS, null, null, null, null, null, null);
		List<Spot> list = new ArrayList<Spot>();
		
		cursor.moveToFirst();
		for(int i=0; i<cursor.getCount(); i++){
			list.add(new Spot(
					cursor.getLong(NUM_ID),
					cursor.getString(NUM_NAME), 
					cursor.getString(NUM_DESCRIPTION), 
					cursor.getDouble(NUM_LATITUDE),
					cursor.getDouble(NUM_LONGITUDE), 
					cursor.getInt(NUM_TYPE), 
					cursor.getFloat(NUM_TOTAL_NOTE), 
					cursor.getInt(NUM_NB_NOTE), 
					cursor.getInt(NUM_FAVORITE) == 1 ? true : false, 
					cursor.getInt(NUM_SCORE), 
					cursor.getInt(NUM_HAS_SCORE) == 1 ? true : false
					)
			);
			cursor.moveToNext();
		}
		cursor.close();
		
		return list;
	}

	public void updateSpot(Spot spot){
		mDatabase.update(TABLE_SPOTS, getSpotContentValue(spot), COL_ID + " = ?",new String[] {String.valueOf(spot.getID())});
	}
	
	private ContentValues getSpotContentValue(Spot spot){
		ContentValues values = new ContentValues();
        values.put(COL_ID, spot.getID());
        values.put(COL_NAME, spot.getName());
        values.put(COL_DESCRIPTION, spot.getDescription());
        values.put(COL_LATITUDE, spot.getPosition_lat());
        values.put(COL_LONGITUDE, spot.getPosition_long());
        values.put(COL_TYPE, spot.getType());
        values.put(COL_TOTAL_NOTE, spot.getTotalNote());
        values.put(COL_NB_NOTE, spot.getNbNote());
        values.put(COL_FAVORITE, spot.isFavorite());
        values.put(COL_SCORE, spot.getScore());
        values.put(COL_HAS_SCORE, spot.isHasScore());
        return values;
	}

	public void insertListEntitySpots(List<Spots> spots){
		mDatabase.beginTransaction();
		
		mDatabase.delete(TABLE_SPOTS, null, null);
		for(Spots spot : spots){
			insertEntitySpots(spot);
		}
		
		mDatabase.setTransactionSuccessful();
		mDatabase.endTransaction();
	}
	
	public void insertEntitySpots(Spots spots){
		mDatabase.insert(TABLE_SPOTS, null, getEntitySpotContentValue(spots));
	}
	
	private ContentValues getEntitySpotContentValue(Spots spots){
		ContentValues values = new ContentValues();
        values.put(COL_ID, spots.getId());
        values.put(COL_NAME, spots.getName());
        values.put(COL_DESCRIPTION, spots.getDescription());
        values.put(COL_LATITUDE, spots.getLatitude());
        values.put(COL_LONGITUDE, spots.getLongitude());
        values.put(COL_TYPE, spots.getType());
        values.put(COL_TOTAL_NOTE, spots.getTotalNote());
        values.put(COL_NB_NOTE, spots.getNbNote());
        values.put(COL_FAVORITE, spots.getFavorite());
        values.put(COL_SCORE, spots.getScore());
        values.put(COL_HAS_SCORE, spots.getHasScore());
        return values;
	}

}
