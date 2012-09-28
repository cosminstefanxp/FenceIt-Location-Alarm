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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.actions.ActionType;
import com.fenceit.alarm.actions.AlarmAction;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.db.AlarmActionBroker;
import com.fenceit.db.DatabaseManager;
import com.fenceit.service.BackgroundService;
import com.fenceit.ui.adapters.ActionsAdapter;
import com.fenceit.ui.adapters.TriggersAdapter;

/**
 * The Class AlarmActivity showing the screen for editing an Alarm.
 */
public class AlarmActivity extends DefaultActivity implements OnClickListener, OnItemClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);

	/** The Constant REQ_CODE_ADD_TRIGGER used as a request code when creating a new trigger. */
	private static final int REQ_CODE_ADD_TRIGGER = 1;

	/** The Constant REQ_CODE_EDIT_TRIGGER used as a request code when editing a trigger. */
	private static final int REQ_CODE_EDIT_TRIGGER = 2;

	/** The Constant REQ_CODE_ADD_ACTION used as a request code when creating a new action. */
	private static final int REQ_CODE_ADD_ACTION = 3;

	/** The Constant REQ_CODE_EDIT_ACTION used as a request code when editing an action. */
	private static final int REQ_CODE_EDIT_ACTION = 4;

	/**
	 * The Constant DIALOG_ALARM_NAME used to identify the dialog that allows the user to set the
	 * alarm name.
	 */
	private static final int DIALOG_ALARM_NAME = 1;

	/** The alarm. */
	private Alarm alarm;

	/** The new alarm. */
	private boolean newAlarm = false;

	/** The data access object. */
	private DefaultDAO<Alarm> dao = null;

	/** The DAO for triggers. */
	private DefaultDAO<BasicTrigger> daoTriggers = null;

	/** The context menu id. */
	private long contextMenuPosition;

	/** The triggers adapter. */
	private TriggersAdapter triggersAdapter;

	/** The actions adapter. */
	private ActionsAdapter actionsAdapter;

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
		if (daoTriggers == null)
			daoTriggers = DatabaseManager.getDAOInstance(getApplicationContext(), BasicTrigger.class,
					BasicTrigger.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the alarm
			Bundle extras = getIntent().getExtras();
			Long alarmID = (Long) (extras != null ? extras.get("id") : null);

			fetchAlarm(alarmID);
		}
		// If it's a restored instance
		else {
			alarm = (Alarm) savedInstanceState.getSerializable("alarm");
			log.info("Restored saved instance of alarm: " + alarm);
		}

		// Add OnClickListeners
		findViewById(R.id.alarm_nameSection).setOnClickListener(this);
		findViewById(R.id.alarm_enabledSection).setOnClickListener(this);
		findViewById(R.id.alarm_saveButton).setOnClickListener(this);
		findViewById(R.id.alarm_addTriggerButton).setOnClickListener(this);
		findViewById(R.id.alarm_addActionButton).setOnClickListener(this);

		// Set up triggers list view and adapter
		triggersAdapter = new TriggersAdapter(this, alarm.getTriggers());
		ListView triggersLV = (ListView) findViewById(R.id.alarm_triggersListView);
		triggersLV.setAdapter(triggersAdapter);
		registerForContextMenu(triggersLV);
		triggersLV.setOnItemClickListener(this);

		// Set up actions list view and adapter
		actionsAdapter = new ActionsAdapter(this, alarm.getActions());
		ListView actionsLV = (ListView) findViewById(R.id.alarm_actionsListView);
		actionsLV.setAdapter(actionsAdapter);
		registerForContextMenu(actionsLV);
		actionsLV.setOnItemClickListener(this);

		// Refresh the activity
		refreshActivity();
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
		if (alarm.isEnabled())
			((TextView) findViewById(R.id.alarm_enabledText)).setText("Alarm ENABLED");
		else
			((TextView) findViewById(R.id.alarm_enabledText)).setText("Alarm DISABLED");
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
			fetchActions();

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

	/**
	 * Fetches the associated actions from the database.
	 */
	private void fetchActions() {
		alarm.getActions().clear();
		alarm.getActions().addAll(
				AlarmActionBroker.fetchAllActions(getApplicationContext(), DefaultDAO.REFERENCE_PREPENDER + "alarm="
						+ alarm.getId()));
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

		// Notify the background service that a change has been done on an enabled alarm
		if (alarm.isEnabled()) {
			Intent intent = new Intent(this, BackgroundService.class);
			intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME, BackgroundService.SERVICE_EVENT_RESET_ALARMS);
			startService(intent);
		}

		// Save the alarm to the database
		log.info("Saving alarm in database...");
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
			// If it's a new alarm, we have to store it in the database so we can get an id and
			// connect the trigger with it
			if (newAlarm)
				storeAlarm();

			// Launch the Activity to define the trigger
			Intent addTriggerActivityIntent = new Intent(this, TriggerActivity.class);
			addTriggerActivityIntent.putExtra("alarm", alarm);
			startActivityForResult(addTriggerActivityIntent, REQ_CODE_ADD_TRIGGER);
			break;
		case R.id.alarm_addActionButton:
			log.info("Add action button clicked.");
			// If it's a new alarm, we have to store it in the database so we can get an id and
			// connect the action with it
			if (newAlarm)
				storeAlarm();

			// Launch the Activity to define the action
			Intent addActionActivityIntent = new Intent(this, RingerModeActivity.class);
			addActionActivityIntent.putExtra("alarm", alarm);
			startActivityForResult(addActionActivityIntent, REQ_CODE_ADD_ACTION);
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

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent) */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request " + requestCode + " with result code: " + resultCode);
		if (resultCode == RESULT_OK && (requestCode == REQ_CODE_ADD_TRIGGER || requestCode == REQ_CODE_EDIT_TRIGGER)) {
			log.debug("Refreshing alarms...");
			fetchTriggers();
			refreshTriggersListView();
		}
		if (resultCode == RESULT_OK && (requestCode == REQ_CODE_ADD_ACTION || requestCode == REQ_CODE_EDIT_ACTION)) {
			log.debug("Refreshing alarms...");
			fetchActions();
			refreshActionsListView();
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View,
	 * android.view.ContextMenu.ContextMenuInfo) */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		switch (v.getId()) {
		case R.id.alarm_triggersListView:
			// Check which list item was selected
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			contextMenuPosition = info.position;
			log.debug("Selected Triggers list item on position: " + contextMenuPosition);

			// Inflate the menu
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.alarm_list_trigger_menu, menu);
			break;
		case R.id.alarm_actionsListView:
			// Check which list item was selected
			AdapterView.AdapterContextMenuInfo info2 = (AdapterView.AdapterContextMenuInfo) menuInfo;
			contextMenuPosition = info2.position;
			log.debug("Selected Actions list item on position: " + contextMenuPosition);

			// Inflate the menu
			MenuInflater inflater2 = getMenuInflater();
			inflater2.inflate(R.menu.alarm_list_action_menu, menu);
			break;
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem) */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_alarm_list_trigger_delete:
			deleteTrigger(alarm.getTriggers().get(info.position));
			refreshTriggersListView();
			return true;
		case R.id.menu_alarm_list_action_delete:
			deleteAction(alarm.getActions().get(info.position));
			refreshActionsListView();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * Refresh triggers list view.
	 */
	private void refreshTriggersListView() {
		triggersAdapter.setTriggers(alarm.getTriggers());
	}

	/**
	 * Refresh actions list view.
	 */
	private void refreshActionsListView() {
		actionsAdapter.setActions(alarm.getActions());
	}

	/**
	 * Delete trigger.
	 * 
	 * @param alarmTrigger the alarm trigger
	 */
	private void deleteTrigger(AlarmTrigger alarmTrigger) {
		log.info("Deleting trigger: " + alarmTrigger);
		daoTriggers.open();
		daoTriggers.delete(alarmTrigger.getId());
		alarm.getTriggers().remove(alarmTrigger);
		daoTriggers.close();
	}

	/**
	 * Delete action.
	 * 
	 * @param alarmAction the alarm action
	 */
	private void deleteAction(AlarmAction alarmAction) {
		log.info("Deleting action: " + alarmAction);
		AlarmActionBroker.deleteAction(getApplicationContext(), alarmAction);
		alarm.getActions().remove(alarmAction);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 * android.view.View, int, long) */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		log.debug("On Item click on position " + position + " for view: " + view);
		switch (parent.getId()) {
		case R.id.alarm_triggersListView:
			log.info("ListView item click for editing trigger with id " + id);
			Intent editTriggerActivityIntent = new Intent(this, TriggerActivity.class);
			editTriggerActivityIntent.putExtra("id", id);
			startActivityForResult(editTriggerActivityIntent, REQ_CODE_EDIT_TRIGGER);
			break;
		case R.id.alarm_actionsListView:
			log.info("ListView item click for editing action with id " + id);
			Intent editActionActivityIntent = AlarmActionBroker.getActivityIntent(getApplicationContext(),
					ActionType.RingerModeAction);
			editActionActivityIntent.putExtra("id", id);
			startActivityForResult(editActionActivityIntent, REQ_CODE_EDIT_ACTION);
			break;
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int) */
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
