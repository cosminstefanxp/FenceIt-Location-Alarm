/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fenceit.alarm.Alarm;

/**
 * The Class AlarmAdapter that is used to display the alarms in a ListView.
 */
public class AlarmAdapter extends BaseAdapter {

	/** The context. */
	private final Activity context;

	/** The alarms. */
	private ArrayList<Alarm> alarms;

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
		public TextView typeTextV;
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
			rowView = inflater.inflate(R.layout.main_list_alarm, null);
			// Save the fields in the view holder for quick reference
			holder = new ViewHolder();
			holder.enableButton = (ToggleButton) rowView.findViewById(R.id.alarmListEnableToggle);
			holder.titleTextV = (TextView) rowView.findViewById(R.id.alarmListTitleText);
			holder.typeTextV = (TextView) rowView.findViewById(R.id.alarmListTypeText);
			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		Alarm a = alarms.get(position);
		holder.enableButton.setChecked(a.isEnabled());
		holder.titleTextV.setText(a.getName());
		holder.typeTextV.setText(a.getTriggers().toString());

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
}
