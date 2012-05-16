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
 * The Annotation that marks a class as corresponding to a Database Table. 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseClass {

}
