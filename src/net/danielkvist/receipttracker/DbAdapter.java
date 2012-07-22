package net.danielkvist.receipttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter
{

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "id";

    private static final String TAG = "DbAdapter";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE = "CREATE TABLE notes (id integer primary key autoincrement, "
            + "title text not null, body text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "receipts";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading " + DATABASE_TABLE + " database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * 
     * @param context
     *            the Context within which to work
     */
    public DbAdapter(Context context)
    {
        this.context = context;
    }

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

    public void close()
    {
        dbHelper.close();
    }

    /**
     * 
     * @param title
     *            the title
     * @param body
     *            the body
     * @return rowId or -1 if failed
     */
    public long createItem(String title, String body)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * @param rowId
     *            id to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteItem(long rowId)
    {

        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all entries in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllItems()
    {

        return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE, KEY_BODY }, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId
     *            id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException
     *             if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException
    {

        Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE, KEY_BODY }, 
                                    KEY_ROWID + "=" + rowId, null,
                                    null, null, null, null);
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
    public boolean updateItem(long rowId, String title, String body)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_BODY, body);

        return db.update(DATABASE_TABLE, values, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
