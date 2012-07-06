/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.util.ArrayList;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.db.DatabaseManager;
import com.fenceit.ui.adapters.TriggersAdapter;

public class AlarmActivity extends DefaultActivity implements OnClickListener, OnItemClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);

	private static final int REQ_CODE_ADD_TRIGGER = 1;
	private static final int REQ_CODE_EDIT_TRIGGER = 2;

	private static final int DIALOG_ALARM_NAME = 1;

	/** The alarm. */
	private Alarm alarm;

	/** The alarm id. */
	private Long alarmID;

	/** The new alarm. */
	private boolean newAlarm = false;

	/** The database helper. */
	private static SQLiteOpenHelper dbHelper = null;

	/** The data access objects. */
	private DefaultDAO<Alarm> dao = null;
	private DefaultDAO<BasicTrigger> daoTriggers = null;

	private Button saveButton;
	private ImageButton addTriggerButton;

	private ListView triggersLV;

	/** The context menu id. */
	private long contextMenuPosition;

	private TriggersAdapter triggersAdapter;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.warn("On create..." + this.alarmID + "--" + savedInstanceState);
		setContentView(R.layout.alarm);

		// Prepare database connection
		if (dbHelper == null)
			dbHelper = DatabaseManager.getDBHelper(getApplicationContext());
		if (dao == null)
			dao = new DefaultDAO<Alarm>(Alarm.class, dbHelper,
					DatabaseManager.getReflectionManagerInstance(Alarm.class), Alarm.tableName);
		if (daoTriggers == null)
			daoTriggers = new DefaultDAO<BasicTrigger>(BasicTrigger.class, dbHelper,
					DatabaseManager.getReflectionManagerInstance(BasicTrigger.class), BasicTrigger.tableName);

		// Get the alarm id, if any
		alarmID = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("id");
		if (alarmID == null) {
			Bundle extras = getIntent().getExtras();
			alarmID = extras != null ? extras.getLong("id") : null;
		}

		findViewById(R.id.alarm_nameSection).setOnClickListener(this);
		findViewById(R.id.alarm_enabledSection).setOnClickListener(this);

		saveButton = (Button) findViewById(R.id.alarm_saveButton);
		saveButton.setOnClickListener(this);

		addTriggerButton = (ImageButton) findViewById(R.id.alarm_addTriggerButton);
		addTriggerButton.setOnClickListener(this);

		triggersLV = (ListView) findViewById(R.id.alarm_triggersListView);
		registerForContextMenu(triggersLV);
		triggersLV.setOnItemClickListener(this);

		// Fill in the data
		fetchAlarm(alarmID);
		fillFields();
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart() */
	@Override
	protected void onStart() {
		super.onStart();
		log.debug("Alarm Activity onStart method running...");

	}

	/**
	 * Fills the fields of the activity with the data from the alarm.
	 */
	private void fillFields() {
		if (alarm == null) {
			log.error("No alarm so not filling fields.");
			return;
		}

		((TextView) findViewById(R.id.alarm_nameText)).setText(alarm.getName());
		if (alarm.isEnabled())
			((TextView) findViewById(R.id.alarm_enabledText)).setText("Alarm ENABLED");
		else
			((TextView) findViewById(R.id.alarm_enabledText)).setText("Alarm DISABLED");

		triggersAdapter = new TriggersAdapter(this, alarm.getTriggers());
		triggersLV.setAdapter(triggersAdapter);
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

			fetchTriggers();

			log.debug("Fetched alarm: " + alarm);
		} else {
			log.info("Creating new alarm...");
			alarm = new Alarm();
			newAlarm = true;
		}
	}

	/**
	 * Fetches the associated triggers from the database.
	 */
	private void fetchTriggers() {
		// Get the associated triggers
		daoTriggers.open();
		ArrayList<BasicTrigger> triggers = daoTriggers.fetchAll(DefaultDAO.REFERENCE_PREPENDER + "alarm="
				+ alarm.getId());
		daoTriggers.close();
		alarm.getTriggers().clear();
		if (triggers != null)
			alarm.getTriggers().addAll(triggers);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause() */
	@Override
	protected void onPause() {
		super.onPause();
		log.warn("On pause...");
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle) */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		log.warn("On restore instance state..." + savedInstanceState);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle) */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("t", "dick");
		log.warn("On save instance state..." + outState);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop() */
	@Override
	protected void onStop() {
		super.onStop();
		log.warn("On stop...");
	}

	/**
	 * Store alarm.
	 * 
	 * @return true, if successful
	 */
	private boolean storeAlarm() {
		if (alarm == null) {
			log.error("No alarm to store in database.");
			return false;
		}

		// Check if all data is all right
		if (!alarm.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Save the alarm to the database
		log.info("Saving alarm in database...");
		dao.open();
		if (newAlarm) {
			long id = dao.insert(alarm);
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

	/* (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View) */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.alarm_saveButton:
			log.info("Save button clicked. Storing alarm...");
			if (!storeAlarm()) {
				Toast.makeText(this, "Not all fields are completed corectly. Please check all of them.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			setResult(RESULT_OK);
			finish();
			return;
		case R.id.alarm_addTriggerButton:
			log.info("Add trigger button clicked.");
			Intent addTriggerActivityIntent = new Intent(this, TriggerActivity.class);
			addTriggerActivityIntent.putExtra("alarm", alarm);
			startActivityForResult(addTriggerActivityIntent, REQ_CODE_ADD_TRIGGER);
			break;
		case R.id.alarm_nameSection:
			showDialog(DIALOG_ALARM_NAME);
			break;
		case R.id.alarm_enabledSection:
			if (alarm.isEnabled()) {
				alarm.setEnabled(false);
				((TextView) findViewById(R.id.alarm_enabledText)).setText("Alarm DISABLED");
			} else {
				alarm.setEnabled(true);
				((TextView) findViewById(R.id.alarm_enabledText)).setText("Alarm ENABLED");
			}
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request " + requestCode + " with result code: " + resultCode);
		if (resultCode == RESULT_OK && (requestCode == REQ_CODE_ADD_TRIGGER || requestCode == REQ_CODE_EDIT_TRIGGER)) {
			log.debug("Refreshing alarms...");
			fetchTriggers();
			refreshTriggersListView();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (v == triggersLV) {
			// Check which list item was selected
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			contextMenuPosition = info.position;
			log.debug("Selected list item on position: " + contextMenuPosition);

			// Inflate the menu
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.alarm_list_trigger_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_alarm_list_trigger_delete:
			deleteTrigger(alarm.getTriggers().get(info.position));
			refreshTriggersListView();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void refreshTriggersListView() {
		triggersAdapter.setTriggers(alarm.getTriggers());

	}

	private void deleteTrigger(AlarmTrigger alarmTrigger) {
		log.info("Deleting trigger: " + alarmTrigger);
		daoTriggers.open();
		daoTriggers.delete(alarmTrigger.getId());
		alarm.getTriggers().remove(alarmTrigger);
		daoTriggers.close();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if (parent == triggersLV) {
			log.info("ListView item click for editing trigger with id " + id);
			Intent editActivityIntent = new Intent(this, TriggerActivity.class);
			editActivityIntent.putExtra("id", id);
			startActivityForResult(editActivityIntent, REQ_CODE_EDIT_TRIGGER);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_ALARM_NAME:
			// Create the dialog associated with setting Alarm name
			builder.setTitle("Alarm name");
			builder.setMessage("Set the alarm name:");
			// Prepare the text edit, including with margins
			LayoutInflater factory = LayoutInflater.from(this);
			View nameDialogView = factory.inflate(R.layout.dialog_edit_text_layout, null);
			final EditText nameText = (EditText) nameDialogView.findViewById(R.id.dialog_editText);
			nameText.setText(alarm.getName());
			builder.setView(nameDialogView);

			// Only use an OK button
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					alarm.setName(nameText.getText().toString());
					((TextView) findViewById(R.id.alarm_nameText)).setText(alarm.getName());
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
