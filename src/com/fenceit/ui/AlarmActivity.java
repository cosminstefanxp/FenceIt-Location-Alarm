/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.DefaultDAO;
import org.androwrapee.IllegalClassStructureException;
import org.androwrapee.ReflectionManager;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseDefaults;

public class AlarmActivity extends Activity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);
	
	private static final int REQ_CODE_ADD_TRIGGER=1;

	/** The alarm. */
	private Alarm alarm;

	/** The alarm id. */
	private Long alarmID;
	
	/** The new alarm. */
	private boolean newAlarm=false;

	/** The database helper. */
	private static SQLiteOpenHelper dbHelper=null;
	
	/** The data access object. */
	private DefaultDAO<Alarm> dao=null;
	
	/** The triggers list view. */
	ListView triggersLV;

	private Button saveButton;
	private ImageButton addTriggerButton;
	
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
			try {
				dao=new DefaultDAO<Alarm>(Alarm.class, dbHelper, new ReflectionManager(Alarm.class), Alarm.tableName);
			} catch (IllegalClassStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		//Get the alarm id, if any
		alarmID = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("id");
		if (alarmID == null) {
			Bundle extras = getIntent().getExtras();
			alarmID = extras != null ? extras.getLong("id") : null;
		}
		
		saveButton = (Button) findViewById(R.id.alarm_saveButton);
		saveButton.setOnClickListener(this);


		addTriggerButton = (ImageButton) findViewById(R.id.alarm_addTriggerButton);
		addTriggerButton.setOnClickListener(this);
		
		triggersLV=(ListView) findViewById(R.id.alarm_triggersListView);
		
		//Fill in the data
		fetchAlarm(alarmID);
		fillFields();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		log.debug("Alarm Activity onStart method running...");
		
	}
	
	/**
	 * Fills the fields of the activity with the data from the alarm.
	 */
	private void fillFields()
	{
		if(alarm==null)
		{
			log.error("No alarm so not filling fields.");
			return;
		}
		
		((EditText)findViewById(R.id.alarm_nameTextField)).setText(alarm.getName());
		((CheckBox)findViewById(R.id.alarm_enabledCheckbox)).setChecked(alarm.isEnabled());
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
		else
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
		alarm.setEnabled(((CheckBox)findViewById(R.id.alarm_enabledCheckbox)).isChecked());
		
		//Check if all data is all right
		if(!alarm.isComplete())
		{
			log.error("Not all required fields are filled in");
			return false;
		}
		
		//Save the alarm to the database
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

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == saveButton) {
			log.info("Save button clicked. Storing alarm...");
			storeAlarm();
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (v == addTriggerButton) {
			log.info("Add trigger button clicked.");
			Intent addTriggerActivityIntent = new Intent(this, TriggerActivity.class);
			startActivityForResult(addTriggerActivityIntent, REQ_CODE_ADD_TRIGGER);
		}
	}
	
	
}
