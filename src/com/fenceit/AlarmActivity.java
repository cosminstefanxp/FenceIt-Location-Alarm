/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.fenceit.alarm.Alarm;
import com.fenceit.db.DefaultDAO;
import com.fenceit.db.DefaultDatabaseHelper;

public class AlarmActivity extends Activity {

	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);

	/** The alarm. */
	private Alarm alarm;

	
	/** The new alarm. */
	private boolean newAlarm=false;

	/** The db helper. */
	private static SQLiteOpenHelper dbHelper=null;
	
	/** The dao. */
	DefaultDAO<Alarm> dao=null;
	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);
		
		//Prepare database connection
		if(dbHelper==null)
			dbHelper=new DefaultDatabaseHelper<Alarm>(getBaseContext(), Alarm.class, Alarm.tableName);
		if(dao==null)
			dao=new DefaultDAO<Alarm>(Alarm.class, dbHelper, Alarm.tableName);

		//Get the alarm id, if any
		Long alarmID = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("id");
		if (alarmID == null) {
			Bundle extras = getIntent().getExtras();
			alarmID = extras != null ? extras.getLong("id") : null;
		}

		//Prepare the associated alarm
		fetchAlarm(alarmID);
	}

	/**
	 * Fetches the associated alarm from the database, or builds a new one.
	 *
	 * @param alarmID the alarm id
	 */
	private void fetchAlarm(Long alarmID) {
		if(alarmID!=null)
		{
			log.info("Fetching alarm from database with id: "+alarmID);
			dao.open();
			alarm=dao.fetch(alarmID);
			log.debug("Fetched alarm: "+alarm);
		}
		if(alarm==null)
		{
			log.info("Creating new alarm...");
			alarm=new Alarm();		
			newAlarm=true;
		}
	}
	
	
}
