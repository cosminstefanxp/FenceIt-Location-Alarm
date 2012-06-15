/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import java.io.Serializable;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;

import android.telephony.gsm.GsmCellLocation;

import com.fenceit.provider.ContextData;

/**
 * The Class CellLocation is an implementation of an AlarmLocation based on the Cell Tower to which
 * the device is currently connected.
 */
@DatabaseClass
public class CellLocation extends AbstractAlarmLocation implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -413247346212461233L;

	/** The Cell ID. */
	@DatabaseField
	private int cellId;
	
	/** The mobile network code. */
	@DatabaseField
	private int mnc;
	
	/** The mobile country code. */
	@DatabaseField
	private int mcc;
	
	/** The location area code. */
	@DatabaseField
	private int lac;

	/* (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#checkStatus(com.fenceit.provider.ContextData)
	 */
	@Override
	public Status checkStatus(ContextData info) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#getTypeDescription()
	 */
	@Override
	public String getTypeDescription() {
		return "Cell Tower";
	}

	/* (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#getType()
	 */
	@Override
	public LocationType getType() {
		return LocationType.CellLocation;
	}

	/* (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#isComplete()
	 */
	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

}
