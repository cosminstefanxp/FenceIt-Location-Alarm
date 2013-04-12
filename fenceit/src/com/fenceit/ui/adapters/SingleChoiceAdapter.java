/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.adapters;

import org.apache.log4j.Logger;

import com.fenceit.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * An adapter for single choice array of objects.
 * 
 * @param <T> the generic type
 */
public class SingleChoiceAdapter<T> extends BaseAdapter {

	/** The values. */
	private T[] values;

	/** The names. */
	private CharSequence[] names;

	/** The context. */
	private Context context;

	/**
	 * Instantiates a new single choice adapter.
	 * 
	 * @param values the values
	 * @param names the names
	 */
	public SingleChoiceAdapter(Context context, T[] values, CharSequence[] names) {
		super();
		assert (values.length == names.length);
		this.values = values;
		this.names = names;
		this.context = context;
	}

	/**
	 * Gets the values.
	 * 
	 * @return the values
	 */
	public T[] getValues() {
		return values;
	}

	/**
	 * Gets the names.
	 * 
	 * @return the names
	 */
	public CharSequence[] getNames() {
		return names;
	}

	/**
	 * Gets the name.
	 * 
	 * @param value the value
	 * @return the name
	 */
	public CharSequence getName(T value) {
		for (int i = 0; i < values.length; i++)
			if (values[i].equals(value))
				return names[i];
		return "-";
	}

	/**
	 * Gets the index of a given element.
	 * 
	 * @param value the value
	 * @return the index
	 */
	public int getIndex(T value) {
		for (int i = 0; i < values.length; i++)
			if (values[i].equals(value))
				return i;
		return -1;
	}

	@Override
	public int getCount() {
		return values.length;
	}

	@Override
	public Object getItem(int position) {
		return values[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Logger.getRootLogger().info("Normal view");
		TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
		textView.setText(names[position]);
		return textView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		Logger.getRootLogger().info("Dropdown view");
		TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_dropdown_item,
				null);
		int padTop = context.getResources().getDimensionPixelSize(R.dimen.spinner_padding);
		textView.setPadding(padTop, padTop, padTop, padTop);
		textView.setText(names[position]);
		return textView;
	}
}
