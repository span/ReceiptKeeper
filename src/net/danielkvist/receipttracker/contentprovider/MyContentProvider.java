package net.danielkvist.receipttracker.contentprovider;

import java.util.Arrays;
import java.util.HashSet;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider
{
	private DBHelper dbHelper;

	public static final int RECEIPTS = 10;
	public static final int RECEIPT_ID = 11;
	public static final int ACCOUNTS = 20;
	public static final int ACCOUNT_ID = 21;
	public static final int SETTINGS = 30;
	public static final int SETTING_ID = 31;

	private static final String AUTHORITY = "net.danielkvist.receipttracker.contentprovider";

	private static final String BASE_PATH = "data";
	private static final String RECEIPT_PATH = BASE_PATH + "/" + ReceiptTable.TABLE_NAME;
	private static final String ACCOUNT_PATH = BASE_PATH + "/" + AccountTable.TABLE_NAME;
	private static final String SETTING_PATH = BASE_PATH + "/" + SettingTable.TABLE_NAME;
	
	public static final Uri CONTENT_URI_RECEIPT = Uri.parse("content://" + AUTHORITY + "/" + RECEIPT_PATH);
	public static final Uri CONTENT_URI_ACCOUNT = Uri.parse("content://" + AUTHORITY + "/" + ACCOUNT_PATH);
	public static final Uri CONTENT_URI_SETTING = Uri.parse("content://" + AUTHORITY + "/" + SETTING_PATH);

	public static final String CONTENT_TYPE_RECEIPT = ContentResolver.CURSOR_DIR_BASE_TYPE + "/receipts";
	public static final String CONTENT_ITEM_TYPE_RECEIPT = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/receipt";
	
	public static final String CONTENT_TYPE_ACCOUNT = ContentResolver.CURSOR_DIR_BASE_TYPE + "/accounts";
	public static final String CONTENT_ITEM_TYPE_ACCOUNT = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/account";
	
	public static final String CONTENT_TYPE_SETTING = ContentResolver.CURSOR_DIR_BASE_TYPE + "/settings";
	public static final String CONTENT_ITEM_TYPE_SETTING = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/setting";
	
	

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static
	{
		sURIMatcher.addURI(AUTHORITY, RECEIPT_PATH, RECEIPTS);
		sURIMatcher.addURI(AUTHORITY, RECEIPT_PATH + "/#", RECEIPT_ID);
		sURIMatcher.addURI(AUTHORITY, ACCOUNT_PATH, ACCOUNTS);
		sURIMatcher.addURI(AUTHORITY, ACCOUNT_PATH + "/#", ACCOUNT_ID);
		sURIMatcher.addURI(AUTHORITY, SETTING_PATH, SETTINGS);
		sURIMatcher.addURI(AUTHORITY, SETTING_PATH + "/#", SETTING_ID);
	}

	@Override
	public boolean onCreate()
	{
		dbHelper = new DBHelper(getContext());
		return false;
	}

	@Override
	synchronized public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		int uriType = sURIMatcher.match(uri);
		String id = uri.getLastPathSegment();
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType)
		{
			case RECEIPTS:
				rowsDeleted = sqlDB.delete(ReceiptTable.TABLE_NAME, selection, selectionArgs);
				break;
			case RECEIPT_ID:
				if (TextUtils.isEmpty(selection))
				{
					rowsDeleted = sqlDB.delete(ReceiptTable.TABLE_NAME, ReceiptTable.KEY_ROWID + "=" + id, null);
				}
				else
				{
					rowsDeleted = sqlDB.delete(ReceiptTable.TABLE_NAME, ReceiptTable.KEY_ROWID + "=" + id + " and "
							+ selection, selectionArgs);
				}
				break;
			case ACCOUNTS:
				rowsDeleted = sqlDB.delete(AccountTable.TABLE_NAME, selection, selectionArgs);
				break;
			case ACCOUNT_ID:
				if (TextUtils.isEmpty(selection))
				{
					rowsDeleted = sqlDB.delete(AccountTable.TABLE_NAME, AccountTable.KEY_ROWID + "=" + id, null);
				}
				else
				{
					rowsDeleted = sqlDB.delete(AccountTable.TABLE_NAME, AccountTable.KEY_ROWID + "=" + id + " and "
							+ selection, selectionArgs);
				}
				break;
			case SETTINGS:
				rowsDeleted = sqlDB.delete(SettingTable.TABLE_NAME, selection, selectionArgs);
				break;
			case SETTING_ID:
				if (TextUtils.isEmpty(selection))
				{
					rowsDeleted = sqlDB.delete(SettingTable.TABLE_NAME, SettingTable.KEY_ROWID + "=" + id, null);
				}
				else
				{
					rowsDeleted = sqlDB.delete(SettingTable.TABLE_NAME, SettingTable.KEY_ROWID + "=" + id + " and "
							+ selection, selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	synchronized public String getType(Uri uri)
	{
		return null;
	}

	@Override
	synchronized public Uri insert(Uri uri, ContentValues values)
	{
		Uri returnUri = null;
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int uriType = sURIMatcher.match(uri);
		long id = 0;
		switch (uriType)
		{
			case RECEIPTS:
				id = sqlDB.insert(ReceiptTable.TABLE_NAME, null, values);
				returnUri = ContentUris.withAppendedId(CONTENT_URI_RECEIPT, id);
				break;
			case ACCOUNTS:
				id = sqlDB.insert(AccountTable.TABLE_NAME, null, values);
				returnUri = ContentUris.withAppendedId(CONTENT_URI_ACCOUNT, id);
				break;
			case SETTINGS:
				id = sqlDB.insert(SettingTable.TABLE_NAME, null, values);
				returnUri = ContentUris.withAppendedId(CONTENT_URI_SETTING, id);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null); // FIXME should this be returnUri?
		return returnUri;
	}

	@Override
	synchronized public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String tableName = "";
		int tableId = 0;
		String rowId = "";
		int uriType = sURIMatcher.match(uri);
		switch (uriType)
		{
			case RECEIPTS:
				tableName = ReceiptTable.TABLE_NAME;
				tableId = RECEIPT_ID;
				break;
			case RECEIPT_ID:
				tableName = ReceiptTable.TABLE_NAME;
				tableId = RECEIPT_ID;
				rowId = ReceiptTable.KEY_ROWID;
				queryBuilder.appendWhere(rowId + "=" + uri.getLastPathSegment());
			case ACCOUNTS:
				tableName = AccountTable.TABLE_NAME;
				tableId = ACCOUNT_ID;
				queryBuilder.setDistinct(true);
				break;
			case ACCOUNT_ID:
				tableName = AccountTable.TABLE_NAME;
				tableId = ACCOUNT_ID;
				rowId = AccountTable.KEY_ROWID;
				queryBuilder.appendWhere(rowId + "=" + uri.getLastPathSegment());
			case SETTINGS:
				tableName = SettingTable.TABLE_NAME;
				tableId = SETTING_ID;
				break;
			case SETTING_ID:
				tableName = SettingTable.TABLE_NAME;
				tableId = SETTING_ID;
				rowId = SettingTable.KEY_ROWID;
				queryBuilder.appendWhere(rowId + "=" + uri.getLastPathSegment());
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		checkColumns(projection, tableId);
		queryBuilder.setTables(tableName);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	synchronized public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		int uriType = sURIMatcher.match(uri);
		String id = uri.getLastPathSegment();
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType)
		{
			case RECEIPTS:
				rowsUpdated = sqlDB.update(ReceiptTable.TABLE_NAME, values, selection, selectionArgs);
				break;
			case RECEIPT_ID:
				if (TextUtils.isEmpty(selection))
				{
					rowsUpdated = sqlDB
							.update(ReceiptTable.TABLE_NAME, values, ReceiptTable.KEY_ROWID + "=" + id, null);
				}
				else
				{
					rowsUpdated = sqlDB.update(ReceiptTable.TABLE_NAME, values, ReceiptTable.KEY_ROWID + "=" + id
							+ " and " + selection, selectionArgs);
				}
				break;
			case ACCOUNTS:
				rowsUpdated = sqlDB.update(AccountTable.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ACCOUNT_ID:
				if (TextUtils.isEmpty(selection))
				{
					rowsUpdated = sqlDB
							.update(AccountTable.TABLE_NAME, values, AccountTable.KEY_ROWID + "=" + id, null);
				}
				else
				{
					rowsUpdated = sqlDB.update(AccountTable.TABLE_NAME, values, AccountTable.KEY_ROWID + "=" + id
							+ " and " + selection, selectionArgs);
				}
				break;
			case SETTINGS:
				rowsUpdated = sqlDB.update(SettingTable.TABLE_NAME, values, selection, selectionArgs);
				break;
			case SETTING_ID:
				if (TextUtils.isEmpty(selection))
				{
					rowsUpdated = sqlDB
							.update(SettingTable.TABLE_NAME, values, SettingTable.KEY_ROWID + "=" + id, null);
				}
				else
				{
					rowsUpdated = sqlDB.update(SettingTable.TABLE_NAME, values, SettingTable.KEY_ROWID + "=" + id
							+ " and " + selection, selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection, int tableId)
	{
		String[] available = {};
		if (projection != null)
		{
			switch (tableId)
			{
				case RECEIPT_ID:
					available = ReceiptTable.COLUMNS;
					break;
				case ACCOUNT_ID:
					available = AccountTable.COLUMNS;
					break;
				case SETTING_ID:
					available = SettingTable.COLUMNS;
					break;
				default:
					break;
			}
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns))
			{
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}

}
