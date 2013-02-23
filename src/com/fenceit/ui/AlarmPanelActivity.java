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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseManager;
import com.fenceit.ui.adapters.AlarmAdapter;
import com.fenceit.ui.helpers.EditItemActionMode;

/**
 * The Class FenceItActivity.
 */
public class AlarmPanelActivity extends DefaultActivity implements OnItemClickListener,
		OnItemLongClickListener {

	/** The logger. */
	private static Logger log = Logger.getRootLogger();

	/** The alarms. */
	ArrayList<Alarm> alarms;

	/** The dao. */
	private DefaultDAO<Alarm> dao = null;

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
		getSupportActionBar().setHomeButtonEnabled(true);

		setContentView(R.layout.alarm_panel);

		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), Alarm.class, Alarm.tableName);

		// Get the alarms
		fetchAlarms();

		// Prepare the listview
		listView = (ListView) findViewById(R.id.alarmPanel_alarmList);
		listAdapter = new AlarmAdapter(this, alarms);
		listView.setAdapter(listAdapter);
		// registerForContextMenu(listView);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

		log.info("Started up.");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_add_item, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == R.id.menu_btn_add_item) {
			log.debug("Add alarm button clicked.");
			Intent addAlarmActivityIntent = new Intent(this, AlarmActivity.class);
			startActivityForResult(addAlarmActivityIntent, REQ_CODE_ADD_ALARM);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
	 * Starts the edit activity for the alarm.
	 * 
	 * @param id the id
	 */
	private void editAlarm(long id) {
		Intent editAlarmActivityIntent = new Intent(AlarmPanelActivity.this, AlarmActivity.class);
		editAlarmActivityIntent.putExtra("id", id);
		startActivityForResult(editAlarmActivityIntent, REQ_CODE_EDIT_ALARM);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request " + requestCode + " with result code: " + resultCode);
		// if ((requestCode == REQ_CODE_ADD_ALARM ||
		// requestCode==REQ_CODE_EDIT_ALARM) && resultCode
		// == RESULT_OK) {
		// TODO: separate new alarm from edited alarm
		log.debug("Refreshing alarms...");
		fetchAlarms();
		refreshAlarmsListView();

	}

	/**
	 * Refreshes the alarms list view.
	 */
	private void refreshAlarmsListView() {
		listAdapter.setAlarms(alarms);
	}

	/*
	 * For click on an Alarm item in list.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		log.info("ListView item click for editing alarm with id " + id);
		editAlarm(id);
	}

	/*
	 * For long click on an Alarm item in the list.
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
		// Start an action mode with options regarding the Alarm
		startActionMode(new EditItemActionMode() {
			@Override
			protected void onEditItem(ActionMode mode) {
				log.info("Editing alarm with id " + id + " using action mode.");
				editAlarm(id);
				mode.finish();
			}

			@Override
			protected void onDeleteItem(ActionMode mode) {
				log.info("Deleting alarm using action mode on " + position);
				deleteAlarm(alarms.get(position));
				refreshAlarmsListView();
				mode.finish();
			}
		});
		return true;
	}
}
