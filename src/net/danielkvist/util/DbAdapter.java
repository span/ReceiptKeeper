package net.danielkvist.util;

import java.security.KeyRep;
import net.danielkvist.receipttracker.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter
{
    
    private final Context context;
    private static final String DATABASE_NAME = "data";
    private static final String TAG = "ReceiptTracker";
    private static final int DATABASE_VERSION = 1;
    
    public static String SELECTED_TABLE;
    public static final String DATABASE_TABLE_RECEIPTS = "receipts";
    public static final String DATABASE_TABLE_SETTINGS = "settings";
    public static final String KEY_ROWID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_LOCATION_LAT = "location_lat";
    public static final String KEY_LOCATION_LONG = "location_long";
    public static final String KEY_SUM = "sum";
    public static final String KEY_TAX = "tax";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_SETTING_VALUE = "setting_value";
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    
    private static final String DATABASE_CREATE_TABLE_RECEIPTS = "CREATE TABLE " + DATABASE_TABLE_RECEIPTS + " (" + 
                        KEY_ROWID + " integer primary key autoincrement, " +
                        KEY_NAME + " text not null, " +
                        KEY_PHOTO + " text not null," +
                        KEY_DATE + " text not null," +
                        KEY_TIME + " text not null," +
                        KEY_LOCATION_LAT + " text not null," +
                        KEY_LOCATION_LONG + " text not null," +
                        KEY_SUM + " text not null," +
                        KEY_TAX + " text not null," +
                        KEY_COMMENT + " text not null" +
            		");";
    
    private static final String DATABASE_CREATE_TABLE_SETTINGS = "CREATE TABLE " + DATABASE_TABLE_SETTINGS + " (" + 
                        KEY_ROWID + " integer primary key autoincrement, " +            
                        KEY_NAME + " text not null, " +
                        KEY_SETTING_VALUE + " integer not null" +
                    ");";
    
    private static final String DATABASE_INIT_SETTING_SUM = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" +
    		            KEY_NAME + "," + KEY_SETTING_VALUE + ") " +
    		            "values " +
    		            "('" + Setting.SETTING_FIELD_SUM + "',0" +
    		        ");";
    
    private static final String DATABASE_INIT_SETTING_TAX = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" +
            KEY_NAME + "," + KEY_SETTING_VALUE + ") " +
            "values " +
            "('" + Setting.SETTING_FIELD_TAX + "',0" +
        ");";
    
    private static final String DATABASE_INIT_SETTING_COMMENT = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" +
            KEY_NAME + "," + KEY_SETTING_VALUE + ") " +
            "values " +
            "('" + Setting.SETTING_FIELD_COMMENT + "',0" +
        ");";
    
    private static final String DATABASE_INIT_SETTING_LOCATION = "INSERT INTO " + DATABASE_TABLE_SETTINGS + " (" +
            KEY_NAME + "," + KEY_SETTING_VALUE + ") " +
            "values " +
            "('" + Setting.SETTING_FIELD_LOCATION + "',0" +
        ");";
    
    // FIXME Add accounts table with CRUD and read file with accounts for initial values (uk, se)

    /**
     * 
     * @param context
     *            the Context within which to work
     */
    public DbAdapter(Context context) { this.context = context; }

    /**
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException
    {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() { dbHelper.close(); }

    /**
     * 
     * @param tax 
     * @param sum 
     * @param date 
     * @param time 
     * @param location 
     * @param title
     *            the title
     * @param body
     *            the body
     * @return rowId or -1 if failed
     */
    public boolean createReceipt(String name, String photo, String date, String time, String locLat, String locLong, String sum, String tax, String comment)
    {
        ContentValues values = putReceiptValues(name, photo, date, time, locLat, locLong, sum, tax, comment);
        return db.insert(DATABASE_TABLE_RECEIPTS, null, values) > 0;
    }

    /**
     * @param rowId
     *            id to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteItem(long rowId) 
    { 
        if(SELECTED_TABLE.equals(""))
            throw new IllegalAccessError("Unknown database to delete row from, please select a table.");
        return db.delete(SELECTED_TABLE, KEY_ROWID + "=" + rowId, null) > 0; 
    }
    
    /**
     * @param rowId
     *            id to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteReceipt(long rowId) 
    { 
        SELECTED_TABLE = DATABASE_TABLE_RECEIPTS;
        return deleteItem(rowId);
    }

    /**
     * Return a Cursor over the list entries in the receipt table
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchReceipts(int limit) 
    { 
        Cursor cursor;
        if(limit > 0)
        {
            cursor = db.query(DATABASE_TABLE_RECEIPTS, new String[] 
                    {KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_DATE, KEY_TIME, KEY_LOCATION_LAT, KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT}, 
                    null, null, null, null, null, String.valueOf(limit));
        }
        else
        {
            cursor = db.query(DATABASE_TABLE_RECEIPTS, new String[] 
                    {KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_DATE, KEY_TIME, KEY_LOCATION_LAT, KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT}, 
                    null, null, null, null, null);
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
     * @return Cursor over all notes
     */
    public Cursor fetchAllSettings() 
    { 
        Cursor cursor = db.query(DATABASE_TABLE_SETTINGS, new String[] 
                                {KEY_ROWID, KEY_NAME, KEY_SETTING_VALUE}, 
                                null, null, null, null, null);
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
     *            id of receipt to retrieve
     * @return Cursor positioned to matching receipt, if found
     * @throws SQLException
     *             if receipt could not be found/retrieved
     */
    public Cursor fetchReceipt(long rowId) throws SQLException
    {

        Cursor cursor = db.query(true, DATABASE_TABLE_RECEIPTS, new String[] 
                                {KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_DATE, KEY_TIME, KEY_LOCATION_LAT, KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT}, 
                                KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;

    }
    
    public Cursor fetchLastReceipt()
    {
        Cursor cursor = db.query(true, DATABASE_TABLE_RECEIPTS, new String[] 
                {KEY_ROWID, KEY_NAME, KEY_PHOTO, KEY_DATE, KEY_TIME, KEY_LOCATION_LAT, KEY_LOCATION_LONG, KEY_SUM, KEY_TAX, KEY_COMMENT}, 
                null, null, null, null, KEY_ROWID + " DESC", "1");
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
     *            id of setting to retrieve
     * @return Cursor positioned to matching setting, if found
     * @throws SQLException
     *             if setting could not be found/retrieved
     */
    public Cursor fetchSetting(String name) throws SQLException
    {

        Cursor cursor = db.query(true, DATABASE_TABLE_SETTINGS, new String[] 
                                {KEY_ROWID, KEY_NAME, KEY_SETTING_VALUE}, 
                                KEY_NAME + "='" + name + "'", null, null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;

    }

    /**
     * 
     * @param rowId
     *            id of note to update
     * @param title
     *            value to set note title to
     * @param body
     *            value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateReceipt(long rowId, String name, String photo, String date, String time, String locLat, String locLong, String sum, String tax, String comment)
    {
        ContentValues values = putReceiptValues(name, photo, date, time, locLat, locLong, sum, tax, comment);
        return db.update(DATABASE_TABLE_RECEIPTS, values, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * 
     * @param rowId
     * @param name
     * @param value
     * @return
     */
    public boolean updateSetting(String name, int value)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_SETTING_VALUE, value);
        return db.update(DATABASE_TABLE_SETTINGS, values, KEY_NAME + "='" + name + "'", null) > 0;
    }
    
    /**
     * 
     * @param name
     * @param photo
     * @param date
     * @param time
     * @param locLat
     * @param locLong
     * @param sum
     * @param tax
     * @param comment
     * @return
     */
    private ContentValues putReceiptValues(String name, String photo, String date, String time, String locLat, String locLong, String sum, String tax, String comment)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHOTO, photo);
        values.put(KEY_DATE, date);
        values.put(KEY_TIME, time);
        values.put(KEY_LOCATION_LAT, locLat);
        values.put(KEY_LOCATION_LONG, locLong);
        values.put(KEY_SUM, sum);
        values.put(KEY_TAX, tax);
        values.put(KEY_COMMENT, comment);
        return values;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper
    {


        DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

        @Override
        public void onCreate(SQLiteDatabase db) 
        { 
            db.execSQL(DATABASE_CREATE_TABLE_RECEIPTS); 
            db.execSQL(DATABASE_CREATE_TABLE_SETTINGS);
            db.execSQL(DATABASE_INIT_SETTING_SUM);
            db.execSQL(DATABASE_INIT_SETTING_TAX);
            db.execSQL(DATABASE_INIT_SETTING_COMMENT);
            db.execSQL(DATABASE_INIT_SETTING_LOCATION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading " + DATABASE_TABLE_RECEIPTS + " and " + DATABASE_CREATE_TABLE_SETTINGS + " database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RECEIPTS);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SETTINGS);
            onCreate(db);
        }
    }
}
