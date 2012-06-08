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
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.Wifi;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.alarm.triggers.BasicTrigger.TriggerType;
import com.fenceit.db.AlarmLocationBroker;
import com.fenceit.db.DatabaseManager;
import com.fenceit.ui.adapters.SingleChoiceAdapter;

public class TriggerActivity extends Activity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(TriggerActivity.class);

	/** The Constant DIALOG_TYPE. */
	private static final int DIALOG_TRIGGER_TYPE = 1;

	/** The Constant DIALOG_NEW_LOCATION. */
	private static final int DIALOG_NEW_LOCATION = 3;

	/** The Constant REQ_CODE_EDIT_LOCATION. */
	private static final int REQ_CODE_EDIT_LOCATION = 4;

	/** The Constant REQ_CODE_NEW_LOCATION. */
	private static final int REQ_CODE_NEW_LOCATION = 5;

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
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), BasicTrigger.class, BasicTrigger.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the trigger
			Bundle extras = getIntent().getExtras();
			Long triggerID = (Long) (extras != null ? extras.get("id") : null);

			Wifi alarm = (Wifi) extras.getSerializable("alarm");
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
	private void fetchTrigger(Long triggerID, Wifi alarm) {
		if (triggerID != null) {
			// Trigger
			log.info("Fetching trigger from database with id: " + triggerID);
			dao.open();
			Cursor cursor = dao.fetchCursor(triggerID);
			try {
				trigger = dao.buildObject(cursor);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
			// Location
			long locID = dao.getReferenceId(cursor, "location");
			log.info("Fetching associated location with id: " + locID);
			trigger.setLocation(AlarmLocationBroker.fetchLocation(getApplicationContext(), locID));

			cursor.close();
			dao.close();
			// Alarm
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
						Toast.LENGTH_SHORT).show();
				return;
			}
			setResult(RESULT_OK);
			finish();
			return;
		} else if (v == (findViewById(R.id.trigger_whenSection))) {
			// Change location type
			showDialog(DIALOG_TRIGGER_TYPE);

		} else if (v == (findViewById(R.id.trigger_locationSection))) {
			log.debug("Editing the existing location");
			Intent intent = AlarmLocationBroker.getActivityIntent(this, trigger.getLocation().getType());
			intent.putExtra("id", trigger.getLocation().getId());
			startActivityForResult(intent, REQ_CODE_EDIT_LOCATION);

		} else if (v == findViewById(R.id.trigger_locationAddSection)) {
			// Create a new location
			showDialog(DIALOG_NEW_LOCATION);
		} else if (v == findViewById(R.id.trigger_locationFavoriteSection)) {
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
					log.debug("Creating new location of type: " + locationsAdapter.getValues()[item]);
					startActivityForNewLocation(locationsAdapter.getValues()[item]);
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

	/**
	 * Start activity for new location.
	 */
	private void startActivityForNewLocation(LocationType type) {
		Intent intent = AlarmLocationBroker.getActivityIntent(this, type);
		startActivityForResult(intent, REQ_CODE_NEW_LOCATION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request " + requestCode + " with result code: " + resultCode);
		if (resultCode == RESULT_OK && (requestCode == REQ_CODE_EDIT_LOCATION || requestCode == REQ_CODE_NEW_LOCATION)) {
			log.debug("Refreshing location...");
			long id = data.getLongExtra("id", -1);
			log.debug("The updated location has id: " + id);
			trigger.setLocation(AlarmLocationBroker.fetchLocation(getApplicationContext(), id));

			refreshActivity();
		}
	}
}
