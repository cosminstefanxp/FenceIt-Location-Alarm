/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.util.HashMap;

import org.androwrapee.db.DefaultDAO;
import org.androwrapee.db.DefaultDatabaseHelper;
import org.androwrapee.db.IllegalClassStructureException;
import org.androwrapee.db.ReflectionManager;
import org.apache.log4j.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.fenceit.alarm.Wifi;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.locations.WifisDetectedLocation;
import com.fenceit.alarm.triggers.BasicTrigger;

/**
 * The Class DatabaseDefaults.
 */
public class DatabaseManager {

	/** The DATABASE NAME. */
	public static final String DATABASE_NAME = "fenceit.db";

	/** The DATABASE VERSION. */
	public static final int DATABASE_VERSION = 3;

	/** The db helper. */
	private static SQLiteOpenHelper dbHelper = null;

	/** The singleton reflection managers map. */
	@SuppressWarnings("rawtypes")
	private static HashMap<Class, ReflectionManager> rmMap = new HashMap<Class, ReflectionManager>();

	@SuppressWarnings("rawtypes")
	private static HashMap<Class, DefaultDAO> daoMap = new HashMap<Class, DefaultDAO>();

	/**
	 * Gets the Singleton database helper.
	 * 
	 * @return the dB helper
	 */
	public static SQLiteOpenHelper getDBHelper(Context context) {
		if (dbHelper == null)
			dbHelper = new DefaultDatabaseHelper(context, DATABASE_NAME, DATABASE_VERSION, new Class[] { Wifi.class,
					BasicTrigger.class, WifiConnectedLocation.class, WifisDetectedLocation.class }, new String[] {
					Wifi.tableName, BasicTrigger.tableName, WifiConnectedLocation.tableName,
					WifisDetectedLocation.tableName });
		return dbHelper;
	}

	/**
	 * Gets a singleton instance of a reflection manager corresponding to a class.
	 * 
	 * @param cls the class
	 * @return the reflection manager instance
	 */
	public static <T> ReflectionManager getReflectionManagerInstance(Class<T> cls) {
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

	/**
	 * Gets a singleton instance of a DefaultDAO object corresponding to a class.
	 * 
	 * @param <T> the generic type
	 * @param cls the class
	 * @param tableName the table name
	 * @return the DAO instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> DefaultDAO<T> getDAOInstance(Context context, Class<T> cls, String tableName) {
		if (daoMap.containsKey(cls))
			return daoMap.get(cls);
		DefaultDAO<T> dao = new DefaultDAO<T>(cls, DatabaseManager.getDBHelper(context),
				DatabaseManager.getReflectionManagerInstance(cls), tableName);
		daoMap.put(cls, dao);
		return dao;
	}
}
