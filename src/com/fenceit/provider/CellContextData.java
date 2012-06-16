/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

/**
 * The Class CellContextData provides context information about the cell network to.
 */
public class CellContextData implements ContextData {
	/** The Cell ID. */
	public int cellId;

	/** The location area code. */
	public int lac;

	/** The numeric name (MCC+MNC) of current registered operator. */
	public String networkOperator;

	/** The previous cell id. */
	public int prevCellId;

	/** The previous lac. */
	public int prevLac;

	/** The previous network operator. */
	public String prevNetworkOperator;

	@Override
	public String toString() {
		return "CellContextData [cellId=" + cellId + ", lac=" + lac + ", networkOperator=" + networkOperator
				+ ", prevCellId=" + prevCellId + ", prevLac=" + prevLac + ", prevNetworkOperator="
				+ prevNetworkOperator + "]";
	}

}
