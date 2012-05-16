/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package org.androwrapee;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The Annotation that marks a field as Database Storable. If this annotates a field of a
 * DatabaseClass, it will be stored as a column in the database.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseField {

}
