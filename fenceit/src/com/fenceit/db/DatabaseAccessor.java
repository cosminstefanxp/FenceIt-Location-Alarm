/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.alarm.triggers.BasicTrigger;

/**
 * The DatabaseAccessor offers some high level processing of items from the database.
 */
public class DatabaseAccessor {

	private static final String QUERY_SELECT_TRIGGERS_LOCATION_TYPE = "SELECT t.* FROM triggers t, alarms a "
			+ "WHERE a._id_id=t._rid_alarm AND a.enabled=\'#t\' and t.locationType=?";
	/** The Constant log. */
	private static final Logger log = Logger.getLogger(DatabaseAccessor.class);

	/**
	 * Fetches the alarm id for a given trigger. Involves running an SQL select.
	 * 
	 * @param ctx the context
	 * @param trigger the trigger
	 * @return the alarm id
	 */
	public static long fetchAlarmIdForTrigger(Context ctx, AlarmTrigger trigger) {
		DefaultDAO<BasicTrigger> dao = DatabaseManager.getDAOInstance(ctx, BasicTrigger.class, BasicTrigger.tableName);
		dao.open();

		Cursor cursor = dao.fetchCursor(trigger.getId());
		long ret = dao.getReferenceId(cursor, "alarm");

		dao.close();
		return ret;
	}

	/**
	 * Build the alarm corresponding to a trigger and makes the connection between them.
	 * <p>
	 * NOTE - Does NOT fetch either the other triggers or the actions.
	 * </p>
	 * 
	 * @param ctx the context
	 * @param trigger the trigger
	 */
	public static void fillAlarmForTrigger(Context ctx, BasicTrigger trigger) {
		DefaultDAO<Alarm> daoAlarms = DatabaseManager.getDAOInstance(ctx, Alarm.class, Alarm.tableName);
		daoAlarms.open();

		// Execute the SQL query to get the required alarm
		SQLiteDatabase mDb = daoAlarms.getDatabaseReference();
		Cursor cursor = mDb.rawQuery("SELECT a.* FROM triggers t, alarms a WHERE a." + DefaultDAO.ID_PREPENDER
				+ "id=t." + DefaultDAO.REFERENCE_PREPENDER + "alarm AND t." + DefaultDAO.ID_PREPENDER + "id=?",
				new String[] { Long.toString(trigger.getId()) });

		// Checks
		if (cursor.getCount() != 1) {
			if (cursor.getCount() <= 0) {
				log.error("No alarms where found matching trigger: " + trigger);
				return;
			} else
				log.error("Too many alarms where generated when creating alarm for trigger: " + trigger);
		}

		// Build the alarm and connect it with the trigger
		Alarm alarm;
		try {
			cursor.moveToFirst();
			alarm = daoAlarms.buildObject(cursor);
		} catch (Exception e) {
			log.error("An error occured while building the alarm from cursor: " + cursor, e);
			cursor.close();
			return;
		}

		// Make the connection
		alarm.addTrigger(trigger);
		trigger.setAlarm(alarm);

		cursor.close();
		daoAlarms.close();
	}

	/**
	 * Builds the full triggers of enabled alarms which are associated to a given type of location.
	 * 
	 * <p>
	 * NOTE - The alarm field of the triggers is set to null, as no reference to alarms is set in this method.
	 * </p>
	 * 
	 * @param ctx the ctx
	 * @param type the type
	 * @return the list of triggers
	 */
	public static List<BasicTrigger> buildFullTriggersForEnabledLocationType(Context ctx, LocationType type) {

		DefaultDAO<BasicTrigger> dao = DatabaseManager.getDAOInstance(ctx, BasicTrigger.class, BasicTrigger.tableName);
		dao.open();

		// Execute the SQL query to only get triggers of enabled alarms which are associated to the given type
		// of location
		SQLiteDatabase mDb = dao.getDatabaseReference();
		Cursor cursor = mDb.rawQuery(QUERY_SELECT_TRIGGERS_LOCATION_TYPE, new String[] { type.toString() });

		// Check for triggers
		if (cursor == null || cursor.getCount() <= 0) {
			cursor.close();
			return Collections.<BasicTrigger> emptyList();
		}

		// Build the triggers from the cursor
		List<BasicTrigger> triggers = new ArrayList<BasicTrigger>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			// Get the trigger
			BasicTrigger trigger;
			try {
				trigger = dao.buildObject(cursor);
			} catch (Exception e) {
				log.error("An error occured while building the triggers from cursor: " + cursor, e);
				cursor.moveToNext();
				continue;
			}
			triggers.add(trigger);

			// Fill the trigger with the corresponding location
			long locationID = dao.getReferenceId(cursor, "location");
			trigger.setLocation(AlarmLocationBroker.fetchLocation(ctx, locationID, trigger.getLocationType()));

			// Advance the cursor
			cursor.moveToNext();
		}

