/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.actions;

/**
 * The possible Action Types.
 */
public enum ActionType {

	/** The Notification Action, that displays a message on a screen and plays an alarm sound. */
	NotificationAction(0);

	/** The id. */
	private int id;

	/**
	 * Instantiates a new location type.
	 * 
	 * @param id the id
	 */
	private ActionType(int id) {
		this.id = id;
	}

	/**
	 * Gets the id of the enum.
	 * 
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

}
