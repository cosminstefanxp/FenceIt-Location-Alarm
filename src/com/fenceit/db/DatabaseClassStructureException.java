/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

/**
 * The Class DatabaseClassStructureException.
 */
public class DatabaseClassStructureException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7208320571758498752L;

	public DatabaseClassStructureException() {
		super();
	}

	public DatabaseClassStructureException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public DatabaseClassStructureException(String detailMessage) {
		super(detailMessage);
	}

	public DatabaseClassStructureException(Throwable throwable) {
		super(throwable);
	}

}
