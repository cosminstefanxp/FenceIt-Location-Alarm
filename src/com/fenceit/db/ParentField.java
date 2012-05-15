/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The ParentField annotation that can be used to annotate fields that mark the parent entity of the
 * current object. A Parent Field is not saved as it is in the database. Instead, the getId() method
 * is called on the field to get the id of the parent entity.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentField {

}
