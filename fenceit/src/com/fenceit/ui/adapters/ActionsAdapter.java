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
import com.fenceit.alarm.actions.AlarmAction;

/**
 * The Class ActionsAdapter that is used to display the actions in a ListView.
 */
public class ActionsAdapter extends BaseAdapter {

	/** The context. */
	private final Activity context;

	/** The actions. */
	private List<AlarmAction> actions;

	/**
	 * The Nested Static class ViewHolder, that contains references to the fields of a View, for quick access.
	 */
	private static class ViewHolder {

		/** The type image. */
		public ImageView typeImage;

		/** The main description. */
		public TextView mainDescriptionTextV;

		/** The secondary description. */
		public TextView typeDescriptionTextV;
	}

	/**
	 * Instantiates a new actions adapter.
	 * 
	 * @param context the context
	 * @param actions the actions
	 */
	public ActionsAdapter(Activity context, List<AlarmAction> actions) {
		this.context = context;
		this.actions = actions;
	}

	/*
	 * (non-Javadoc)
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
			rowView = inflater.inflate(R.layout.actions_panel_fragment_list, null);

			// Save the fields in the view holder for quick reference
			holder = new ViewHolder();
			holder.mainDescriptionTextV = (TextView) rowView.findViewById(R.id.actionsPanel_actionsListMainDesc);
			holder.typeDescriptionTextV = (TextView) rowView.findViewById(R.id.actionsPanel_actionsListSecDesc);
			holder.typeImage = (ImageView) rowView.findViewById(R.id.actionsPanel_actionsListTypeImage);

			// Save the view holder as a tag
			rowView.setTag(holder);
		} else {
			// Get the holder that contains the view
			holder = (ViewHolder) rowView.getTag();
		}

		// Populate the view
		AlarmAction action = actions.get(position);
		holder.mainDescriptionTextV.setText(action.getTypeDescription(context));
		holder.typeDescriptionTextV.setText(action.getDescription(context));
		holder.typeImage.setImageResource(action.getTypeImageResource());

		return rowView;
	}

	/**
	 * Sets the actions.
	 * 
	 * @param actions the new actions
	 */
	public void setActions(List<AlarmAction> actions) {
		this.actions = actions;
		super.notifyDataSetChanged();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return actions.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return actions.get(position).getId();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return actions.size();
	}

	/**
	 * Gets the action by id.
	 *
	 * @param id the id
	 * @return the action by id
	 */
	public AlarmAction getActionById(long id) {
		for (AlarmAction a : actions)
			if (a.getId() == id)
				return a;
		return null;
	}

}
