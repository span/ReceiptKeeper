package net.danielkvist.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.content.ReceiptAccount;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * This class handles communication with the user and the database. It provides a layer between the database handling
 * and the converting of data from the database provided Cursor to the more specific data types that are used in the
 * Activities and Fragments.
 * 
 * @author Daniel Kvist
 * 
 */
public class Communicator
{
	private static final String MESSAGE_RECEIPT_WAS_DELETED = "Receipt was successfully deleted from the database.";
	private static final String MESSAGE_DATA_WAS_SAVED = "Data was saved to database!";
	private static final String MESSAGE_COULD_NOT_SAVE = "Could not save to database... try again and please report it to the developer!";
	private static final String MESSAGE_COULD_NOT_OPEN = "Could not open database... try again and please report it to the developer!";
	private static final String MESSAGE_COULD_NOT_DELETE_FILE = "Could not delete the file associated with the receipt. ";

	private Context context;
	private DbAdapter dbAdapter;

	/**
	 * Constructor that takes a context as parameter and instantiates a new database adapter
	 * 
	 * @param context
	 */
	public Communicator(Context context)
	{
		this.context = context;
		this.dbAdapter = new DbAdapter(context);
	}

	/**
	 * Deletes the receipt that is being passed in both from disk and from the database. Shows messages to the user on
	 * results.
	 * 
	 * @param receipt
	 *            the receipt to delete
	 * @return true if file deletion and db-deletion are true
	 */
	public boolean deleteReceipt(Receipt receipt)
	{
		String pathToFile = receipt.getPhoto();
		File file = new File(pathToFile);
		boolean deleted = file.delete();
		boolean result = false;

		if (openDatabase())
		{
			dbAdapter.deleteReceipt(receipt.getId());
			showToast(MESSAGE_RECEIPT_WAS_DELETED);
			closeDatabase();
		}
		if (!deleted)
		{
			Log.d(context.getString(R.string.tag_receipttracker), "Failed to delete file: " + pathToFile);
			showToast(MESSAGE_COULD_NOT_DELETE_FILE);
		}
		return result && deleted;
	}

	/**
	 * Gets the last added receipt from the database
	 * 
	 * @return the receipt
	 */
	public Receipt getLatestReceipt()
	{
		Receipt receipt = null;
		if (openDatabase())
		{
			Cursor cursor = dbAdapter.fetchLastReceipt();
			if (cursor != null)
			{
				cursor.moveToFirst();
				receipt = buildReceipt(cursor);
			}
			closeDatabase(cursor);
		}
		return receipt;
	}

	/**
	 * Gets a list of receipts with the limit of the parameter.
	 * 
	 * @param limit
	 *            the number of receipts
	 * @return an ArrayList with receipts
	 */
	public List<Receipt> getReceipts(int limit)
	{
		List<Receipt> receiptList = null;
		if (openDatabase())
		{
			Cursor cursor = dbAdapter.fetchReceipts(limit);
			receiptList = buildReceiptList(cursor);
			closeDatabase(cursor);
		}
		return receiptList;
	}

	/**
	 * Gets a list of receipts with dates between the from and to date.
	 * 
	 * @param timeFrom
	 *            lower date restriction
	 * @param timeTo
	 *            upper date restriction
	 * @return an ArrayList with receipts
	 */
	public List<Receipt> getReceipts(long timeFrom, long timeTo)
	{
		List<Receipt> receiptList = null;
		if (openDatabase())
		{
			Cursor cursor = dbAdapter.fetchReceipts(timeFrom, timeTo);
			receiptList = buildReceiptList(cursor);
			closeDatabase(cursor);
		}
		return receiptList;
	}

	/**
	 * Convenience method that gets a list of receipts that has the same code as the code passed in
	 * 
	 * @param code
	 *            the receipt account code
	 * @return the sum of the receipts sum field
	 */
	public int getReceiptsSum(long code)
	{
		List<Long> accountCode = new ArrayList<Long>();
		accountCode.add(code);
		return getReceiptsSum(accountCode);
	}

