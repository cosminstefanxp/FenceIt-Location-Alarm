/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * The ReflectionManager manages and gets the required field of a given class, to be used in the
 * rest of the Database Provider Classes.<br/>
 * <br/>
 * Each field should be annotated with one Annotation maximum and each class (including
 * superclasses) that needs to be stored to the database has to have the {@link DatabaseClass}
 * annotation.
 * 
 * @param <T> the generic type
 */
public class ReflectionManager {

	/** The c. */
	@SuppressWarnings("rawtypes")
	private Class c;

	/** The parent reference fields. */
	private List<Field> parentReferenceFields;

	/** The database fields. */
	private List<Field> databaseFields;

	/** The id field. */
	private Field idField;

	/**
	 * Instantiates a new reflection manager.
	 * 
	 * @param c the c
	 * @throws DatabaseClassStructureException the database class structure exception
	 */
	@SuppressWarnings("rawtypes")
	public ReflectionManager(Class c) throws DatabaseClassStructureException {
		super();
		this.c = c;
		parentReferenceFields = new LinkedList<Field>();
		databaseFields = new LinkedList<Field>();

		prepareFields();
	}

	/**
	 * Checks if is database field.
	 * 
	 * @param f the field
	 * @return true, if is database field
	 */
	private boolean isDatabaseField(Field f) {
		DatabaseField annotation = f.getAnnotation(DatabaseField.class);
		if (annotation == null)
			return false;
		return true;
	}

	/**
	 * Checks if is parent reference field.
	 * 
	 * @param f the field
	 * @return true, if is parent reference field
	 */
	private boolean isParentReferenceField(Field f) {
		ParentField annotation = f.getAnnotation(ParentField.class);
		if (annotation == null)
			return false;
		return true;
	}

	/**
	 * Checks if is id field.
	 * 
	 * @param f the field
	 * @return true, if is id field
	 */
	private boolean isIdField(Field f) {
		IdField annotation = f.getAnnotation(IdField.class);
		if (annotation == null)
			return false;
		return true;
	}

	/**
	 * Process the fields in a given class. Should be called on a class annotated with
	 * 
	 * @param cls the class {@link DatabaseClass}
	 */
	@SuppressWarnings("rawtypes")
	private void processFields(Class cls) {
		Field[] fields = cls.getDeclaredFields();

		// Process each field, make it accessible and classify it according to the annotation
		for (Field field : fields) {
			field.setAccessible(true);

			if (isIdField(field)) {
				this.idField = field;
				continue;
			}
			if (isDatabaseField(field)) {
				this.databaseFields.add(field);
				continue;
			}
			if (isParentReferenceField(field))
				this.parentReferenceFields.add(field);
		}
	}

	/**
	 * Prepare the fields.
	 * 
	 * @throws DatabaseClassStructureException the database class structure exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void prepareFields() throws DatabaseClassStructureException {

		Annotation annotation;

		// Process the main class
		annotation = c.getAnnotation(DatabaseClass.class);
		if (annotation == null)
			throw new DatabaseClassStructureException("Class " + c.getName()
					+ " is not a DatabaseClass. Check for the required annotation: "
					+ DatabaseClass.class.getSimpleName());
		processFields(c);

		// Process any parent class that is a DatabaseClass
		Class cls = c.getSuperclass();
		while (cls != null) {
			annotation = cls.getAnnotation(DatabaseClass.class);
			if (annotation == null)
				break;
			processFields(cls);
			cls = cls.getSuperclass();
		}

	}

	/**
	 * Gets the parent reference fields.
	 * 
	 * @return the parentReferenceFields
	 */
	final List<Field> getParentReferenceFields() {
		return parentReferenceFields;
	}

	/**
	 * Gets the database fields.
	 * 
	 * @return the databaseFields
	 */
	final List<Field> getDatabaseFields() {
		return databaseFields;
	}

	/**
	 * Gets the id field.
	 * 
	 * @return the idField
	 */
	final Field getIdField() {
		return idField;
	}

}
