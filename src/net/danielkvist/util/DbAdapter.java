package net.danielkvist.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class handles all the database IO together with creating and updating tables and the database when necessary. It
 * also is responsibile for adding the default data in the settings and accounts tables.
 * 
 * @author daniel
 * 
 */
public class DbAdapter
{

    private final Context context;
    private static final String DATABASE_NAME = "data";
    private static final String TAG = "ReceiptTracker";
    private static final int DATABASE_VERSION = 1;

    public static String SELECTED_TABLE;
    public static final String DATABASE_TABLE_RECEIPTS = "receipts";
    public static final String DATABASE_TABLE_SETTINGS = "settings";
    public static final String DATABASE_TABLE_ACCOUNTS = "accounts";
    public static final String KEY_ROWID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_TIMESTAMP = "time";
    public static final String KEY_LOCATION_LAT = "location_lat";
    public static final String KEY_LOCATION_LONG = "location_long";
    public static final String KEY_SUM = "sum";
    public static final String KEY_TAX = "tax";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_ACCOUNT_ID = "account_id";
    public static final String KEY_CODE = "code";
    public static final String KEY_ROW = "row";
    public static final String KEY_SETTING_VALUE = "setting_value";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private static final String DATABASE_CREATE_TABLE_RECEIPTS = "CREATE TABLE " + DATABASE_TABLE_RECEIPTS + " (" + KEY_ROWID
            + " integer primary key autoincrement, " + KEY_NAME + " text not null, " + KEY_PHOTO + " text not null," + KEY_TIMESTAMP
            + " numeric not null," + KEY_LOCATION_LAT + " text not null," + KEY_LOCATION_LONG + " text not null," + KEY_SUM
            + " text not null," + KEY_TAX + " text not null," + KEY_COMMENT + " text not null," + KEY_ACCOUNT_ID + " integer not null"
            + ");";

    private static final String DATABASE_CREATE_TABLE_SETTINGS = "CREATE TABLE " + DATABASE_TABLE_SETTINGS + " (" + KEY_ROWID
            + " integer primary key autoincrement, " + KEY_NAME + " text not null, " + KEY_SETTING_VALUE + " integer not null" + ");";

    private static final String DATABASE_INIT_SETTING_SUM = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" + KEY_NAME + ","
            + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_SUM + "',0" + ");";

    private static final String DATABASE_INIT_SETTING_TAX = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" + KEY_NAME + ","
            + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_TAX + "',0" + ");";

    private static final String DATABASE_INIT_SETTING_COMMENT = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" + KEY_NAME + ","
            + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_COMMENT + "',0" + ");";

    private static final String DATABASE_INIT_SETTING_LOCATION = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" + KEY_NAME + ","
            + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_LOCATION + "',0" + ");";

    private static final String DATABASE_INIT_SETTING_ACCOUNT = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" + KEY_NAME + ","
            + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_ACCOUNT + "',0" + ");";

    /**
     * Constructor that saves the context that is passed in
     * 
     * @param context
     */
    public DbAdapter(Context context)
    {
        this.context = context;
    }

