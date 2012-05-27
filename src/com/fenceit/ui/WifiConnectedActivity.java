/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.alarm.triggers.BasicTrigger.TriggerType;
import com.fenceit.db.DatabaseManager;
import com.fenceit.ui.adapters.AlarmLocationBroker;
import com.fenceit.ui.adapters.SingleChoiceAdapter;

public class WifiConnectedActivity extends Activity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(WifiConnectedActivity.class);

	/** The Constant DIALOG_TYPE. */
	private static final int DIALOG_TRIGGER_TYPE = 1;

	/** The Constant DIALOG_NEW_LOCATION. */
	private static final int DIALOG_NEW_LOCATION = 3;

	/** The database helper. */
	private static SQLiteOpenHelper dbHelper = null;

	/** The data access object. */
	private DefaultDAO<BasicTrigger> dao = null;

	private BasicTrigger trigger;

	private boolean newEntity;

	private Button saveButton;

	private SingleChoiceAdapter<TriggerType> typesAdapter;
	private SingleChoiceAdapter<LocationType> locationsAdapter;

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
			log.info("Restored saved instance of trigger: " + trigger);
		}

		// Buttons and others
		saveButton = (Button) findViewById(R.id.trigger_saveButton);
		saveButton.setOnClickListener(this);

		findViewById(R.id.trigger_whenSection).setOnClickListener(this);
		findViewById(R.id.trigger_locationSection).setOnClickListener(this);
		findViewById(R.id.trigger_locationAddSection).setOnClickListener(this);
		findViewById(R.id.trigger_locationFavoriteSection).setOnClickListener(this);

		// Fill data
		typesAdapter = new SingleChoiceAdapter<BasicTrigger.TriggerType>(new TriggerType[] { TriggerType.ON_ENTER,
				TriggerType.ON_EXIT }, new CharSequence[] { "Arriving at location", "Leaving the location" });
		locationsAdapter = AlarmLocationBroker.getLocationTypesAdapter();
		refreshActivity();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("trigger", trigger);
	}

	/**
	 * Refresh the activity displayed views using the data from the trigger.
	 */
	private void refreshActivity() {
		// Type Section
		((TextView) findViewById(R.id.trigger_whenText)).setText(typesAdapter.getName(trigger.getType()));
		// Location Section
		if (trigger.getLocation() == null) {
			((TextView) findViewById(R.id.trigger_locationAddText)).setText("Define a new location");
			((TextView) findViewById(R.id.trigger_locationFavoriteText)).setText("Use a favorite location");
			((View) findViewById(R.id.trigger_locationSection)).setVisibility(View.GONE);
		} else {
			AlarmLocation loc = trigger.getLocation();
			((TextView) findViewById(R.id.trigger_locationAddText)).setText("Replace with a new location");
			((TextView) findViewById(R.id.trigger_locationFavoriteText)).setText("Replace with a favorite location");
			((View) findViewById(R.id.trigger_locationSection)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.trigger_locationTitleText)).setText(loc.getName());
			((TextView) findViewById(R.id.trigger_locationTypeText)).setText(loc.getTypeDescription()
					+ " location type");
			((TextView) findViewById(R.id.trigger_locationDescText)).setText(loc.getDescription());
			// TODO: change type image
		}
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
			log.info("Creating new trigger for alarm " + alarm + "...");
			trigger = new BasicTrigger(alarm);
			newEntity = true;
		}
	}

	/**
	 * Stores the trigger in the database.
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

	@Override
	public void onClick(View v) {
		if (v == saveButton) {
			log.info("Save button clicked. Storing entity...");
			if (!storeTrigger()) {
				Toast.makeText(this, "Not all fields are completed corectly. Please check all of them.",
						Toast.LENGTH_SHORT);
				return;
			}
			setResult(RESULT_OK);
			finish();
			return;
		} else if (v == (findViewById(R.id.trigger_whenSection))) {
			// Change location type
			showDialog(DIALOG_TRIGGER_TYPE);
		} else if (v == (findViewById(R.id.trigger_locationSection))) {
			// TODO: Edit existing location
			log.debug("Editing the existing location");
		} else if (v == findViewById(R.id.trigger_locationAddSection)) {
			// Create a new location
			showDialog(DIALOG_NEW_LOCATION);
		} else if(v==findViewById(R.id.trigger_locationFavoriteSection)){
			// TODO: Select a favorite location
			log.debug("Using a pre-defined location");
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_NEW_LOCATION:
			// Create the dialog associated with creating a new type of location
			builder.setTitle("New location of type");
			builder.setItems(locationsAdapter.getNames(), new DialogInterface.OnClickListener() {

				// Process the selection
				public void onClick(DialogInterface dialog, int item) {
					log.debug("Selected new location of type: " + locationsAdapter.getValues()[item]);
					// TODO: fill in
					dialog.dismiss();
				}
			});
			// Build the dialog
			dialog = builder.create();
			break;

		case DIALOG_TRIGGER_TYPE:
			// Create the dialog associated with the Type of the Trigger

			builder.setTitle("It is triggered on");
			builder.setSingleChoiceItems(typesAdapter.getNames(), typesAdapter.getIndex(trigger.getType()),
					new DialogInterface.OnClickListener() {

						// Process the selection
						public void onClick(DialogInterface dialog, int item) {
							log.debug("Selected new trigger type: " + typesAdapter.getValues()[item]);
							trigger.setType(typesAdapter.getValues()[item]);
							refreshActivity();
							dialog.dismiss();
						}
					});
			// Build the dialog
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
}
