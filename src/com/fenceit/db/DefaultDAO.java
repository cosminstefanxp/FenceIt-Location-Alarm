/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Simple database access helper class, for SQLite for Android. Defines the basic CRUD operations
 * for the and gives the ability to insert, get, update or delete objects from the database.<br/>
 * <br/>
 * 
 * Uses Java Reflection for creating the database entries. This has some implications and
 * restrictions:
 * <ul>
 * <li>Only the direct fields of the object and fields of first level parent will be saved.</li>
 * <li>All fields of the object and the parent (besides the one marked with {@link Transient} or
 * {@link ParentField}</li>
 * <li>Every object MUST have an id field named {@code id} of recommended type long, which will be
 * saved as {@literal _id} in the database. Otherwise the {@literal ID_FIELD} can be overwritten.</li>
 * <li>All other fields will be saved in the database with the same name as the object field.</li>
 * <li>The class requires a constructor with no parameters, otherwise it will throw an exception.</li>
 * <li>The class or the parent class can have a field marked with {@link ParentField} which will not
 * be stored in the database, but it MUST have a field with the name {@code id} which will be stored
 * in the database as {@code _parent_id}. This field can be used for queries and for making
 * one-to-many relationships.</li>
 * </ul>
 * 
 * @param <T> the generic type
 */
public class DefaultDAO<T> {

	protected String mTableName;

	/** The name of the id field in the object. */
	protected static final String ID_FIELD = "id";

	/** The column names. */
	protected String mColumnNames[];

	/** The database. */
	private SQLiteDatabase mDb;

	/** The database helper. */
	private SQLiteOpenHelper mDbHelper;

	/** The class. */
	private final Class<T> mClass;

	/** The logger. */
	private static Logger log = Logger.getLogger(DefaultDAO.class);

	/**
	 * Checks if the field is transient.
	 * 
	 * @param field the field
	 * @return true, if is transient
	 */
	private boolean isTransient(Field field) {
		Transient annotation = field.getAnnotation(Transient.class);
		if (annotation == null)
			return false;
		else
			return true;
	}

	/**
	 * Checks if is parent field.
	 * 
	 * @param field the field
	 * @return true, if is parent field
	 */
	private boolean isParentField(Field field) {
		ParentField annotationParentField = field.getAnnotation(ParentField.class);
		if (annotationParentField == null)
			return false;
		else
			return true;
	}

	/**
	 * Gets the fields of a given class
	 * 
	 * @param cls the cls
	 * @return the fields
	 */
	@SuppressWarnings("rawtypes")
	private ArrayList<Field> getFields(Class cls) {
		// Get the fields
		Field[] fieldsT = cls.getDeclaredFields();
		ArrayList<Field> fields = new ArrayList<Field>();
		for (Field field : fieldsT)
			fields.add(field);
		// Get Parent fields
		fieldsT = cls.getSuperclass().getDeclaredFields();
		for (Field field : fieldsT)
			fields.add(field);

		return fields;
	}

	/**
	 * Constructor - takes the context to allow the database to be opened/created.
	 * 
	 * @param c the class
	 * @param dbHelper the db helper
	 */
	public DefaultDAO(Class<T> c, SQLiteOpenHelper dbHelper, String tableName) {
		super();
		this.mClass = c;
		this.mDbHelper = dbHelper;
		this.mTableName = tableName;

		// Create the column names
		ArrayList<String> columnNames = new ArrayList<String>();
		ArrayList<Field> fields = getFields(c);
		for (int i = 0; i < fields.size(); i++) {
			if (!isTransient(fields.get(i)) && !isParentField(fields.get(i))) {
				if (fields.get(i).getName().equals(ID_FIELD))
					columnNames.add("_id");
				else
					columnNames.add(fields.get(i).getName());
			}
		}

		mColumnNames = (String[]) columnNames.toArray(new String[0]);

	}

	/**
	 * Open the database. If it cannot be opened, try to create a new instance of the database. If
	 * it cannot be created, throw an exception to signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public DefaultDAO<T> open() throws SQLException {
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Close the database.
	 */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * Inserts a new entry in the database for the object provided. If the entry is successfully
	 * created return the new rowId for that entry, otherwise return a -1 to indicate failure.
	 * 
	 * @param newObject the new object
	 * @return rowId or -1 if failed
	 */
	public long insert(T newObject) {

		ContentValues initialValues;
		try {
			initialValues = buildContentValues(newObject, false);
		} catch (Exception e) {
			log.fatal("Error occured while parsing object for insertion: " + newObject + ". Error message: "
					+ e.getMessage());
			e.printStackTrace();
			return -1;
		}

		// Use the ContentValues to insert the entry in the database and return the row id.
		log.debug("Inserting: " + initialValues);
		return mDb.insert(mTableName, null, initialValues);
	}

	/**
	 * Update the entry in the database corresponding to the provided object. The row id is given
	 * separately.
	 * 
	 * @param object the object
	 * @param rowId the row id
	 * @return true if the object was successfully updated, false otherwise
	 */
	public boolean update(T object, long rowId) {
		ContentValues args;
		try {
			args = buildContentValues(object, true);
		} catch (Exception e) {
			log.fatal("Error occured while parsing object for update: " + object + ". Error message: "
					+ e.getMessage());
			e.printStackTrace();
			return false;
		}

		return mDb.update(mTableName, args, "_id" + "=" + rowId, null) > 0;
	}

	/**
	 * Builds the content values required to update or create a new entry in the database.
	 * 
	 * @param object the object
	 * @param setID whether to set the ID_FIELD to the value in the object, or leave it without any
	 *        value, so it will be generated by itself, if the database column is set as
	 * @return the content values
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception {@literal autoincrement}
	 * @throws SecurityException the security exception
	 * @throws NoSuchFieldException the no such field exception
	 */
	protected ContentValues buildContentValues(T object, boolean setID) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchFieldException {
		ContentValues contentValues = new ContentValues();
		List<Field> fields = getFields(object.getClass());

		// Take every field in the object and insert it in the ContentValues entry
		for (Field field : fields) {
			field.setAccessible(true);

			// if it's a parent field, get the id and put it in "_parent_id"
			if (isParentField(field)) {
				Object parent = field.get(object);
				if (parent == null)
					contentValues.put("_parent_id", "null");
				Field parentIdField = parent.getClass().getDeclaredField(ID_FIELD);
				parentIdField.setAccessible(true);
				Long parentId = parentIdField.getLong(parent);
				contentValues.put("_parent_id", parentId.toString());
				continue;
			}

			// if it's a transient field, skip it
			if (isTransient(field))
				continue;

			Object value = field.get(object);
			String fieldName = field.getName();

			// Check if it's the id field
			if (fieldName.equals(ID_FIELD)) {
				if (setID)
					contentValues.put("_id", value.toString());
				continue;
			}

			// Special type checks
			if (field.getType().equals(Date.class))
				value = ((Date) value).getTime();
			if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
				if ((Boolean) value)
					value = "t";
				else
					value = "f";
			}

			contentValues.put(fieldName, value.toString());
		}

		return contentValues;
	}

	/**
	 * Delete the entry with the given rowId.
	 * 
	 * @param rowId id of the object to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean delete(long rowId) {
		return mDb.delete(mTableName, "_id" + "=" + rowId, null) > 0;
	}

	/**
	 * Return the object positioned at the entry that matches the given id.
	 * 
	 * @param rowId id of object to retrieve
	 * @return the object fetched from the database
	 */
	public T fetch(long rowId) {

		// Get the cursor for the database entry
		Cursor cursor = mDb.query(true, mTableName, mColumnNames, "_id" + "=" + rowId, null, null, null,
				null, null);

		if (cursor == null || cursor.getCount() == 0)
			return null;

		// Build the object from the cursor
		cursor.moveToFirst();
		try {
			T object = buildObject(cursor);
			cursor.close();
			return object;
		} catch (Exception e) {
			log.fatal("Error occured while building object of type " + mClass + " from cursor: " + cursor
					+ ".");
			log.error("Error message: " + e.getMessage());
			e.printStackTrace();
			cursor.close();
			return null;
		}
	}

	/**
	 * Fetches all the object in the database that match a given where clause.
	 * 
	 * @param where the where clause; null means it will return all rows
	 * @return the array list
	 */
	public ArrayList<T> fetchAll(String where) {
		ArrayList<T> objects = new ArrayList<T>();

		// Get the cursor for the database entry
		Cursor cursor = mDb.query(true, mTableName, mColumnNames, where, null, null, null, null, null);
		if (cursor == null || cursor.getCount() == 0)
			return objects;

		// Build the objects from the cursor
		cursor.moveToFirst();
		try {

			// Build all objects from the cursor
			while (!cursor.isAfterLast()) {
				T object = buildObject(cursor);
				objects.add(object);
				cursor.moveToNext();
			}

			cursor.close();
			return objects;
		} catch (Exception e) {
			log.fatal("Error occured while building objects of type " + mClass + " from cursor: " + cursor
					+ ".");
			log.error("Error message: " + e.getMessage());
			e.printStackTrace();
			cursor.close();
			return null;
		}
	}

	/**
	 * Gets the database reference that can be used to manually do queries.
	 * 
	 * @return the database reference
	 */
	public SQLiteDatabase getDatabaseReference() {
		return mDb;
	}

	/**
	 * Builds the object.
	 * 
	 * @param cursor the cursor
	 * @return the t
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws ParseException the parse exception
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
	public T buildObject(Cursor cursor) throws IllegalAccessException, InstantiationException,
			IllegalArgumentException, ParseException, SecurityException, NoSuchMethodException,
			InvocationTargetException {
		// Create a new instance of the class, that will be populated with information
		// Hack for private/inner classes
		Constructor<T> c = mClass.getDeclaredConstructor();
		c.setAccessible(true);
		T object = c.newInstance();
		List<Field> fields = getFields(mClass);

		// For every field in the class, fill it with data from the cursor
		for (Field field : fields) {
			field.setAccessible(true);

			// if it's a transient or parent field, skip it
			if (isTransient(field) || isParentField(field))
				continue;

			// Get the column index for the field
			int columnIndex = cursor.getColumnIndex(field.getName());

			// Check if it's the id field
			if (field.getName().equals(ID_FIELD)) {
				columnIndex = cursor.getColumnIndex("_id");
				field.set(object, cursor.getLong(columnIndex));
				continue;
			}

			// Special type checks
			if (field.getType() == int.class || field.getType() == Integer.class)
				field.set(object, cursor.getInt(columnIndex));
			if (field.getType() == short.class || field.getType() == Short.class)
				field.set(object, cursor.getShort(columnIndex));
			if (field.getType() == long.class || field.getType() == Long.class)
				field.set(object, cursor.getLong(columnIndex));
			if (field.getType() == double.class || field.getType() == Double.class)
				field.set(object, cursor.getDouble(columnIndex));
			if (field.getType() == float.class || field.getType() == Float.class)
				field.set(object, cursor.getFloat(columnIndex));
			if (field.getType() == boolean.class || field.getType() == Boolean.class) {
				if (cursor.getString(columnIndex).equals("t"))
					field.set(object, true);
				else
					field.set(object, false);
			}
			if (field.getType() == Date.class)
				field.set(object, new Date(cursor.getLong(columnIndex)));
			if (field.getType() == String.class)
				field.set(object, cursor.getString(columnIndex));
		}

		return object;
	}

}
