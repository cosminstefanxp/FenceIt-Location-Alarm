/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

/**
 * The DatabaseClassStructureException is thrown when the structure of a class that has to be stored
 * in the database is not according to the specifications. Please check {@link ReflectionManager} and
 * {@link DefaultDAO} for specifications.
 */
public class IllegalClassStructureException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7208320571758498752L;

	public IllegalClassStructureException() {
		super();
	}

	public IllegalClassStructureException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public IllegalClassStructureException(String detailMessage) {
		super(detailMessage);
	}

	public IllegalClassStructureException(Throwable throwable) {
		super(throwable);
	}

}
