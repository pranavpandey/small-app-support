/*
 * Copyright (C) 2016 Pranav Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pranavpandey.smallapp.database;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.widget.Toast;

/**
 * Database helper functions to manage file and intent association. If
 * user selects to save preferences then, this class will be used to save
 * the selected app and automatically open it again when next time user 
 * launches the same file or intent.
 * 
 * It has all the basic functions to read, create update and delete the
 * saved apps.
 */
public class Associations {

	/**
	 * SQLiteOpenHelper object to perform database operations
	 */
	private final SQLiteHelper mSQLiteHelper;
	
	/**
	 * Initialize associations before performing any operation. It will 
	 * initialize the SQLiteHelper and setup the database so that we can
	 * perform read and write operations.
	 */
	public Associations(Context context) {
		if (context == null) {
			throw new NullPointerException("Context should not be null");
		}
		
		mSQLiteHelper = new SQLiteHelper(context);
	}
	
	/**
	 * Get instance of {@link #mSQLiteHelper}.
	 */
	public SQLiteHelper getHelper() {
		return mSQLiteHelper;
	}
	
	/**
	 * A SQLiteOpenHelper class to perform read, create update and delete
	 * operations on our database to manage file or intent associations.
	 */
	public static class SQLiteHelper extends SQLiteOpenHelper {
	
		/**
		 * Name of the database.
		 */
		private static final String DB_NAME = "Assocations";
		
		/**
		 * Name of the table in which we have to store associations.
		 */
		private static final String TABLE_NAME = "associations";
		
		/**
		 * Name of the Key Column in which we store intent or file type.
		 */
		private static final String COL_KEY = "intent_type";
		
		/**
		 * Name of the Value Column in which we store associated package name.
		 */
		private static final String COL_VALUE = "intent_package";
		
		/**
		 * Version of the database.
		 */
		private static final int VERSION = 1;
		
		/**
		 * Context to retrieve resources.
		 */
		private Context context;
		
		public SQLiteHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
			this.context = context;
		}
		
		@Override 
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXIST " + TABLE_NAME +
			    " ( " + COL_KEY + " text primary key not null, " +
			    COL_VALUE + " text null);");
		}
		
		@Override 
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
		
		/**
		 * Insert a <key, value> pair into the database.
		 * 
		 * @param key to be inserted.
		 * @param value of the key.
		 * 
		 * @return <code>true</code> if inserts successfully.
		 */
		public boolean put(String key, String value) {
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL("INSERT OR REPLACE INTO " + TABLE_NAME +
			    " (" + COL_KEY + ", " + COL_VALUE + ") " +
			    " VALUES('" + key + "', '" + value + "')");
			db.close();
			
			return true;
		}
		
		/**
		 * Insert a list of <key, value> pairs into the database.
		 * 
		 * @param list if pairs.
		 * 
		 * @return <code>true</code> if inserts successfully.
		 */
		public boolean put(List<Pair<String, ?>> list) {
			SQLiteDatabase db = getWritableDatabase();
			boolean result = true;
			
			try {
				db.beginTransaction();
				for (Pair<String, ?> pair : list) {
					db.execSQL("INSERT OR REPLACE INTO " + TABLE_NAME +
							" (" + COL_KEY + ", " + COL_VALUE + ") " +
							" VALUES('" + pair.first + "', '" + String.valueOf(pair.second) + "')");
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				result = false;
			} finally {
				db.endTransaction();
				db.close();
			}
			
			return result;
		}
		
		/**
		 * Delete a entry form the database.
		 * 
		 * @param key to be deleted.
		 * 
		 * @return <code>true</code> if deletes successfully.
		 */
		public boolean delete(String key) {
			SQLiteDatabase db = getWritableDatabase();
			int count = db.delete(TABLE_NAME, COL_KEY + "='" + key + "'", null);
			db.close();
			
			return count != -1;
		}
		
		/**
		 * Delete multiple entries form the database.
		 * 
		 * @param keys to be deleted.
		 * 
		 * @return <code>true</code> if deletes successfully.
		 */
		public boolean delete(String... keys) {
			SQLiteDatabase db = getWritableDatabase();
			boolean result = true;
			try {
				db.beginTransaction();
				for (String key : keys) {
					if (key == null) {
						continue;
					}
					db.delete(TABLE_NAME, COL_KEY + "='" + key + "'", null);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				result = false;
			} finally {
				db.endTransaction();
				db.close();
			}
			
			return result;
		}
		
		/**
		 * Check if a key already exist in the database or not.
		 * 
		 * @param key to check.
		 * 
		 * @return <code>true</code> key of same name already exist.
		 */
		public boolean contains(String key) {
			return get(key) != null;
		}
		
		/**
		 * Retrieve the value of a key from the database.
		 * 
		 * @param key to retrieve value.
		 * 
		 * @return Value of the key.
		 */
		public String get(String key) {
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
			    " WHERE " + COL_KEY + " = '" + key + "'", null);
			
			if (cursor == null) {
				return null;
			}
			
			cursor.moveToFirst();
			if (cursor.getCount() == 0) {
				return null;
			}
			
			String value = cursor.getString(1);
			cursor.close();
			db.close();
			
			return value;
		}
		
		/**
		 * Clear all the entries and associations in the database.
		 * 
		 * @param showToast <code>true</code> to show a toast to notify
		 * user.
		 */
		public boolean clearAll(boolean showToast) {
			SQLiteDatabase db = getWritableDatabase();
			db.execSQL("DELETE FROM " + TABLE_NAME);
			db.close();
			
			if (showToast) {
				Toast.makeText(context, com.pranavpandey.smallapp.R.string.sas_clear_defaults_reset, 
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		
		/**
		 * @return No. of entries in the database.
		 */
		public long count() {
			SQLiteDatabase db = getWritableDatabase();
			long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
			db.close();
			
			return count;
		}
	}
}