	/**
	 * Builds a where statement for the dbadapter and passes it. Gets a list of receipts that has the same codes as the
	 * codes in the List that passed in
	 * 
	 * @param accountCodes
	 *            the receipt account codes in a List
	 * @return the sum of the receipts sum field
	 */
	public int getReceiptsSum(List<Long> accountCodes)
	{
		int result = 0;
		if (openDatabase())
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < accountCodes.size(); i++)
			{
				sb.append(accountCodes.get(i));
				if (i < (accountCodes.size() - 1))
				{
					sb.append(" OR ");
					sb.append(DbAdapter.KEY_ACCOUNT_ID);
					sb.append("=");
				}
			}
			Cursor cursor = dbAdapter.fetchReceiptsSum(sb.toString());
			if (cursor != null)
			{
				cursor.moveToFirst();
				result = cursor.getInt(0);
			}
			closeDatabase(cursor);
		}
		return result;
	}

	/**
	 * Searches for receipts that contains the provided query and returns a list of them
	 * 
	 * @param query
	 *            the query to search for
	 * @return an ArrayList with receipts
	 */
	public List<Receipt> searchReceipts(String query)
	{
		List<Receipt> receiptList = null;
		if (openDatabase())
		{
			Cursor cursor = dbAdapter.searchReceiptName(query);
			receiptList = buildReceiptList(cursor);
			closeDatabase(cursor);
		}
		return receiptList;
	}

	/**
	 * Saves the receipt to the databse. If the receipt has an id > 0 it updates the database row, if the id < 0 it
	 * creates a new row.
	 * 
	 * @param receipt
	 * @return int with new row id or updated rows or -1
	 */
	public int saveReceipt(Receipt receipt)
	{
		if (receipt.getId() > 0)
		{
			return updateReceipt(receipt);
		}
		else
		{
			return (int) insertReceipt(receipt);
		}
	}

	/**
	 * Creates a new row out of the supplied Receipt
	 * 
	 * @param receipt
	 * @return new rowId or -1
	 */
	private long insertReceipt(Receipt receipt)
	{
		long result = -1;
		if (openDatabase())
		{
			result = dbAdapter.createReceipt(receipt.getName(), receipt.getPhoto(), receipt.getTimestamp(),
					receipt.getLocationLat(), receipt.getLocationLong(), receipt.getSum(), receipt.getTax(),
					receipt.getComment(), receipt.getReceiptAccountCode());
			closeDatabase();
		}
		showResult(result > 0);
		return result;
	}

	/**
	 * Updates a row out with the supplied Receipt
	 * 
	 * @param receipt
	 * @return number of rows affected
	 */
	public int updateReceipt(Receipt receipt)
	{
		int result = -1;
		if (openDatabase())
		{
			result = dbAdapter.updateReceipt(receipt.getId(), receipt.getName(), receipt.getPhoto(),
					receipt.getTimestamp(), receipt.getLocationLat(), receipt.getLocationLong(), receipt.getSum(),
					receipt.getTax(), receipt.getComment(), receipt.getReceiptAccountCode());
			closeDatabase();
		}
		showResult(result > 0);
		return result;
	}

	/**
	 * Deletes the provided receipt account
	 * 
	 * @param receiptAccount
	 *            the account to delete
	 * @return true if successful
	 */
	public boolean deleteReceiptAccount(ReceiptAccount receiptAccount)
	{
		boolean result = false;
		if (openDatabase())
		{
			result = dbAdapter.deleteReceiptAccount(receiptAccount);
			closeDatabase();
		}
		showResult(result);
		return result;
	}

	/**
	 * Gets a list of all receipt accounts
	 * 
	 * @return an ArrayList of accounts
	 */
	public List<ReceiptAccount> getReceiptAccounts()
	{
		List<ReceiptAccount> receiptAccountList = null;
		if (openDatabase())
		{
			Cursor cursor = dbAdapter.fetchReceiptAccounts();
			if (cursor != null)
			{
				ReceiptAccount receiptAccount;
				int visible = getSettingValue(Setting.SETTING_ACCOUNT_DEFAULTS);
				receiptAccountList = new ArrayList<ReceiptAccount>();
				cursor.moveToFirst();
				while (!cursor.isAfterLast())
				{
					receiptAccount = new ReceiptAccount(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID)),
							cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_CODE)), cursor.getString(cursor
									.getColumnIndex(DbAdapter.KEY_NAME)), cursor.getString(cursor
									.getColumnIndex(DbAdapter.KEY_CATEGORY)));

					if (visible == View.VISIBLE || (visible == View.GONE && receiptAccount.isUserAdded()))
					{
						receiptAccountList.add(receiptAccount);
					}

					cursor.moveToNext();
				}
			}
			closeDatabase(cursor);
		}
		return receiptAccountList;
	}

	/**
	 * Saves the receipt account. If the account has an id > 0 it updates the database row, if the id < 0 it creates a
	 * new row.
	 * 
	 * @param receiptAccount
	 * @return true if successful
	 */
	public boolean saveReceiptAccount(ReceiptAccount receiptAccount)
	{
		if (receiptAccount.getRowId() > 0)
		{
			return updateReceiptAccount(receiptAccount);
		}
		else
		{
			return insertReceiptAccount(receiptAccount);
		}
	}

	/**
	 * Creates a new row in the database for the account
	 * 
	 * @param receiptAccount
	 * @return true if successful
	 */
	private boolean insertReceiptAccount(ReceiptAccount receiptAccount)
	{
		boolean result = false;
		if (openDatabase())
		{
			result = dbAdapter.createReceiptAccount(receiptAccount.getCode(), receiptAccount.getName(),
					receiptAccount.getCategory());
			closeDatabase();
		}
		showResult(result);
		return result;
	}

	/**
	 * Updates a row in the database with the provided account
	 * 
	 * @param receiptAccount
	 * @return true if successful
	 */
	private boolean updateReceiptAccount(ReceiptAccount receiptAccount)
	{
		boolean result = false;
		if (openDatabase())
		{
			result = dbAdapter.updateReceiptAccount(receiptAccount.getRowId(), receiptAccount.getCode(),
					receiptAccount.getName(), receiptAccount.getCategory());
			closeDatabase();
		}
		showResult(result);
		return result;
	}

	/**
	 * Gets a list of all the Settings that are stored in the database.
	 * 
	 * @return a HashMap with the setting name and setting value
	 */
	public HashMap<String, Integer> getAllSettings()
	{
		HashMap<String, Integer> settingsMap = null;
		if (openDatabase())
		{
			Cursor cursor = dbAdapter.fetchAllSettings();
			settingsMap = new HashMap<String, Integer>();
			if (cursor != null)
			{
				cursor.moveToFirst();
				while (!cursor.isAfterLast())
				{
					settingsMap.put(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_NAME)),
							cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SETTING_VALUE)));
					cursor.moveToNext();
				}
			}
			closeDatabase(cursor);
		}

		return settingsMap;
	}

	/**
	 * Gets the value for a specific Setting
	 * 
	 * @param name
	 *            the setting name
	 * @return the setting value
	 */
	public int getSettingValue(String name)
	{
		int value = -1;
		if (openDatabase())
		{
			Cursor cursor = dbAdapter.fetchSetting(name);
			if (cursor != null)
			{
				cursor.moveToFirst();
				value = cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SETTING_VALUE));
			}
			closeDatabase(cursor);
		}
		return value;
	}

	/**
	 * Saves the Setting that is passed in
	 * 
	 * @param setting
	 *            the setting to save
	 */
	public boolean saveSetting(Setting setting)
	{
		return updateSetting(setting);
	}

	/**
	 * Updates the database row for the specific Setting
	 * 
	 * @param setting
	 * @return true if successfull
	 */
	private boolean updateSetting(Setting setting)
	{
		boolean result = false;
		if (openDatabase())
		{
			result = dbAdapter.updateSetting(setting.getName(), setting.getValue());
			closeDatabase();
		}
		return result;
	}

	/**
	 * Gets a List of string representations of the name of the resources that make up the receipt account categories.
	 * 
	 * @return a list of resource names
	 */
	public List<String> getReceiptAccountCategories()
	{
		List<String> list = new ArrayList<String>();
		if (openDatabase())
		{
			Cursor cursor = dbAdapter.fetchReceiptAccountCategories();
			if (cursor != null)
			{
				cursor.moveToFirst();
				while (!cursor.isAfterLast())
				{
					list.add(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_CATEGORY)));
					cursor.moveToNext();
				}
			}
			closeDatabase(cursor);
		}
		return list;
	}

	/**
	 * Builds an ArrayList out of the provided Cursor
	 * 
	 * @param cursor
	 *            the cursor from the query
	 * @return an ArrayList with the receipts
	 */
	private List<Receipt> buildReceiptList(Cursor cursor)
	{
		List<Receipt> receiptList = null;
		if (cursor != null)
		{
			Receipt receipt;
			cursor.moveToFirst();
			receiptList = new ArrayList<Receipt>();
			while (!cursor.isAfterLast())
			{
				receipt = buildReceipt(cursor);
				receiptList.add(receipt);
				cursor.moveToNext();
			}
		}
		return receiptList;
	}

	/**
	 * Builds a Receipt from the passed in Cursor
	 * 
	 * @param cursor
	 * @return a new Receipt
	 */
	private Receipt buildReceipt(Cursor cursor)
	{
		Receipt receipt = null;
		if (cursor.getCount() > 0)
		{
			receipt = new Receipt(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID)), cursor.getString(cursor
					.getColumnIndex(DbAdapter.KEY_NAME)), cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_PHOTO)),
					cursor.getLong(cursor.getColumnIndex(DbAdapter.KEY_TIMESTAMP)), cursor.getString(cursor
							.getColumnIndex(DbAdapter.KEY_LOCATION_LAT)), cursor.getString(cursor
							.getColumnIndex(DbAdapter.KEY_LOCATION_LONG)), cursor.getString(cursor
							.getColumnIndex(DbAdapter.KEY_SUM)), cursor.getString(cursor
							.getColumnIndex(DbAdapter.KEY_TAX)), cursor.getString(cursor
							.getColumnIndex(DbAdapter.KEY_COMMENT)), cursor.getInt(cursor
							.getColumnIndex(DbAdapter.KEY_ACCOUNT_ID)));
		}
		return receipt;
	}

	/**
	 * Handles logging and messaging to the user if there was a problem with the database
	 * 
	 * @param e
	 */
	private void catchSQLException(SQLException e)
	{
		Log.d(context.getString(R.string.tag_receipttracker), e.getMessage());
		showToast(MESSAGE_COULD_NOT_OPEN);
	}

	/**
	 * Tries to open the database and shows an error if it fails
	 * 
	 * @return true if successful
	 */
	private boolean openDatabase()
	{
		try
		{
			dbAdapter.open();
			return true;
		}
		catch (SQLException e)
		{
			catchSQLException(e);
			return false;
		}
	}

	/**
	 * Closes the database
	 */
	private void closeDatabase()
	{
		dbAdapter.close();
	}

	/**
	 * Closes the database and cursor
	 * 
	 * @param cursor
	 *            the cursor to close
	 */
	private void closeDatabase(Cursor cursor)
	{
		cursor.close();
		closeDatabase();
	}

	/**
	 * Shows successful result if result is true and negative result if result is false
	 * 
	 * @param result
	 *            the result
	 */
	private void showResult(boolean result)
	{
		if (result)
		{
			showToast(MESSAGE_DATA_WAS_SAVED);
		}
		else
		{
			showToast(MESSAGE_COULD_NOT_SAVE);
		}
	}

	/**
	 * Shows the passed in message to the user as a Toast.
	 */
	public void showToast(String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

}
