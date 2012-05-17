/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.androwrapee.db.IllegalClassStructureException;
import org.androwrapee.db.ReflectionManager;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.db.DatabaseManager;

public class TriggerActivity extends Activity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(TriggerActivity.class);

	/** The database helper. */
	private static SQLiteOpenHelper dbHelper = null;

	/** The data access object. */
	private DefaultDAO<BasicTrigger> dao = null;

	private BasicTrigger trigger;

	private boolean newEntity;

	private Button saveButton;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trigger);

		// Prepare database connection
		if (dbHelper == null)
			dbHelper = DatabaseManager.getDBHelper(getApplicationContext());
		if (dao == null)
			dao = new DefaultDAO<BasicTrigger>(BasicTrigger.class, dbHelper,
					DatabaseManager.getReflectionManagerInstance(BasicTrigger.class), BasicTrigger.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the trigger
			Bundle extras = getIntent().getExtras();
			Long triggerID = (Long) (extras != null ? extras.get("id") : null);

			Alarm alarm = (Alarm) extras.getSerializable("alarm");
			fetchTrigger(triggerID, alarm);
		}
		// If it's a restored instance
		else {
			trigger = (BasicTrigger) savedInstanceState.getSerializable("trigger");
			log.info("Restored saved instance of trigger: "+trigger);
		}

		// Buttons and others
		saveButton = (Button) findViewById(R.id.trigger_saveButton);
		saveButton.setOnClickListener(this);

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("trigger", trigger);
	}

	/**
	 * Fetches the associated trigger from the database, or builds a new one.
	 * 
	 * @param triggerID
	 */
	private void fetchTrigger(Long triggerID, Alarm alarm) {
		if (triggerID != null) {
			log.info("Fetching trigger from database with id: " + triggerID);
			dao.open();
			trigger = dao.fetch(triggerID);
			dao.close();
			trigger.setAlarm(alarm);
			log.debug("Fetched trigger: " + trigger);
		} else {
			log.info("Creating new trigger for alarm "+alarm+"...");
			trigger = new BasicTrigger(alarm);
			newEntity = true;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == saveButton) {
			log.info("Save button clicked. Storing entity...");
			if(!storeTrigger())
			{
				Toast.makeText(this, "Not all fields are completed corectly. Please check all of them.", Toast.LENGTH_SHORT);
				return;
			}
			setResult(RESULT_OK);
			finish();
			return;
		}

	}

	/**
	 * Stores a trigger.
	 *
	 * @return true, if successful
	 */
	private boolean storeTrigger() {
		// Checks
		if (trigger == null) {
			log.error("No trigger to store in database.");
			return false;
		}
		// Store required data

		// Check if all data is all right
		if (!trigger.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Save the entity to the database
		log.info("Saving trigger in database...");
		dao.open();
		if (newEntity) {
			long id = dao.insert(trigger);
			if (id == -1)
				return false;
			log.info("Successfully saved new trigger with id: " + id);
			trigger.setId(id);
			newEntity = false;
		} else
			dao.update(trigger, trigger.getId());
		dao.close();

		return true;

	}

}