    /**
     * Tries to open the database and throws an exception if it fails.
     * 
     * @return the instance of itself
     * @throws SQLException
     *             if the database couldn't be opened or created
     */
    public DbAdapter open() throws SQLException
    {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Closes the database
     */
    public void close()
    {
        dbHelper.close();
    }

    /**
     * Creates a new row in the database with the information passed in.
     * 
     * @param name
     *            name of the receipt
     * @param photo
     *            path to the photo of the receipt
     * @param timestamp
     *            timestamp of the receipt
     * @param locLat
     *            string representation of the latitude
     * @param locLong
     *            string representation of the longitude
     * @param sum
     *            the total sum
     * @param tax
     *            the tax
     * @param comment
     *            any comment on the receipt
     * @param account_id
     *            which receipt accound id to associate with the receipt
     * @return the new rowId or -1
     */
    public boolean createReceipt(String name, String photo, long timestamp, String locLat, String locLong, String sum, String tax,
            String comment, long account_id)
    {
        ContentValues values = putReceiptValues(name, photo, timestamp, locLat, locLong, sum, tax, comment, account_id);
        return db.insert(DATABASE_TABLE_RECEIPTS, null, values) > 0;
    }

    /**
     * Creates a new row in the accounts table with the receipt account information passed in.
     * 
     * @param code
     *            the account code
     * @param name
     *            the account name
     * @return the new rowId or -1
     */
    public boolean createReceiptAccount(long code, String name)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, code);
        values.put(KEY_ROW, "user");
        values.put(KEY_NAME, name);
        return db.insert(DATABASE_TABLE_ACCOUNTS, null, values) > 0;
    }

    /**
     * Deletes the item whose rowId corresponds with the parameter. Make sure to set which table to delete from before
     * calling this.
     * 
     * @param rowId
     *            id to delete
     * @return true if deleted, false otherwise
     * @throws IllegalAccessErrror
     *             if the selected table is not set
     */
    public boolean deleteItem(long rowId)
    {
        if (SELECTED_TABLE.equals(""))
            throw new IllegalAccessError("Unknown database to delete row from, please select a table.");
        return db.delete(SELECTED_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Deletes a receipt.
     * 
     * @param rowId
     *            the rowId to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteReceipt(long rowId)
    {
        SELECTED_TABLE = DATABASE_TABLE_RECEIPTS;
        return deleteItem(rowId);
    }

    /**
     * Deletes a receipt account
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteReceiptAccount(long rowId)
    {
        SELECTED_TABLE = DATABASE_TABLE_ACCOUNTS;
        return deleteItem(rowId);
    }

    /**
     * Fetches all receipt accounts from the database and returns a Cursor pointing to the first row.
     * 
     * @return Cursor pointing to the first row or null
     */
    public Cursor fetchReceiptAccounts()
    {
        Cursor cursor;
        cursor = db.query(DATABASE_TABLE_ACCOUNTS, new String[] { KEY_ROWID, KEY_NAME, KEY_CODE }, null, null, null, null, KEY_CODE);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Return a Cursor over the list entries in the receipt table
     * 
     * @return Cursor pointing to the first row or null
     */
    public Cursor fetchReceipts(int limit)
    {
        Cursor cursor;
        if (limit > 0)
        {
            cursor = db.query(DATABASE_TABLE_RECEIPTS, new String[] { KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_TIMESTAMP, KEY_LOCATION_LAT,
                    KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT, KEY_ACCOUNT_ID }, null, null, null, null, KEY_TIMESTAMP + " DESC,"
                    + KEY_TIMESTAMP + " DESC", String.valueOf(limit));
        }
        else
        {
            cursor = db.query(DATABASE_TABLE_RECEIPTS, new String[] { KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_TIMESTAMP, KEY_LOCATION_LAT,
                    KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT, KEY_ACCOUNT_ID }, null, null, null, null, null);
        }

        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Return a Cursor over the list of all entries in the settings table
     * 
     * @return Cursor pointing to the first row or null
     */
    public Cursor fetchAllSettings()
    {
        Cursor cursor = db.query(DATABASE_TABLE_SETTINGS, new String[] { KEY_ROWID, KEY_NAME, KEY_SETTING_VALUE }, null, null, null, null,
                null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Return a Cursor positioned at the receipt that matches the given rowId
     * 
     * @param rowId
     *            the id of receipt to retrieve
     * @return Cursor pointing to the first row or null
     */
    public Cursor fetchReceipt(long rowId)
    {

        Cursor cursor = db.query(true, DATABASE_TABLE_RECEIPTS, new String[] { KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_TIMESTAMP,
                KEY_LOCATION_LAT, KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT, KEY_ACCOUNT_ID }, KEY_ROWID + "=" + rowId, null, null,
                null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;

    }

    /**
     * Return a Cursor pointing to the receipts that matches the time span passed in.
     * 
     * @param timeFrom
     *            lower time restriction
     * @param timeTo
     *            upper time restriction
     * @return Cursor pointing to the first row or null
     */
    public Cursor fetchReceipts(long timeFrom, long timeTo)
    {
        Cursor cursor = db.query(true, DATABASE_TABLE_RECEIPTS, new String[] { KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_TIMESTAMP,
                KEY_LOCATION_LAT, KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT, KEY_ACCOUNT_ID }, KEY_TIMESTAMP + ">" + timeFrom
                + " AND " + KEY_TIMESTAMP + "<" + timeTo, null, null, null, KEY_TIMESTAMP + " DESC", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Return a Cursor pointing to the last receipt
     * 
     * @return Cursor pointing to the first row or null
     */
    public Cursor fetchLastReceipt()
    {
        Cursor cursor = db.query(true, DATABASE_TABLE_RECEIPTS, new String[] { KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_TIMESTAMP,
                KEY_LOCATION_LAT, KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT, KEY_ACCOUNT_ID }, null, null, null, null, KEY_ROWID
                + " DESC", "1");
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Return a Cursor positioned at the setting that matches the given rowId
     * 
     * @param rowId
     *            the id of setting to retrieve
     * @return Cursor pointing to first row or null
     */
    public Cursor fetchSetting(String name)
    {

        Cursor cursor = db.query(true, DATABASE_TABLE_SETTINGS, new String[] { KEY_ROWID, KEY_NAME, KEY_SETTING_VALUE }, KEY_NAME + "='"
                + name + "'", null, null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;

    }

    /**
     * Searches for a receipt with a name matching the query.
     * 
     * @param query
     *            the string to search for
     * @return Cursor pointing to first row or null
     */
    public Cursor searchReceiptName(String query)
    {
        Cursor cursor = db.query(DATABASE_TABLE_RECEIPTS, new String[] { KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_TIMESTAMP, KEY_LOCATION_LAT,
                KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT, KEY_ACCOUNT_ID }, KEY_NAME + " LIKE ?",
                new String[] { "%" + query + "%" }, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Updates the receipt account row with the provided information
     * 
     * @param rowId
     *            the row to update
     * @param name
     *            the name to update
     * @param code
     *            the code to update
     * @return true if successful
     */
    public boolean updateReceiptAccount(long rowId, long code, String name)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, code);
        values.put(KEY_NAME, name);
        return db.update(DATABASE_TABLE_SETTINGS, values, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Updates the corresponding row in the database with the information passed in.
     * 
     * @paran rowId the row id to update
     * @param name
     *            name of the receipt
     * @param photo
     *            path to the photo of the receipt
     * @param timestamp
     *            timestamp of the receipt
     * @param locLat
     *            string representation of the latitude
     * @param locLong
     *            string representation of the longitude
     * @param sum
     *            the total sum
     * @param tax
     *            the tax
     * @param comment
     *            any comment on the receipt
     * @param account_id
     *            which receipt accound id to associate with the receipt
     * @return true if successful
     */
    public boolean updateReceipt(long rowId, String name, String photo, long timestamp, String locLat, String locLong, String sum,
            String tax, String comment, long account_id)
    {
        ContentValues values = putReceiptValues(name, photo, timestamp, locLat, locLong, sum, tax, comment, account_id);
        return db.update(DATABASE_TABLE_RECEIPTS, values, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Updates the setting value corresponding to the name passed in
     * 
     * @param name
     *            name of setting
     * @param value
     *            value of setting
     * @return true if successful
     */
    public boolean updateSetting(String name, int value)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_SETTING_VALUE, value);
        return db.update(DATABASE_TABLE_SETTINGS, values, KEY_NAME + "='" + name + "'", null) > 0;
    }

    /**
     * Saves the values in a ContentValues object that is then return to the caller
     * 
     * @param name
     * @param photo
     * @param timestamp
     * @param locLat
     * @param locLong
     * @param sum
     * @param tax
     * @param comment
     * @param account_id
     * @return the ContentValues object containing the data
     */
    private ContentValues putReceiptValues(String name, String photo, long timestamp, String locLat, String locLong, String sum,
            String tax, String comment, long account_id)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHOTO, photo);
        values.put(KEY_TIMESTAMP, timestamp);
        values.put(KEY_LOCATION_LAT, locLat);
        values.put(KEY_LOCATION_LONG, locLong);
        values.put(KEY_SUM, sum);
        values.put(KEY_TAX, tax);
        values.put(KEY_COMMENT, comment);
        values.put(KEY_ACCOUNT_ID, account_id);
        return values;
    }

    /**
     * Inner class that handles first initialization of data in the database and creates its tables.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        private Context context;

        /**
         * Constructor that takes a context as a parameter
         * 
         * @param context
         */
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        /**
         * Creates tables, initiates setting values and imports the accounts.sql file
         */
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE_TABLE_RECEIPTS);
            db.execSQL(DATABASE_CREATE_TABLE_SETTINGS);
            db.execSQL(DATABASE_INIT_SETTING_SUM);
            db.execSQL(DATABASE_INIT_SETTING_TAX);
            db.execSQL(DATABASE_INIT_SETTING_COMMENT);
            db.execSQL(DATABASE_INIT_SETTING_LOCATION);
            db.execSQL(DATABASE_INIT_SETTING_ACCOUNT);
            initDatabaseData(db);
        }

        /**
         * This method does the import of the accounts.sql data
         * 
         * @param db
         */
        private void initDatabaseData(SQLiteDatabase db)
        {
            BufferedReader br = null;
            try
            {

                br = new BufferedReader(new InputStreamReader(context.getAssets().open("accounts.sql")), 1024 * 4);
                String line = null;
                db.beginTransaction();
                while ((line = br.readLine()) != null)
                {
                    db.execSQL(line);
                }
                db.setTransactionSuccessful();
            }
            catch (IOException e)
            {
                Log.e("test", "read database init file error");
            }
            finally
            {
                db.endTransaction();
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch (IOException e)
                    {
                        Log.e("test", "buffer reader close error");
                    }
                }
            }
        }

        /**
         * Runs on upgrade and destroys all tables and data
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading " + DATABASE_TABLE_RECEIPTS + " and " + DATABASE_CREATE_TABLE_SETTINGS + " database from version "
                    + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RECEIPTS);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ACCOUNTS);
            onCreate(db);
        }
    }

}
