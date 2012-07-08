/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.adapters;

import java.util.ArrayList;

import org.androwrapee.db.DefaultDAO;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.db.DatabaseManager;
import com.fenceit.service.BackgroundService;

/**
 * The Class AlarmAdapter that is used to display the alarms in a ListView.
 */
public class AlarmAdapter extends BaseAdapter implements OnClickListener {

	/** The context. */
	private final Activity context;

	/** The alarms. */
	private ArrayList<Alarm> alarms;

	/** The dao trigger. */
	private DefaultDAO<BasicTrigger> daoTrigger;

	/**
	 * The Nested Static class ViewHolder, that contains references to the fields of a View, for
	 * quick access.
	 */
	private static class ViewHolder {

		/** The enable button. */
		public ToggleButton enableButton;

		/** The title. */
		public TextView titleTextV;

		/** The type. */
		public TextView descTextV;
	}

	/**
	 * Instantiates a new alarm adapter.
	 * 
	 * @param context the context
	 * @param alarms the alarms
	 */
	public AlarmAdapter(Activity context, ArrayList<Alarm> alarms) {
		this.context = context;
		this.alarms = alarms;
		this.daoTrigger = DatabaseManager.getDAOInstance(context.getApplicationContext(), BasicTrigger.class,
				BasicTrigger.tableName);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getCount() */
	@Override
	public int getCount() {
		return alarms.size();
	}

	/* (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup) */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Try to us a converted view
		View rowView = convertView;
		ViewHolder holder;
		// If there is no converted view, create a new one
		if (rowView == null) {
			// Inflate a new view
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.alarm_panel_list_alarm, null);
			// Save the fields in the view holder for quick reference
			holder = new ViewHolder();
			holder.enableButton = (ToggleButton) rowView.findViewById(R.id.alarmPanel_alarmToggleButton);
			holder.titleTextV = (TextView) rowView.findViewById(R.id.alarmPanel_alarmTitleText);
			holder.descTextV = (TextView) rowView.findViewById(R.id.alarmPanel_alarmDescrText);

			// Create the onclick event for the button
			holder.enableButton.setOnClickListener(this);

			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		Alarm a = alarms.get(position);
		holder.enableButton.setTag(position); // for the click event
		holder.enableButton.setChecked(a.isEnabled());
		holder.titleTextV.setText(a.getName());
		// Get count of triggers
		daoTrigger.open();
		int triggerCount = daoTrigger.countEntries(DefaultDAO.REFERENCE_PREPENDER + "alarm=" + a.getId());
		daoTrigger.close();
		holder.descTextV.setText(triggerCount + " active triggers");

		return rowView;
	}

	/**
	 * Sets the alarms.
	 * 
	 * @param alarms the new alarms
	 */
	public void setAlarms(ArrayList<Alarm> alarms) {
		this.alarms = alarms;
		super.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return alarms.get(position);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int) */
	@Override
	public long getItemId(int position) {
		return alarms.get(position).getId();
	}

	/* Method called when there is a click on the toggle button.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.
	 * CompoundButton, boolean) */
	@Override
	public void onClick(View v) {

		// Change the alarm enabled status
		ToggleButton buttonView = (ToggleButton) v;
		Integer position = (Integer) buttonView.getTag();
		if (position == null)
			return;
		Alarm alarm = alarms.get(position);
		// If the alarm is already checked, skip this
		Log.i(this.getClass().getName(), "Toggle button for alarm with id " + alarm.getId() + " toggled to "
				+ buttonView.isChecked());
		if (alarm.isEnabled() == buttonView.isChecked())
			return;
		alarm.setEnabled(buttonView.isChecked());

		// Persist the alarm in the database
		DefaultDAO<Alarm> dao = DatabaseManager.getDAOInstance(this.context.getApplicationContext(), Alarm.class,
				Alarm.tableName);
		dao.open();
		dao.update(alarm, alarm.getId());
		dao.close();

		// Notify the background service that a new alarm is now enabled
		if (alarm.isEnabled()) {
			Intent intent = new Intent(context, BackgroundService.class);
			intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME, BackgroundService.SERVICE_EVENT_RESET_ALARMS);
			context.startService(intent);
		}
	}
}
