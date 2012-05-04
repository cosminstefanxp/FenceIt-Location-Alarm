/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseDefaults;
import com.fenceit.db.DefaultDAO;

/**
 * The Class FenceItActivity.
 */
public class FenceItActivity extends Activity implements OnClickListener {

	/** The logger. */
	private static Logger log = Logger.getRootLogger();
	
	ArrayList<Alarm> alarms;
	
	/** The db helper. */
	private static SQLiteOpenHelper dbHelper=null;
	
	/** The dao. */
	private DefaultDAO<Alarm> dao=null;
	
	private static final int REQ_CODE_ADD_ALARM=1;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		
		setContentView(R.layout.main);
		
		//Prepare database connection
		if(dbHelper==null)
			dbHelper=DatabaseDefaults.getDBHelper(getApplicationContext());
		if(dao==null)
			dao=new DefaultDAO<Alarm>(Alarm.class, dbHelper, Alarm.tableName);
		
		//Add listeners
		ImageButton but=(ImageButton)findViewById(R.id.main_addAlarmButton);
		but.setOnClickListener(this);
		
		//Get the alarms
		fetchAlarms();
		
		ListView lv=(ListView) findViewById(R.id.main_alarmList);
		lv.setAdapter(new AlarmAdapter(this, alarms));
	}
	
	/**
	 * Fetch alarms.
	 */
	private void fetchAlarms()
	{
		dao.open();
		alarms=dao.fetchAll(null);
		dao.close();
	}

	/**
	 * Initializes the application environment.
	 */
	private void init() {
		new Log4jConfiguration();
		log.info("Starting up...");
		

	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if(v==findViewById(R.id.main_addAlarmButton))
		{  
			log.debug("Add alarm button clicked.");
			Intent addAlarmActivityIntent=new Intent(this, AlarmActivity.class);
			startActivityForResult(addAlarmActivityIntent, REQ_CODE_ADD_ALARM);
		}
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request "+requestCode+" with result code: "+resultCode);
	}
}