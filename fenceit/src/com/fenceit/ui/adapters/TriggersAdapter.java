/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.adapters;

import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.alarm.triggers.BasicTrigger.TriggerType;
import com.fenceit.ui.TriggersFragment;

/**
 * The Class AlarmAdapter that is used to display the alarms in a ListView.
 */
public class TriggersAdapter extends BaseAdapter {

	/** The context. */
	private final Activity context;

	/** The triggers. */
	private List<BasicTrigger> triggers;

	private SingleChoiceAdapter<BasicTrigger.TriggerType> triggerTypeAdapter;

	private TriggersFragment fragment;

	/**
	 * The Nested Static class ViewHolder, that contains references to the fields of a View, for quick access.
	 */
	private static class ViewHolder {

		/** The trigger type. */
		public View triggerTypeSpinner;

		/** The main description. */
		public TextView mainDescriptionTextV;

		/** The secondary description. */
		public TextView secondDescriptionTextV;

		/** The location type image view. */
		public ImageView locationTypeImageV;

		/** Whether we are using a spinner or a textview selector. */
		public boolean usingSpinner;
	}

	/**
	 * Instantiates a new trigger adapter.
	 * 
	 * @param context the context
	 */
	public TriggersAdapter(Activity context, List<BasicTrigger> triggers, TriggersFragment fragment) {
		this.context = context;
		this.triggers = triggers;
		this.triggerTypeAdapter = new SingleChoiceAdapter<BasicTrigger.TriggerType>(context,
				BasicTrigger.getTriggerTypes(), BasicTrigger.getTriggerTypesNames(context));
		this.fragment = fragment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
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
			holder.triggerTypeSpinner = rowView.findViewById(R.id.triggerPanel_triggerTypeSpinner);
			if (holder.triggerTypeSpinner == null) {
				holder.triggerTypeSpinner = rowView.findViewById(R.id.triggerPanel_triggerTypeTextSelector);
				holder.usingSpinner = false;
			} else {
				holder.usingSpinner = true;
				((Spinner) holder.triggerTypeSpinner).setAdapter(triggerTypeAdapter);
			}
			holder.mainDescriptionTextV = (TextView) rowView.findViewById(R.id.triggerPanel_mainDescription);
			holder.secondDescriptionTextV = (TextView) rowView
					.findViewById(R.id.triggerPanel_secondaryDescription);
			holder.locationTypeImageV = (ImageView) rowView.findViewById(R.id.triggerPanel_locationTypeImage);
			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		BasicTrigger trigger = triggers.get(position);

		// Normal Spinner handling method for the case in which the code is Post Android V4, when the
		// Spinner was implemented as a dropdown list.
		if (holder.usingSpinner) {
			Spinner spinner = (Spinner) holder.triggerTypeSpinner;
			spinner.setSelection(triggerTypeAdapter.getIndex(trigger.getType()));
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
						int selectedPosition, long id) {
					Logger.getRootLogger().info("Trigger type changed by textview for position " + position);
					triggers.get(position).setType((TriggerType) parentView.getSelectedItem());
					fragment.storeTrigger(triggers.get(position), false);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});
		} else {
			// Custom handling method for the case in which the code is Pre Android V4, when the Spinner was
			// not implemented as a dropdown list. In this case, on click on the textview, the value is
			// changed directly, without using a Spinner.
			((TextView) holder.triggerTypeSpinner).setText(triggerTypeAdapter.getName(triggers.get(position)
					.getType()));
			holder.triggerTypeSpinner.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Logger.getRootLogger().info("Trigger type changed by textview for position " + position);
					switch (triggers.get(position).getType()) {
					case ON_ENTER:
						triggers.get(position).setType(TriggerType.ON_EXIT);
						break;
					case ON_EXIT:
						triggers.get(position).setType(TriggerType.ON_ENTER);
						break;
					}
					((TextView) v).setText(triggerTypeAdapter.getName(triggers.get(position).getType()));
					fragment.storeTrigger(triggers.get(position), false);

				}
			});
		}
		holder.mainDescriptionTextV.setText(trigger.getMainDescription());
		holder.secondDescriptionTextV.setText(trigger.getSecondaryDescription());
		holder.locationTypeImageV.setImageResource(trigger.getLocation().getTypeImageResource());

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
