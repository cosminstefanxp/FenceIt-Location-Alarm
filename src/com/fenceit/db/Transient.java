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
 * The Transient annotation that can be used to annotate fields that shouldn't be stored in the
 * database.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {

}
