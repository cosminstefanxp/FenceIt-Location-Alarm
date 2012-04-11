/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class Alarm that defines an alarm.
 */
public class Alarm {

	/** The enabled. */
	private boolean enabled;

	/** The triggers. */
	private List<AlarmTrigger> triggers;

	/** The actions. */
	private List<AlarmAction> actions;

	/** The creation date. */
	private final Date creationDate=new Date();

	/**
	 * Checks if the alarm should be triggered.
	 *
	 * @param data the environment data which provides enough information for the 
	 * 		alarm to check if it should be triggered.
	 * @return true, if is triggered
	 */
	public boolean shouldTrigger(EnvironmentData data)
	{
		for(AlarmTrigger t:triggers)
			if(t.shouldTrigger(data))
				return true;

		return false;
	}
	
	/**
	 * Instantiates a new alarm.
	 */
	public Alarm() {
		super();
		this.enabled = false;
		this.triggers = new LinkedList<AlarmTrigger>();
		this.actions = new LinkedList<AlarmAction>();
	}
	
	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled.
	 *
	 * @param enabled the new enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the actions.
	 *
	 * @return the actions
	 */
	public List<AlarmAction> getActions() {
		return actions;
	}

	/**
	 * Adds the action.
	 *
	 * @param action the action
	 */
	public void addAction(AlarmAction action)
	{
		if(action!=null)
			actions.add(action);
	}

	/**
	 * Removes the action.
	 *
	 * @param action the action
	 * @return true, if successful
	 */
	public boolean removeAction(AlarmAction action)
	{
		return actions.remove(action);
	}

	/**
	 * Gets the creation date.
	 *
	 * @return the creation date
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the triggers.
	 *
	 * @param triggers the new triggers
	 */
	public void setTriggers(List<AlarmTrigger> triggers) {
		this.triggers = triggers;
	}
	
	/**
	 * Adds a new trigger.
	 *
	 * @param trigger the trigger
	 */
	public void addTrigger(AlarmTrigger trigger)
	{
		if(trigger!=null)
			triggers.add(trigger);
	}
	
	/**
	 * Removes the trigger.
	 *
	 * @param trigger the trigger
	 * @return true, if successful
	 */
	public boolean removeTrigger(AlarmTrigger trigger)
	{
		boolean ret=triggers.remove(trigger);
		if(triggers.size()==0)
			this.setEnabled(false);
		
		return ret;
	}

}
