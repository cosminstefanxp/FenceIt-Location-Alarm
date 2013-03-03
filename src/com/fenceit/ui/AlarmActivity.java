/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseManager;
import com.fenceit.service.BackgroundService;
import com.fenceit.ui.ActionsFragment.ActionsFragmentContainer;
import com.fenceit.ui.TriggersFragment.TriggersFragmentContainer;

/**
 * The Class AlarmActivity showing the screen for editing an Alarm.
 */
public class AlarmActivity extends DefaultActivity implements TriggersFragmentContainer,
		ActionsFragmentContainer {

	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);

	/** The alarm. */
	private Alarm alarm;

	/** The data access object. */
	private DefaultDAO<Alarm> dao = null;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);

		// Prepare database connections
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), Alarm.class, Alarm.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the alarm
			Bundle extras = getIntent().getExtras();
			Long alarmID = (Long) (extras != null ? extras.get("id") : null);

			fetchAlarm(alarmID);

			// Add the triggers fragment
			Fragment triggersFragment = TriggersFragment.newInstance(alarm.getId());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.alarm_triggersFragmentContainer, triggersFragment).commit();

			// Add the actions fragment
			Fragment actionsFragment = ActionsFragment.newInstance(alarm.getId());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.alarm_actionsFragmentContainer, actionsFragment).commit();
		}
		// If it's a restored instance
		else {
			alarm = (Alarm) savedInstanceState.getSerializable("alarm");
			log.info("Restored saved instance of alarm: " + alarm);
		}

		// Add OnClickListeners
		// ((TextView) findViewById(R.id.alarm_nameText))
		// .setOnEditorActionListener(new LoseFocusOnEditorActionListener());

		refreshActivity();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// If the name was changed, save it
		String newName = ((TextView) findViewById(R.id.alarm_nameText)).getText().toString();
		if (!newName.equals(alarm.getName())) {
			alarm.setName(newName);
			if (storeAlarm(false) == false)
				Toast.makeText(this, "Alarm not updated due to invalid name.", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Fills the fields of the activity with the data from the alarm.
	 */
	private void refreshActivity() {
		if (alarm == null) {
			log.error("No alarm so not filling fields.");
			return;
		}
		((TextView) findViewById(R.id.alarm_nameText)).setText(alarm.getName());
	}

	/**
	 * Fetches the associated alarm from the database, or builds a new one.
	 * 
	 * @param alarmID the alarm id
	 */
	private void fetchAlarm(Long alarmID) {
		if (alarmID != null) {
			log.info("Fetching alarm from database with id: " + alarmID);

			// Get the alarm
			dao.open();
			alarm = dao.fetch(alarmID);
			dao.close();

			log.debug("Fetched alarm: " + alarm);
		} else {
			log.info("Creating new alarm...");
			alarm = new Alarm();
			storeAlarm(true);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("alarm", alarm);
	}

	/**
	 * Store alarm.
	 * 
	 * @return true, if successful
	 */
	private boolean storeAlarm(boolean newAlarm) {
		if (alarm == null) {
			log.error("No alarm to store in database.");
			return false;
		}

		// Check if all data is all right
		if (!alarm.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Notify the background service that a change has been done on an enabled alarm
		if (alarm.isEnabled()) {
			Intent intent = new Intent(this, BackgroundService.class);
			intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
					BackgroundService.SERVICE_EVENT_FORCE_RECHECK);
			startService(intent);
		}

		// Save the alarm to the database
		if (log.isDebugEnabled())
			log.debug("Saving alarm in database: " + alarm);
		dao.open();
		if (newAlarm) {
			long id = dao.insert(alarm, true);
			if (id == -1)
				return false;
			log.info("Successfully saved new alarm with id: " + id);
			alarm.setId(id);
			newAlarm = false;
		} else
			dao.update(alarm, alarm.getId());
		dao.close();
		return true;
	}

	@Override
	public Alarm getCorrespondingAlarm() {
		return alarm;
	}
}