		dao.close();
		cursor.close();
		return triggers;

	}

	/**
	 * Build the full alarms (going recursively for every field in the object) that match the given where
	 * clause.
	 * 
	 * <p>
	 * NOTE - Does not fill in the corresponding Actions
	 * </p>
	 * 
	 * @param ctx the context
	 * @param whereClause the where clause
	 * @return the full triggers
	 */
	public static List<Alarm> buildFullAlarms(Context ctx, String whereClause) {

		DefaultDAO<Alarm> daoAlarms = DatabaseManager.getDAOInstance(ctx, Alarm.class, Alarm.tableName);
		daoAlarms.open();

		// Fetch all alarms
		List<Alarm> alarms = daoAlarms.fetchAll(whereClause);

		// Fill alarms
		for (Alarm alarm : alarms) {

			// Fetch associated triggers
			List<BasicTrigger> triggers = buildFullTriggers(ctx,
					DefaultDAO.REFERENCE_PREPENDER + "alarm=" + alarm.getId());
			for (BasicTrigger t : triggers) {
				t.setAlarm(alarm);
				alarm.addTrigger(t);
			}
		}

		daoAlarms.close();
		return alarms;
	}

	/**
	 * Build the full triggers (going recursively for every field in the object) that match the given where
	 * clause.
	 * 
	 * @param ctx the context
	 * @param whereClause the where clause
	 * @return the full triggers
	 */
	public static List<BasicTrigger> buildFullTriggers(Context ctx, String whereClause) {

		DefaultDAO<BasicTrigger> dao = DatabaseManager.getDAOInstance(ctx, BasicTrigger.class, BasicTrigger.tableName);
		dao.open();

		// Fetch the triggers from the database
		Cursor cursor = dao.fetchCursor(whereClause);
		if (cursor == null || cursor.getCount() <= 0) {
			cursor.close();
			return Collections.<BasicTrigger> emptyList();
		}

		// Build all objects from the cursor
		List<BasicTrigger> triggers = new ArrayList<BasicTrigger>();
		while (!cursor.isAfterLast()) {

			// Get the trigger
			BasicTrigger trigger;
			try {
				trigger = dao.buildObject(cursor);
			} catch (Exception e) {
				log.error("An error occured while building the triggers from cursor: " + cursor, e);
				cursor.moveToNext();
				continue;
			}
			triggers.add(trigger);

			// Fill the trigger with the corresponding location
			long locationID = dao.getReferenceId(cursor, "location");
			trigger.setLocation(AlarmLocationBroker.fetchLocation(ctx, locationID, trigger.getLocationType()));

			// Advance the cursor
			cursor.moveToNext();
		}

		dao.close();
		cursor.close();
		return triggers;
	}

	/**
	 * Gets the location types enabled. By enabled location types it is meant a {@link LocationType} which is
	 * used by an {@link AlarmTrigger} contained in an enabled {@link Alarm}.
	 * <p>
	 * This method performs SQL queries in the database to check for the required condition.
	 * </p>
	 * 
	 * @return the location types enabled
	 */
	public static List<LocationType> getLocationTypesEnabled(Context context) {
		LocationType[] types = AlarmLocationBroker.getLocationTypes();
		ArrayList<LocationType> result = new ArrayList<LocationType>();

		// Prepare the SQL Statement
		SQLiteDatabase mDb = DatabaseManager.getDBHelper(context).multiThreadOpen();
		SQLiteStatement s = mDb.compileStatement("SELECT COUNT(*) FROM alarms a, triggers t "
				+ "WHERE a._id_id=t._rid_alarm AND a.enabled=\'#t\' and locationType=?");

		// Check which location types are enabled
		for (LocationType type : types) {
			s.bindString(1, type.toString());
			long count = s.simpleQueryForLong();
			log.warn("For " + type + ": " + count);
			if (count > 0)
				result.add(type);
		}

		// Close the database
		DatabaseManager.getDBHelper(context).multiThreadClose();

		return result;
	}
}
