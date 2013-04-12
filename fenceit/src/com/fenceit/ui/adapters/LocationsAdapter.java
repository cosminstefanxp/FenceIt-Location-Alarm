/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.locations.AlarmLocation;

/**
 * The Class LocationAdapter that is used to display the locations in a ListView.
 */
public class LocationsAdapter extends BaseAdapter {

	/** The context. */
	private final Activity context;

	/** The locations. */
	private List<AlarmLocation> locations;

	/**
	 * The Nested Static class ViewHolder, that contains references to the fields of a View, for quick access.
	 */
	private static class ViewHolder {

		/** The type image. */
		public ImageView typeImage;

		/** The name. */
		public TextView nameTextV;

		/** The main description. */
		public TextView mainDescriptionTextV;

		/** The secondary description. */
		public TextView typeDescriptionTextV;
	}

	/**
	 * Instantiates a new locations adapter.
	 * 
	 * @param context the context
	 * @param locations the locations
	 */
	public LocationsAdapter(Activity context, List<AlarmLocation> locations) {
		this.context = context;
		this.locations = locations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Try to us a converted view
		View rowView = convertView;
		ViewHolder holder;
		// If there is no converted view, create a new one
		if (rowView == null) {
			// Inflate a new view
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.location_panel_list, null);
			// Save the fields in the view holder for quick reference
			holder = new ViewHolder();
			holder.nameTextV = (TextView) rowView.findViewById(R.id.locationPanel_nameText);
			holder.mainDescriptionTextV = (TextView) rowView.findViewById(R.id.locationPanel_descriptionText);
			holder.typeDescriptionTextV = (TextView) rowView.findViewById(R.id.locationPanel_typeText);
			holder.typeImage = (ImageView) rowView.findViewById(R.id.locationPanel_typeImage);
			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		AlarmLocation location = locations.get(position);
		holder.nameTextV.setText(location.getDisplayName(context));
		holder.mainDescriptionTextV.setText(location.getDetailedDescription());
		holder.typeDescriptionTextV.setText(location.getTypeDescription() + " Location");
		holder.typeImage.setImageResource(location.getTypeImageResource());

		return rowView;
	}

	/**
	 * Sets the locations.
	 * 
	 * @param locations the new locations
	 */
	public void setLocations(List<AlarmLocation> locations) {
		this.locations = locations;
		super.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return locations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return locations.get(position).getId();
	}

	@Override
	public int getCount() {
		return locations.size();
	}

}
