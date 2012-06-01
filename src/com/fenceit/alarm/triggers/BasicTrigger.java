/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.triggers;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;
import org.androwrapee.db.ReferenceField;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.provider.ContextData;

/**
 * A basic implementation of the trigger.
 */
@DatabaseClass
public class BasicTrigger extends AbstractAlarmTrigger {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1858903324908003651L;

	/** The Constant tableName. */
	public static final String tableName = "triggers";

	/** The location. */
	@ReferenceField
	private AlarmLocation location;

	/** The type of the triggering. */
	@DatabaseField
	private TriggerType type;

	/**
	 * The Enum TriggerType.
	 */
	public enum TriggerType {
		/** The triggering occurs when entering the Location. */
		ON_ENTER,
		/** The triggering occurs when exiting the Location. */
		ON_EXIT,
	};

	/**
	 * Instantiates a new basic trigger.
	 * 
	 * @param alarm the alarm
	 */
	public BasicTrigger(Alarm alarm) {
		super(alarm);
		this.location = null;
		this.type = TriggerType.ON_ENTER;
	}

	/**
	 * Instantiates a new basic trigger.
	 */
	public BasicTrigger() {
		super(null);
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.triggers.AbstractAlarmTrigger#shouldTrigger(com.fenceit
	 * .alarm.EnvironmentData ) */
	@Override
	public boolean shouldTrigger(ContextData data) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Gets the associated location.
	 * 
	 * @return the location
	 */
	public AlarmLocation getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 * 
	 * @param fence the location to set
	 */
	public void setLocation(AlarmLocation fence) {
		this.location = fence;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public TriggerType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type to set
	 */
	public void setType(TriggerType type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.triggers.AlarmTrigger#isComplete() */
	@Override
	public boolean isComplete() {
		if (location == null || type == null)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return "BasicTrigger [id=" + id + ", alarm=" + alarm + ", type=" + type + ", location=" + location + "]";
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.triggers.AlarmTrigger#getMainDescription() */
	@Override
	public String getMainDescription() {
		switch (this.type) {
		case ON_ENTER:
			return "When arriving at";
		case ON_EXIT:
			return "When leaving";
		default:
			return "Unset";
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.triggers.AlarmTrigger#getSecondaryDescription() */
	@Override
	public String getSecondaryDescription() {
		return "Home";
	}

}
