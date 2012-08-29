package net.danielkvist.receipttracker.contentprovider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AccountTable
{
	public static final String TABLE_NAME = "accounts";

	public static final String KEY_ROWID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_CODE = "code";
	public static final String KEY_CATEGORY = "category";
	public static final String[] COLUMNS = { KEY_ROWID, KEY_NAME, KEY_CODE, KEY_CATEGORY };

	
	public static void onCreate(SQLiteDatabase db, Context context)
	{
		initDatabaseData(db, context);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, Context context)
	{
		Log.w(ReceiptTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		onCreate(db, context);
	}
	
	/**
	 * This method does the import of the accounts.sql data
	 * 
	 * @param db
	 * @param context
	 */
	private static void initDatabaseData(SQLiteDatabase db, Context context)
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
			Log.e(AccountTable.class.getName(), "read database init file error at db import");
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
					Log.e(AccountTable.class.getName(), "buffer reader close error at db import");
				}
			}
		}
	}
}
