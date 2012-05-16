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
 * The Annotation that marks the ID field in a Database Class that is being stored.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IdField {

}
