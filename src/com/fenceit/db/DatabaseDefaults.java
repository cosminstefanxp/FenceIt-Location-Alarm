/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.triggers.BasicTrigger;

/**
 * The Class DatabaseDefaults.
 */
public class DatabaseDefaults {
	
	/** The DATABASE NAME. */
	public static final String DATABASE_NAME = "fenceit.db";
	
	/** The DATABASE VERSION. */
	public static final int DATABASE_VERSION = 7;
	
	private static SQLiteOpenHelper dbHelper=null;
	
	/**
	 * Gets the Singleton database helper.
	 *
	 * @return the dB helper
	 */
	public static SQLiteOpenHelper getDBHelper(Context context)
	{
		if(dbHelper==null)
			dbHelper=new DefaultDatabaseHelper(context, 
					new Class[] { Alarm.class, BasicTrigger.class }, 
					new String[] { Alarm.tableName, BasicTrigger.tableName });
		return dbHelper;
	}
}
