/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package org.androwrapee.db;

/**
 * The Interface DatabaseReference must be inherited by any class that aims to be used as a
 * reference field for a entry to be stored in the database.
 */
public interface DatabaseReferenceClass {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public long getId();

}
