/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.util.HashMap;

import org.androwrapee.db.DefaultDatabaseHelper;
import org.androwrapee.db.IllegalClassStructureException;
import org.androwrapee.db.ReflectionManager;
import org.apache.log4j.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.triggers.BasicTrigger;

/**
 * The Class DatabaseDefaults.
 */
public class DatabaseManager {

	/** The DATABASE NAME. */
	public static final String DATABASE_NAME = "fenceit.db";

	/** The DATABASE VERSION. */
	public static final int DATABASE_VERSION = 1;

	/** The db helper. */
	private static SQLiteOpenHelper dbHelper = null;

	/** The singleton reflection managers map. */
	@SuppressWarnings("rawtypes")
	private static HashMap<Class, ReflectionManager> rmMap = new HashMap<Class, ReflectionManager>();

	/**
	 * Gets the Singleton database helper.
	 * 
	 * @return the dB helper
	 */
	public static SQLiteOpenHelper getDBHelper(Context context) {
		if (dbHelper == null)
			dbHelper = new DefaultDatabaseHelper(context, DATABASE_NAME, DATABASE_VERSION, new Class[] {
					Alarm.class, BasicTrigger.class, WifiConnectedLocation.class },
					new String[] { Alarm.tableName, BasicTrigger.tableName, WifiConnectedLocation.tableName });
		return dbHelper;
	}

	/**
	 * Gets a singleton instance of a reflection manager corresponding to a class.
	 * 
	 * @param cls the class
	 * @return the reflection manager instance
	 */
	@SuppressWarnings("rawtypes")
	public static ReflectionManager getReflectionManagerInstance(Class cls) {
		if (rmMap.containsKey(cls))
			return rmMap.get(cls);
		try {
			ReflectionManager rm = new ReflectionManager(cls);
			rmMap.put(cls, rm);
			return rm;
		} catch (IllegalClassStructureException ex) {
			ex.printStackTrace();
			Logger.getRootLogger().fatal("Illegal Class Structure for class " + cls + ": " + ex.getMessage());
			return null;
		}
	}
}
