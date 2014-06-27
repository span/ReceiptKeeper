package net.danielkvist.receipttracker.contentprovider;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ReceiptTable {

	public static final String TABLE_NAME = "receipts";

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
	public static final String[] COLUMNS = { KEY_ROWID, KEY_NAME, KEY_PHOTO,
			KEY_TIMESTAMP, KEY_LOCATION_LAT, KEY_LOCATION_LONG, KEY_SUM,
			KEY_TAX, KEY_COMMENT, KEY_ACCOUNT_ID };

	private static final String DATABASE_CREATE_TABLE_RECEIPTS = "CREATE TABLE "
			+ TABLE_NAME
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_NAME
			+ " text not null, "
			+ KEY_PHOTO
			+ " text not null,"
			+ KEY_TIMESTAMP
			+ " numeric not null,"
			+ KEY_LOCATION_LAT
			+ " text not null,"
			+ KEY_LOCATION_LONG
			+ " text not null,"
			+ KEY_SUM
			+ " text not null,"
			+ KEY_TAX
			+ " text not null,"
			+ KEY_COMMENT
			+ " text not null,"
			+ KEY_ACCOUNT_ID
			+ " integer not null" + ");";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_TABLE_RECEIPTS);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(ReceiptTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
