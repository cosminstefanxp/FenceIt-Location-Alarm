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
 * The ReferenceField annotation that can be used to annotate fields that mark the related entity
 * of the current object. A Reference Field is not saved as it is in the database. Instead, the
 * {@code id} field of the related class is used.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceField {

}
