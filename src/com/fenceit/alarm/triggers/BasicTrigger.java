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

import android.util.Log;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.AlarmLocation.Status;
import com.fenceit.alarm.locations.LocationType;
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

	/** The type of location to which this trigger corresponds. Only stored the location type id, to save storage. */
	@DatabaseField
	// TODO: to repair implementation
	private int locationType;

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
		ON_EXIT
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
		Status status = location.checkStatus(data);
		Log.d("Trigger", "New Trigger Status: " + status);
		if (this.type == TriggerType.ON_ENTER && status == Status.ENTERED)
			return true;
		if (this.type == TriggerType.ON_EXIT && status == Status.LEFT)
			return true;
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
		if (alarm != null)
			return "BasicTrigger [id=" + id + ", alarm=" + alarm.getId() + ", type=" + type + ", location=" + location
					+ "]";
		else
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

	/**
	 * Gets the location type id.
	 * 
	 * @return the locationType
	 */
	public int getLocationTypeId() {
		return locationType;
	}

	/**
	 * Sets the location type.
	 * 
	 * @param locationType the locationType to set
	 */
	public void setLocationType(LocationType locationType) {
		this.locationType = locationType.getId();
	}

}
