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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.locations.WifisDetectedLocation.Wifi;

/**
 * The Class AlarmAdapter that is used to display the alarms in a ListView.
 */
public class WifisDetectedAdapter extends BaseAdapter {

	/** The context. */
	private final Activity context;

	/** The wifis. */
	private ArrayList<Wifi> wifis;

	/**
	 * The Nested Static class ViewHolder, that contains references to the fields of a View, for
	 * quick access.
	 */
	private static class ViewHolder {

		/** The image view that states if this Wifi is used during the scan. */
		public ImageView enabledImage;

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
			rowView = inflater.inflate(R.layout.wifis_detected_location_list, null);
			// Save the fields in the view holder for quick reference
			holder = new ViewHolder();
			holder.enabledImage = (ImageView) rowView.findViewById(R.id.wifidetec_list_selectedImage);
			holder.ssidTextV = (TextView) rowView.findViewById(R.id.wifidetec_list_ssidText);
			holder.bssidTextV = (TextView) rowView.findViewById(R.id.wifidetec_list_bssidText);

			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		Wifi w = wifis.get(position);
		holder.ssidTextV.setText(w.SSID);
		holder.bssidTextV.setText(w.BSSID);
		if (w.selected)
			holder.enabledImage.setImageResource(android.R.drawable.button_onoff_indicator_on);
		else
			holder.enabledImage.setImageResource(android.R.drawable.button_onoff_indicator_off);

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
	 * 
	 * @see android.widget.Adapter#getItem(int) */
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
}
