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
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.fenceit.Log4jConfiguration;
import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseManager;
import com.fenceit.ui.adapters.AlarmAdapter;

/**
 * The Class FenceItActivity.
 */
public class FenceItActivity extends DefaultActivity implements OnClickListener, OnItemClickListener {

	/** The logger. */
	private static Logger log = Logger.getRootLogger();

	/** The alarms. */
	ArrayList<Alarm> alarms;

	/** The db helper. */
	private static SQLiteOpenHelper dbHelper = null;

	/** The dao. */
	private DefaultDAO<Alarm> dao = null;

	/** The context menu id. */
	private long contextMenuPosition;

	/** The list view. */
	ListView listView;

	/** The list adapter. */
	AlarmAdapter listAdapter;

	/** The Constant REQ_CODE_ADD_ALARM. */
	private static final int REQ_CODE_ADD_ALARM = 1;

	/** The Constant REQ_CODE_EDIT_ALARM. */
	private static final int REQ_CODE_EDIT_ALARM = 2;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();

		setContentView(R.layout.main);

		// Prepare database connection
		if (dbHelper == null)
			dbHelper = DatabaseManager.getDBHelper(getApplicationContext());
		if (dao == null)
			dao = new DefaultDAO<Alarm>(Alarm.class, dbHelper,
					DatabaseManager.getReflectionManagerInstance(Alarm.class), Alarm.tableName);

		// Add listeners
		ImageButton but = (ImageButton) findViewById(R.id.main_addAlarmButton);
		but.setOnClickListener(this);

		// Get the alarms
		fetchAlarms();

		// Prepare the listview
		listView = (ListView) findViewById(R.id.main_alarmList);
		listAdapter = new AlarmAdapter(this, alarms);
		listView.setAdapter(listAdapter);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(this);

		log.info("Started up.");
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View,
	 * android.view.ContextMenu.ContextMenuInfo) */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (v == listView) {
			// Check which list item was selected
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			contextMenuPosition = info.position;
			log.debug("Selected list item on position: " + contextMenuPosition);

			// Inflate the menu
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main_list_alarm_menu, menu);
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem) */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_main_list_alarm_delete:
			deleteAlarm(alarms.get(info.position));
			refreshAlarmsListView();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * Delete alarm.
	 * 
	 * @param alarm the alarm
	 */
	private void deleteAlarm(Alarm alarm) {
		log.info("Deleting alarm: " + alarm);
		dao.open();
		dao.delete(alarm.getId());
		alarms.remove(alarm);
		dao.close();
	}

	/**
	 * Fetch alarms.
	 */
	private void fetchAlarms() {
		log.info("Fetching all alarms from database.");
		dao.open();
		alarms = dao.fetchAll(null);
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
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View) */
	@Override
	public void onClick(View v) {
		if (v == findViewById(R.id.main_addAlarmButton)) {
			log.debug("Add alarm button clicked.");
			Intent addAlarmActivityIntent = new Intent(this, AlarmActivity.class);
			startActivityForResult(addAlarmActivityIntent, REQ_CODE_ADD_ALARM);
		}

	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent) */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request " + requestCode + " with result code: " + resultCode);
		// if ((requestCode == REQ_CODE_ADD_ALARM || requestCode==REQ_CODE_EDIT_ALARM) && resultCode
		// == RESULT_OK) {
		if (resultCode == RESULT_OK) {
			// TODO: separate new alarm from edited alarm
			log.debug("Refreshing alarms...");
			fetchAlarms();
			refreshAlarmsListView();
		}

	}

	/**
	 * Refreshes the alarms list view.
	 */
	private void refreshAlarmsListView() {
		listAdapter.setAlarms(alarms);
	}

	/* For click on Alarm items in list (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 * android.view.View, int, long) */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		log.info("ListView item click for editing alarm with id " + id);
		Intent editAlarmActivityIntent = new Intent(this, AlarmActivity.class);
		editAlarmActivityIntent.putExtra("id", id);
		startActivityForResult(editAlarmActivityIntent, REQ_CODE_EDIT_ALARM);
	}
}