/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.triggers.AlarmTrigger;

/**
 * The Class AlarmAdapter that is used to display the alarms in a ListView.
 */
public class TriggersAdapter extends BaseAdapter {

	/** The context. */
	private final Activity context;

	/** The triggers. */
	private List<AlarmTrigger> triggers;

	/**
	 * The Nested Static class ViewHolder, that contains references to the
	 * fields of a View, for quick access.
	 */
	private static class ViewHolder {

		/** The main description. */
		public TextView mainDescriptionTextV;

		/** The secondary description. */
		public TextView secondDescriptionTextV;
	}

	/**
	 * Instantiates a new trigger adapter.
	 * 
	 * @param context
	 *            the context
	 */
	public TriggersAdapter(Activity context, List<AlarmTrigger> triggers) {
		this.context = context;
		this.triggers = triggers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
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
			rowView = inflater.inflate(R.layout.alarm_trigger_list, null);
			// Save the fields in the view holder for quick reference
			holder = new ViewHolder();
			holder.mainDescriptionTextV = (TextView) rowView
					.findViewById(R.id.alarm_triggersListMainDesc);
			holder.secondDescriptionTextV = (TextView) rowView
					.findViewById(R.id.alarm_triggersListSecDesc);
			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		AlarmTrigger trigger = triggers.get(position);
		holder.mainDescriptionTextV.setText(trigger.getMainDescription());
		holder.secondDescriptionTextV
				.setText(trigger.getSecondaryDescription());

		return rowView;
	}

	/**
	 * Sets the triggers.
	 * 
	 */
	public void setTriggers(List<AlarmTrigger> triggers) {
		this.triggers = triggers;
		super.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return triggers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return triggers.get(position).getId();
	}

	@Override
	public int getCount() {
		return triggers.size();
	}

}
