/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.util.ArrayList;
import java.util.List;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.content.Context;
import android.database.Cursor;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.triggers.BasicTrigger;

/**
 * The DatabaseAccessor offers some high level processing of items from the database.
 */
public class DatabaseAccessor {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(DatabaseAccessor.class);

	/**
	 * Build the full alarms (going recursively for every field in the object) that match the given
	 * where clause.
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
	 * Build the full triggers (going recursively for every field in the object) that match the
	 * given where clause.
	 * 
	 * @param ctx the context
	 * @param whereClause the where clause
	 * @return the full triggers
	 */
	public static List<BasicTrigger> buildFullTriggers(Context ctx, String whereClause) {

		DefaultDAO<BasicTrigger> dao = DatabaseManager.getDAOInstance(ctx, BasicTrigger.class, BasicTrigger.tableName);
		dao.open();

		// Fetch the triggers from the database
		List<BasicTrigger> triggers = new ArrayList<BasicTrigger>();
		Cursor cursor = dao.fetchCursor(whereClause);
		if (cursor == null || cursor.getCount() == 0) {
			cursor.close();
			return triggers;
		}

		// Build all objects from the cursor
		while (!cursor.isAfterLast()) {

			// Get the trigger
			BasicTrigger trigger;
			try {
				trigger = dao.buildObject(cursor);
			} catch (Exception e) {
				log.error("An error occured while building the triggers from cursor: " + cursor, e);
				continue;
			}
			triggers.add(trigger);

			// Fill the trigger with the corresponding location
			long locationID = dao.getReferenceId(cursor, "location");
			trigger.setLocation(AlarmLocationBroker.fetchLocation(ctx, locationID));

			// Advance the cursor
			cursor.moveToNext();
		}

		dao.close();
		cursor.close();
		return triggers;
	}
}
