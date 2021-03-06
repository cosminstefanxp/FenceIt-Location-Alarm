/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;
import org.androwrapee.db.DatabaseReferenceClass;
import org.androwrapee.db.IdField;
import org.apache.log4j.Logger;

import com.fenceit.alarm.actions.AlarmAction;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.provider.ContextData;

/**
 * The Class Alarm that defines an alarm.
 */
@DatabaseClass
public class Alarm implements Serializable, DatabaseReferenceClass {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2191764559986512813L;

	/** The id. */
	@IdField
	private long id;

	/** The name. */
	@DatabaseField
	private String name;

	/** The enabled. */
	@DatabaseField
	private boolean enabled;

	/** Whether the alarm gets automatically disabled when triggered. */
	@DatabaseField
	private boolean disableOnTrigger;

	/** The triggers. */
	transient private List<AlarmTrigger> triggers;

	/** The actions. */
	transient private List<AlarmAction> actions;

	/** The creation date. */
	@DatabaseField
	private Date creationDate = new Date();

	/** The logger. */
	private static Logger log = Logger.getLogger(Alarm.class);

	/** The Constant tableName. */
	public static final String tableName = "alarms";

	/**
	 * Checks if the alarm should be triggered.
	 * 
	 * @param data the environment data which provides enough information for
	 *            the alarm to check if it should be triggered.
	 * @return true, if is triggered
	 */
	public boolean shouldTrigger(ContextData data) {
		for (AlarmTrigger t : triggers)
			if (t.shouldTrigger(data)) {
				log.info("Alarm " + this + " triggered by " + t);
				return true;
			}

		return false;
	}

	/**
	 * Checks if the alarm is complete and has all the fields correctly filled
	 * in.
	 * 
	 * @return true, if is complete
	 */
	public boolean isComplete() {
		if (name == null || name.trim().length() == 0)
			return false;
		return true;
	}

	/**
	 * Instantiates a new alarm.
	 */
	public Alarm() {
		super();
		this.enabled = false;
		this.disableOnTrigger = false;
		this.triggers = new ArrayList<AlarmTrigger>();
		this.actions = new ArrayList<AlarmAction>();
		this.name = "New Alarm";
	}

	/**
	 * Instantiates a new alarm only with the id.
	 * 
	 * @param id the id
	 */
	public Alarm(long id) {
		this();
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Alarm [id=" + id + ", name=" + name + ", enabled=" + enabled + ", disableOnTrigger="
				+ disableOnTrigger + ", triggers=" + triggers + ", actions=" + actions + "]";
		// String out = "Alarm [id=" + id + ", name=" + name + ", enabled=" +
		// enabled + "]";
		// out += "\n\tTriggers:";
		// for (AlarmTrigger t : triggers)
		// out += "\n\t" + t.toString();
		// out += "\n\tActions:";
		// for (AlarmAction a : actions)
		// out += "\n\t" + a.toString();
		// return out;
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
		if (actions == null)
			this.actions = new ArrayList<AlarmAction>();
		return actions;
	}

	/**
	 * Adds the action.
	 * 
	 * @param action the action
	 */
	public void addAction(AlarmAction action) {
		if (action != null)
			actions.add(action);
	}

	/**
	 * Removes the action.
	 * 
	 * @param action the action
	 * @return true, if successful
	 */
	public boolean removeAction(AlarmAction action) {
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
		if (triggers == null)
			this.triggers = new ArrayList<AlarmTrigger>();
		return this.triggers;
	}

	/**
	 * Adds a new trigger.
	 * 
	 * @param trigger the trigger
	 */
	public void addTrigger(AlarmTrigger trigger) {
		if (trigger != null)
			triggers.add(trigger);
	}

	/**
	 * Removes the trigger.
	 * 
	 * @param trigger the trigger
	 * @return true, if successful
	 */
	public boolean removeTrigger(AlarmTrigger trigger) {
		boolean ret = triggers.remove(trigger);
		if (triggers.size() == 0)
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

	/**
	 * Checks if is disabling on trigger.
	 * 
	 * @return true, if is disabling on trigger
	 */
	public boolean isDisablingOnTrigger() {
		return disableOnTrigger;
	}

	/**
	 * Sets the disable on trigger.
	 * 
	 * @param disableOnTrigger the new disable on trigger
	 */
	public void setDisableOnTrigger(boolean disableOnTrigger) {
		this.disableOnTrigger = disableOnTrigger;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
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
