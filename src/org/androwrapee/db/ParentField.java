/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package org.androwrapee.db;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The ParentField annotation that can be used to annotate fields that mark the parent entity of the
 * current object. A Parent Field is not saved as it is in the database. Instead, the {@code id}
 * field of the parent class is used.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentField {

}
