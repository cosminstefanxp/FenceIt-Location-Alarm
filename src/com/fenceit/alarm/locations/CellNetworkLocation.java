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

import com.fenceit.R;
import com.fenceit.provider.CellContextData;
import com.fenceit.provider.ContextData;

/**
 * The Class CellLocation is an implementation of an AlarmLocation based on the Cell Tower to which the device
 * is currently connected.
 */
@DatabaseClass
public class CellNetworkLocation extends AbstractAlarmLocation implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -413247346212461233L;

	/** The table name. */
	public static final String tableName = "cell_locations";
	// CID - > BID
	// LAC -> NID
	// MNC -> SID
	/** The Cell ID. */
	@DatabaseField
	private int cellId = -1;

	/** The mobile network code. */
	@DatabaseField
	private int mnc = -1;

	/** The mobile country code. */
	@DatabaseField
	private int mcc = -1;

	/** The location area code. */
	@DatabaseField
	private int lac = -1;

	/** The operator name. */
	@DatabaseField
	private String operatorName;

	@Override
	public Status checkStatus(ContextData info) {
		CellContextData data = (CellContextData) info;
		if (data == null || data.cellId == -1)
			return Status.UNKNOWN;

		// Prepare MNC and MCC
		int localMnc = Integer.parseInt(data.networkOperator.substring(0, 3));
		int localMcc = Integer.parseInt(data.networkOperator.substring(3));

		// Check current status
		boolean isInside = false;
		if (data.cellId == this.cellId && data.lac == this.lac && localMcc == this.mcc
				&& localMnc == this.mnc)
			isInside = true;

		// Prepare MNC and MCC
		localMnc = Integer.parseInt(data.prevNetworkOperator.substring(0, 3));
		localMcc = Integer.parseInt(data.prevNetworkOperator.substring(3));

		// Check previous status
		boolean wasInside = false;
		if (data.prevCellId == this.cellId && data.prevLac == this.lac && localMcc == this.mcc
				&& localMnc == this.mnc)
			wasInside = true;

		// Compute the status
		if (isInside) {
			if (wasInside)
				return Status.STAYED_INSIDE;
			else
				return Status.ENTERED;
		} else {
			if (wasInside)
				return Status.LEFT;
			else
				return Status.STAYED_OUTSIDE;
		}
	}

	@Override
	public String getDetailedDescription() {
		return operatorName + " " + cellId;
	}

	@Override
	public String getTypeDescription() {
		return "Cell Tower";
	}

	@Override
	public LocationType getType() {
		return LocationType.CellNetworkLocation;
	}

	@Override
	public boolean isComplete() {
		return cellId != -1 && lac != -1;
	}

	/**
	 * Gets the cell id.
	 * 
	 * @return the cell id
	 */
	public int getCellId() {
		return cellId;
	}

	/**
	 * Sets the cell id.
	 * 
	 * @param cellId the new cell id
	 */
	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	/**
	 * Gets the mobile network code
	 * 
	 * @return the mobile network code
	 */
	public int getMnc() {
		return mnc;
	}

	/**
	 * Sets the mobile network code
	 * 
	 * @param mnc the new mobile network code
	 */
	public void setMnc(int mnc) {
		this.mnc = mnc;
	}

	/**
	 * Gets the mobile country code
	 * 
	 * @return the mobile country code
	 */
	public int getMcc() {
		return mcc;
	}

	/**
	 * Sets the mobile country code.
	 * 
	 * @param mcc the new mobile country code
	 */
	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	/**
	 * Gets the location area code.
	 * 
	 * @return the location area code
	 */
	public int getLac() {
		return lac;
	}

	/**
	 * Sets the location area code.
	 * 
	 * @param lac the new location area code
	 */
	public void setLac(int lac) {
		this.lac = lac;
	}

	/**
	 * Gets the operator name.
	 * 
	 * @return the operator name
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * Sets the operator name.
	 * 
	 * @param operatorName the new operator name
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	@Override
	public String toString() {
		return "CellLocation [cellId=" + cellId + ", mnc=" + mnc + ", mcc=" + mcc + ", lac=" + lac + "]";
	}

	@Override
	public int getTypeImageResource() {
		return R.drawable.ic_location_cell_network;
	}

	@Override
	public AbstractAlarmLocation copy() {
		CellNetworkLocation newLocation = new CellNetworkLocation();
		newLocation.cellId = this.cellId;
		newLocation.lac = this.lac;
		newLocation.mcc = this.mcc;
		newLocation.mnc = this.mnc;
		newLocation.name = this.name;
		newLocation.operatorName = this.operatorName;
		newLocation.favorite = this.favorite;
		newLocation.id = this.id;
		return newLocation;
	}

}
