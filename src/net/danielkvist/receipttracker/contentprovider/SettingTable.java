package net.danielkvist.receipttracker.contentprovider;

import net.danielkvist.util.Setting;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SettingTable
{
	public static final String TABLE_NAME = "settings";
	public static final String KEY_SETTING_VALUE = "setting_value";
	public static final String KEY_NAME = "name";
	public static final String KEY_ROWID = "id";
	public static final String[] COLUMNS = { KEY_ROWID, KEY_NAME, KEY_SETTING_VALUE };
	
	private static final String DATABASE_CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_NAME + " ("
			+ KEY_ROWID + " integer primary key autoincrement, " + KEY_NAME + " text not null, " + KEY_SETTING_VALUE
			+ " integer not null" + ");";

	private static final String DATABASE_INIT_SETTING_SUM = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME
			+ "," + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_SUM + "',0" + ");";

	private static final String DATABASE_INIT_SETTING_TAX = "INSERT INTO " + TABLE_NAME + " (" + KEY_NAME
			+ "," + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_TAX + "',0" + ");";

	private static final String DATABASE_INIT_SETTING_COMMENT = "INSERT INTO " + TABLE_NAME + " ("
			+ KEY_NAME + "," + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_COMMENT + "',0"
			+ ");";

	private static final String DATABASE_INIT_SETTING_LOCATION = "INSERT INTO " + TABLE_NAME + " ("
			+ KEY_NAME + "," + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_LOCATION + "',0"
			+ ");";

	private static final String DATABASE_INIT_SETTING_ACCOUNT = "INSERT INTO " + TABLE_NAME + " ("
			+ KEY_NAME + "," + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_FIELD_ACCOUNT + "',0"
			+ ");";

	private static final String DATABASE_INIT_SETTING_DEFAULT_ACCOUNTS = "INSERT INTO " + TABLE_NAME
			+ " (" + KEY_NAME + "," + KEY_SETTING_VALUE + ") " + "values " + "('" + Setting.SETTING_ACCOUNT_DEFAULTS
			+ "',0" + ");";

	public static void onCreate(SQLiteDatabase db)
	{
		// FIXME do in db transaction
		db.execSQL(DATABASE_CREATE_TABLE_SETTINGS);
		db.execSQL(DATABASE_INIT_SETTING_SUM);
		db.execSQL(DATABASE_INIT_SETTING_TAX);
		db.execSQL(DATABASE_INIT_SETTING_COMMENT);
		db.execSQL(DATABASE_INIT_SETTING_LOCATION);
		db.execSQL(DATABASE_INIT_SETTING_ACCOUNT);
		db.execSQL(DATABASE_INIT_SETTING_DEFAULT_ACCOUNTS);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(ReceiptTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
