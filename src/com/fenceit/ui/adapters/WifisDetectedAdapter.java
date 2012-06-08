/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fenceit.R;
import com.fenceit.alarm.locations.WifisDetectedLocation.Wifi;
/**
 * The Class AlarmAdapter that is used to display the alarms in a ListView.
 */
public class WifisDetectedAdapter extends BaseAdapter implements OnClickListener {

	/** The context. */
	private final Activity context;

	/** The wifis. */
	private ArrayList<Wifi> wifis;

	/**
	 * The Nested Static class ViewHolder, that contains references to the fields of a View, for
	 * quick access.
	 */
	private static class ViewHolder {

		/** The enable button. */
		public ToggleButton enableButton;

		/** The SSID. */
		public TextView ssidTextV;

		/** The BSSID. */
		public TextView bssidTextV;
	}

	/**
	 * Instantiates a new adapter for detected Wifi networks.
	 * 
	 * @param context the context
	 * @param wifis the wifis
	 */
	public WifisDetectedAdapter(Activity context, ArrayList<Wifi> wifis) {
		this.context = context;
		this.wifis = wifis;
	}

	/* (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getCount() */
	@Override
	public int getCount() {
		return wifis.size();
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
			rowView = inflater.inflate(R.layout.wifi_detected_location_list, null);
			// Save the fields in the view holder for quick reference
			holder = new ViewHolder();
			holder.enableButton = (ToggleButton) rowView.findViewById(R.id.wifidetec_list_selectedCheckbox);
			holder.ssidTextV = (TextView) rowView.findViewById(R.id.wifidetec_list_ssidText);
			holder.bssidTextV = (TextView) rowView.findViewById(R.id.wifidetec_list_bssidText);

			// Create the onclick event for the button
			holder.enableButton.setOnClickListener(this);

			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		Wifi w = wifis.get(position);
		holder.enableButton.setTag(position); // for the click event
		holder.enableButton.setChecked(w.selected);
		holder.ssidTextV.setText(w.SSID);
		holder.bssidTextV.setText(w.BSSID);

		return rowView;
	}

	/**
	 * Sets the wifis.
	 * 
	 * @param wifis the new wifis
	 */
	public void setWifis(ArrayList<Wifi> wifis) {
		this.wifis = wifis;
		super.notifyDataSetChanged();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return wifis.get(position);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int) */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/* Method called when there is a click on the toggle button.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.
	 * CompoundButton, boolean) */
	@Override
	public void onClick(View v) {

		// TODO: to fill in
		// // Change the alarm enabled status
		// ToggleButton buttonView = (ToggleButton) v;
		// Integer position = (Integer) buttonView.getTag();
		// if (position == null)
		// return;
		// Wifi alarm = wifis.get(position);
		// // If the alarm is already checked, skip this
		// Log.i(this.getClass().getName(), "Toggle button for alarm with id " + alarm.getId() +
		// " toggled to "
		// + buttonView.isChecked());
		// if (alarm.isEnabled() == buttonView.isChecked())
		// return;
		// alarm.setEnabled(buttonView.isChecked());
		//
		// // Persist the alarm in the database
		// DefaultDAO<Wifi> dao =
		// DatabaseManager.getDAOInstance(this.context.getApplicationContext(), Wifi.class,
		// Wifi.tableName);
		// dao.open();
		// dao.update(alarm, alarm.getId());
		// dao.close();
	}
}
