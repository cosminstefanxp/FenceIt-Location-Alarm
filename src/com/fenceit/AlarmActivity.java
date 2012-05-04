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
import android.widget.EditText;

import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseDefaults;
import com.fenceit.db.DefaultDAO;

public class AlarmActivity extends Activity {

	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);

	/** The alarm. */
	private Alarm alarm;

	/** The alarm id. */
	private Long alarmID;
	
	/** The new alarm. */
	private boolean newAlarm=false;

	/** The db helper. */
	private static SQLiteOpenHelper dbHelper=null;
	
	/** The dao. */
	private DefaultDAO<Alarm> dao=null;
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
			dbHelper=DatabaseDefaults.getDBHelper(getApplicationContext());
		if(dao==null)
			dao=new DefaultDAO<Alarm>(Alarm.class, dbHelper, Alarm.tableName);

		//Get the alarm id, if any
		Long alarmID = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("id");
		if (alarmID == null) {
			Bundle extras = getIntent().getExtras();
			alarmID = extras != null ? extras.getLong("id") : null;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		log.debug("Alarm Activity onStart method running...");
		
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
			dao.close();
			log.debug("Fetched alarm: "+alarm);
		}
		if(alarm==null)
		{
			log.info("Creating new alarm...");
			alarm=new Alarm();		
			newAlarm=true;
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		storeAlarm();
	}

	/**
	 * Store alarm.
	 *
	 * @return true, if successful
	 */
	private boolean storeAlarm()
	{
		if(alarm==null)
		{
			log.error("No alarm to store in database.");
			return false;
		}
		
		//Get data from fields
		alarm.setName(((EditText)findViewById(R.id.alarm_nameTextField)).getText().toString());
		
		if(!alarm.isComplete())
		{
			log.error("Not all required fields are filled in");
			return false;
		}
		
		log.info("Saving alarm in database...");
		dao.open();
		if(newAlarm)
		{
			long id=dao.insert(alarm);
			if(id==-1)
				return false;
			log.info("Successfully saved new alarm with id: "+id);
			alarm.setId(id);
			newAlarm=false;
		}
		else
			dao.update(alarm, alarm.getId());
		dao.close();
		return true;
	}
	
	
}
