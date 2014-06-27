package net.danielkvist.receipttracker.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 1;
	private Context context;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase db) {
		ReceiptTable.onCreate(db);
		AccountTable.onCreate(db, context);
		SettingTable.onCreate(db);
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ReceiptTable.onUpgrade(db, oldVersion, newVersion);
		AccountTable.onUpgrade(db, oldVersion, newVersion, context);
		SettingTable.onUpgrade(db, oldVersion, newVersion);
	}

}
