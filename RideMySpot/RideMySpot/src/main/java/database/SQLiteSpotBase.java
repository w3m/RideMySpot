package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteSpotBase extends SQLiteOpenHelper{
	
	private static final String CREATE_DATABASE = "CREATE TABLE " + SQLiteSpot.TABLE_SPOTS + "(" +
			SQLiteSpot.COL_ID			+ " INTEGER PRIMARY KEY," +
			SQLiteSpot.COL_NAME			+ " TEXT," +
			SQLiteSpot.COL_DESCRIPTION	+ " TEXT," +
			SQLiteSpot.COL_LATITUDE		+ " REAL," +
			SQLiteSpot.COL_LONGITUDE	+ " REAL," +
			SQLiteSpot.COL_TYPE			+ " INTEGER," +
			SQLiteSpot.COL_TOTAL_NOTE	+ " REAL," +
			SQLiteSpot.COL_NB_NOTE		+ " INTEGER," +
			SQLiteSpot.COL_SCORE		+ " INTEGER," +
			SQLiteSpot.COL_FAVORITE		+ " INTEGER," +
			SQLiteSpot.COL_HAS_SCORE	+ " INTEGER" +
			");"
			;

	public SQLiteSpotBase(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + SQLiteSpot.TABLE_SPOTS + ";");
		onCreate(db);
	}

}
