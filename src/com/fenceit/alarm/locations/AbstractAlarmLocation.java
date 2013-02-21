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
import org.androwrapee.db.IdField;

/**
 * An abstract implementation of the AlarmLocation.
 */
@DatabaseClass
public abstract class AbstractAlarmLocation implements AlarmLocation, Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5014117271712172420L;

	/** The id. */
	@IdField
	protected long id;

	/** The name of the location. */
	@DatabaseField
	protected String name;
	
	public static final int STAYED_INSIDE=0;
	public static final int STAYED_OUTSIDE=1;
	public static final int ENTERED_LOCATION=2;
	public static final int LEFT_LOCATION=3;

	/**
	 * If the location is a favorite one. Only the favorite locations remain in the database after
	 * the referencing trigger has been deleted.
	 */
	@DatabaseField
	protected boolean favorite;

	/**
	 * Instantiates a new abstract alarm location.
	 */
	protected AbstractAlarmLocation() {
		this.favorite = false;
		this.name = "Alarm Location";
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#getId() */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#getName() */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Checks if is favorite.
	 * 
	 * @return true, if is favorite
	 */
	public boolean isFavorite() {
		return favorite;
	}

	/**
	 * Sets favorite status of the location.
	 * 
	 * @param favorite the new favorite
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	/**
	 * Creates a copy of the Alarm Location. Must be an actual instance of the extending type.
	 *
	 * @return the location
	 */
	public abstract AbstractAlarmLocation copy();

}
