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
import android.widget.Spinner;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.triggers.BasicTrigger;

/**
 * The Class AlarmAdapter that is used to display the alarms in a ListView.
 */
public class TriggersAdapter extends BaseAdapter {

	/** The context. */
	private final Activity context;

	/** The triggers. */
	private List<BasicTrigger> triggers;

	private SingleChoiceAdapter<BasicTrigger.TriggerType> triggerTypeAdapter;

	/**
	 * The Nested Static class ViewHolder, that contains references to the fields of a View, for quick access.
	 */
	private static class ViewHolder {

		/** The trigger type. */
		public Spinner triggerTypeSpinner;

		/** The main description. */
		public TextView mainDescriptionTextV;

		/** The secondary description. */
		public TextView secondDescriptionTextV;
	}

	/**
	 * Instantiates a new trigger adapter.
	 * 
	 * @param context the context
	 */
	public TriggersAdapter(Activity context, List<BasicTrigger> triggers) {
		this.context = context;
		this.triggers = triggers;
		this.triggerTypeAdapter = new SingleChoiceAdapter<BasicTrigger.TriggerType>(context,
				BasicTrigger.getTriggerTypes(), BasicTrigger.getTriggerTypesNames(context));
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
			rowView = inflater.inflate(R.layout.trigger_panel_fragment_list, null);
			// Save the fields in the view holder for quick reference
			holder = new ViewHolder();
			holder.triggerTypeSpinner = (Spinner) rowView.findViewById(R.id.triggerPanel_triggerTypeSpinner);
			holder.triggerTypeSpinner.setAdapter(triggerTypeAdapter);
			holder.mainDescriptionTextV = (TextView) rowView.findViewById(R.id.triggerPanel_mainDescription);
			holder.secondDescriptionTextV = (TextView) rowView
					.findViewById(R.id.triggerPanel_secondaryDescription);
			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		BasicTrigger trigger = triggers.get(position);
		holder.triggerTypeSpinner.setSelection(triggerTypeAdapter.getIndex(trigger.getType()));
		holder.mainDescriptionTextV.setText(trigger.getMainDescription());
		holder.secondDescriptionTextV.setText(trigger.getSecondaryDescription());

		return rowView;
	}

	/**
	 * Sets the triggers and resets the listening Views.
	 * 
	 */
	public void setTriggers(List<BasicTrigger> triggers) {
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
