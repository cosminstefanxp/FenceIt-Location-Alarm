/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
 * <li>Only the direct fields of the object will be saved. This means, none of the fields in parent
 * classes are stored.</li>
 * <li>All of the object's fields are stored.</li>
 * <li>Every object should have an id field named {@code id}, which will be saved as {@literal _id}
 * in the database. Otherwise the {@literal ID_FIELD} can be overwritten.</li>
 * <li>All other fields will be saved in the database with the same name as the object field.</li>
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

	/** The Constant dateFormat. */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	/** The logger. */
	private static Logger log = Logger.getLogger(DefaultDAO.class);
	
	/**
	 * Checks if the field is transient.
	 *
	 * @param field the field
	 * @return true, if is transient
	 */
	private boolean isTransient(Field field)
	{
		Transient annotation=field.getAnnotation(Transient.class);
		if(annotation==null)
			return false;
		else
			return true;
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
		ArrayList<String> columnNames=new ArrayList<String>();
		Field[] fields = c.getFields();
		mColumnNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			if(!isTransient(fields[i]))
				columnNames.add(fields[i].getName());
		}

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
			log.fatal("Error occured while parsing object for insertion: " + newObject 
					+ ". Error message: " + e.getMessage());
			e.printStackTrace();
			return -1;
		}

		// Use the ContentValues to insert the entry in the database and return the row id.
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
			log.fatal("Error occured while parsing object for update: " + object 
					+ ". Error message: " + e.getMessage());
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
	 */
	protected ContentValues buildContentValues(T object, boolean setID) throws IllegalArgumentException,
			IllegalAccessException {
		ContentValues contentValues = new ContentValues();
		Field[] fields = object.getClass().getDeclaredFields();

		// Take every field in the object and insert it in the ContentValues entry
		for (Field field : fields) {
			field.setAccessible(true);
			//if it's a transient field, skip it
			if(isTransient(field))
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
				value = dateFormat.format((Date) value);
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
	 * @return Cursor positioned to matching note, if found
	 */
	public T fetch(long rowId) {

		

		// Get the cursor for the database entry
		Cursor cursor =mDb.query(true, mTableName, mColumnNames, "_id" + "=" + rowId, null, null, null,
				null, null);

		if (cursor == null || cursor.getCount()==0)
			return null;

		// Build the object from the cursor
		cursor.moveToFirst();
		try {
			T object=buildObject(cursor);
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
	 * Builds the object.
	 * 
	 * @param cursor the cursor
	 * @return the t
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws ParseException the parse exception
	 */
	protected T buildObject(Cursor cursor) throws IllegalAccessException, InstantiationException,
			IllegalArgumentException, ParseException {
		// Create a new instance of the class, that will be populated with information
		T object = mClass.newInstance();
		Field[] fields = mClass.getDeclaredFields();

		// For every field in the class, fill it with data from the cursor
		for (Field field : fields) {
			field.setAccessible(true);
			//if it's a transient field, skip it
			if(isTransient(field))
				continue;
			
			
			// Get the column index for the field
			int columnIndex = cursor.getColumnIndex(field.getName());

			// Check if it's the id field
			if (field.getName().equals(ID_FIELD)) {
				columnIndex = cursor.getColumnIndex("_id");
				field.setInt(object, cursor.getInt(columnIndex));
				continue;
			}

			// Special type checks
			if (field.getType() == int.class || field.getType() == Integer.class)
				field.setInt(object, cursor.getInt(columnIndex));
			if (field.getType() == short.class || field.getType() == Short.class)
				field.setShort(object, cursor.getShort(columnIndex));
			if (field.getType() == long.class || field.getType() == Long.class)
				field.setLong(object, cursor.getLong(columnIndex));
			if (field.getType() == double.class || field.getType() == Double.class)
				field.setDouble(object, cursor.getDouble(columnIndex));
			if (field.getType() == float.class || field.getType() == Float.class)
				field.setFloat(object, cursor.getFloat(columnIndex));
			if (field.getType() == boolean.class || field.getType() == Boolean.class) {
				if (cursor.getString(columnIndex).equals("t"))
					field.setBoolean(object, true);
				else
					field.setBoolean(object, false);
			}
			if (field.getType() == Date.class)
				field.set(object, dateFormat.parseObject(cursor.getString(columnIndex)));
			if (field.getType() == String.class)
				field.set(object, cursor.getString(columnIndex));
		}

		return object;
	}

}
