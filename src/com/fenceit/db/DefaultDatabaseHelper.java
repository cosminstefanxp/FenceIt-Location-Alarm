/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The Class DefaultDatabaseHelper that acts like a SQLiteOpenHelper, creating the database when
 * needed.
 * 
 */
public class DefaultDatabaseHelper extends SQLiteOpenHelper {

	/** The name of the id field in the object. */
	protected static final String ID_FIELD = "id";

	/** The table names used for this object. */
	protected final String[] mTableNames;

	/** The class. */
	@SuppressWarnings("rawtypes")
	protected final Class[] mClasses;

	/** The logger. */
	private static Logger log = Logger.getLogger(DefaultDatabaseHelper.class);

	/**
	 * Checks if the field is transient.
	 * 
	 * @param field the field
	 * @return true, if is transient
	 */
	private boolean isTransient(Field field) {
		Transient annotationTransient = field.getAnnotation(Transient.class);
		if (annotationTransient == null)
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
	private List<Field> getFields(Class cls) {
		// Get the fields
		Field[] fieldsT = cls.getDeclaredFields();
		LinkedList<Field> fields = new LinkedList<Field>();
		for(Field field:fieldsT)
			fields.add(field);
		// Get Parent fields
		fieldsT = cls.getSuperclass().getDeclaredFields();
		for(Field field:fieldsT)
			fields.add(field);

		return fields;
	}

	/**
	 * Instantiates a new default database helper.
	 * 
	 * @param context the context
	 * @param c the class
	 * @param tableName the table name
	 */
	@SuppressWarnings("rawtypes")
	public DefaultDatabaseHelper(Context context, Class c[], String tableNames[]) {
		super(context, DatabaseDefaults.DATABASE_NAME, null, DatabaseDefaults.DATABASE_VERSION);
		this.mTableNames = tableNames;
		this.mClasses = c;
	}

	/* (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase) */
	@SuppressWarnings("rawtypes")
	@Override
	public void onCreate(SQLiteDatabase database) {

		// For every class type, create the corresponding table
		for (int i = 0; i < mClasses.length; i++) {
			Class cls = mClasses[i];
			String tableName = mTableNames[i];

			// Build the Create Table Statement
			List<Field> fields = getFields(cls);

			String createQuery = "CREATE TABLE " + tableName + "  ( ";
			assert (fields.size() > 0);

			for (Field field : fields) {
				{
					field.setAccessible(true);

					//if it's a parent field, create the query accordingly
					if(isParentField(field))
					{
						createQuery += "_parent_id integer, ";
						continue;
					}
					
					// if it's a transient field, skip it 
					if (isTransient(field))
						continue;

					// Check if it's the id field
					if (field.getName().equals(ID_FIELD)) {
						createQuery += "_id integer primary key autoincrement, ";
						continue;
					}

					// Special type checks
					if (field.getClass().equals(Double.class) || field.getClass().equals(double.class)
							|| field.getClass().equals(Float.class) || field.getClass().equals(float.class))
						createQuery += field.getName() + " real, ";
					else if (field.getClass().equals(Integer.class) || field.getClass().equals(int.class)
							|| field.getClass().equals(Short.class) || field.getClass().equals(short.class)
							|| field.getClass().equals(Byte.class) || field.getClass().equals(byte.class))
						createQuery += field.getName() + " integer, ";
					else
						createQuery += field.getName() + " text not null, ";
				}
			}

			createQuery = createQuery.substring(0, createQuery.length() - 2);
			createQuery += ");";

			// Create the table
			log.warn("Creating database with query: "+createQuery);
			database.execSQL(createQuery);
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
	 * int, int) */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Delete all existing data and re-create the table
		log.warn("Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all existing data.");
		for (int i = 0; i < mTableNames.length; i++)
			db.execSQL("DROP TABLE IF EXISTS " + mTableNames[i]);
		onCreate(db);
	}

}
