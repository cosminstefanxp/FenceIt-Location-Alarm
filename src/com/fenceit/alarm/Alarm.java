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

import org.apache.log4j.Logger;

import com.fenceit.db.Transient;

/**
 * The Class Alarm that defines an alarm.
 */
public class Alarm {
	
	/** The id. */
	private long id;

	/** The name. */
	private String name;
	
	/** The enabled. */
	private boolean enabled;

	/** The triggers. */
	@Transient
	private List<AlarmTrigger> triggers;

	/** The actions. */
	@Transient
	private List<AlarmAction> actions;

	/** The creation date. */
	private Date creationDate=new Date();
	
	/** The logger. */
	@Transient
	private static Logger log = Logger.getLogger(Alarm.class);
	
	/** The Constant tableName. */
	@Transient
	public static final String tableName="alarm";

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
			{
				log.info("Alarm "+this+" triggered by "+t);
				return true;
			}

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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Alarm [enabled=" + enabled + ", triggers=" + triggers
				+ ", actions=" + actions + "]";
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
	 * Gets the triggers.
	 *
	 * @return the triggers
	 */
	public List<AlarmTrigger> getTriggers() {
		return this.triggers;
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

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Alarm other = (Alarm) obj;
		if (enabled != other.enabled)
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
