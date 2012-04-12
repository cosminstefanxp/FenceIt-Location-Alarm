package com.fenceit.db;

import org.apache.log4j.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDBHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "alarms";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ENABLED = "enabled";
	public static final String COLUMN_CREATION_DATE = "creation_date";

	public static final String[] ALL_COLUMNS = new String[] { COLUMN_ID, COLUMN_NAME, COLUMN_ENABLED,
			COLUMN_CREATION_DATE };
	
	private static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME + "( "
			+ COLUMN_ID	+ " integer primary key autoincrement, " 
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_ENABLED + " integer, "
			+ COLUMN_CREATION_DATE + " date );";

	public AlarmDBHelper(Context context) {
		super(context, DatabaseDefaults.DATABASE_NAME, null, DatabaseDefaults.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Delete all existing data and re-create the table
		Logger.getLogger(DatabaseDefaults.class).warn(
				"Upgrading database from version " + oldVersion + " to " + newVersion
						+ ", which will destroy all exiting data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}
