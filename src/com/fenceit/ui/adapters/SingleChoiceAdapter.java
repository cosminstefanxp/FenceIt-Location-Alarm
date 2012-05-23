/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.adapters;

/**
 * An adapter for single choice array of objects.
 * 
 * @param <T> the generic type
 */
public class SingleChoiceAdapter<T> {

	/** The values. */
	private T[] values;

	/** The names. */
	private CharSequence[] names;

	/**
	 * Instantiates a new single choice adapter.
	 * 
	 * @param values the values
	 * @param names the names
	 */
	public SingleChoiceAdapter(T[] values, CharSequence[] names) {
		super();
		assert (values.length == names.length);
		this.values = values;
		this.names = names;
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
}
