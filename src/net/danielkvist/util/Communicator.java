package net.danielkvist.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.content.ReceiptAccount;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

public class Communicator
{
    private static final String MESSAGE_RECEIPT_WAS_DELETED = "Receipt was successfully deleted from the database.";
    private static final String MESSAGE_DATA_WAS_SAVED = "Data was saved to database!";
    private static final String MESSAGE_COULD_NOT_SAVE = "Could not save to database... try again and please report it to the developer!";
    private static final String MESSAGE_COULD_NOT_OPEN = "Could not open database... try again and please report it to the developer!";
    private static final String MESSAGE_COULD_NOT_DELETE_FILE = "Could not delete the file associated with the receipt. ";

    private Context context;
    private DbAdapter dbAdapter;

    public Communicator(Context context)
    {
        this.context = context;
        this.dbAdapter = new DbAdapter(context);
    }

    public boolean deleteReceipt(Receipt receipt)
    {
        String pathToFile = receipt.getPhoto();
        File file = new File(pathToFile);
        boolean deleted = file.delete();
        boolean result = false;

        if(openDatabase())
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

    public Receipt getLatestReceipt()
    {
        Receipt receipt = null;
        if(openDatabase())
        {
            Cursor cursor = dbAdapter.fetchLastReceipt();
            receipt = buildReceipt(cursor);
            closeDatabase();
        }
        return receipt;
    }

    public ArrayList<Receipt> getReceipts(int limit)
    {
        ArrayList<Receipt> receiptList = null;
        if(openDatabase())
        {
            Cursor cursor = dbAdapter.fetchReceipts(limit);
            receiptList = buildReceiptList(cursor);
            closeDatabase();
        }
        return receiptList;
    }

    public ArrayList<Receipt> getReceipts(long timeFrom, long timeTo)
    {
        ArrayList<Receipt> receiptList = null;
        if(openDatabase())
        {
            Cursor cursor = dbAdapter.fetchReceipts(timeFrom, timeTo);
            receiptList = buildReceiptList(cursor);
            closeDatabase();
        }
        return receiptList;
    }

    public ArrayList<Receipt> searchReceipts(String query)
    {
        ArrayList<Receipt> receiptList = null;
        if(openDatabase())
        {
            Cursor cursor = dbAdapter.searchReceiptName(query);
            receiptList = buildReceiptList(cursor);
            closeDatabase();
        }
        return receiptList;
    }

    public boolean saveReceipt(Receipt receipt)
    {
        if (receipt.getId() > 0)
        {
            return updateReceipt(receipt);
        }
        else
        {
            return insertReceipt(receipt);
        }
    }

    private boolean insertReceipt(Receipt receipt)
    {
        boolean result = false;
        if(openDatabase())
        {
            result = dbAdapter.createReceipt(receipt.getName(), receipt.getPhoto(), receipt.getTimestamp(), receipt.getLocationLat(),
                    receipt.getLocationLong(), receipt.getSum(), receipt.getTax(), receipt.getComment(), receipt.getReceiptAccountId());
            closeDatabase();
        }
        showResult(result);
        return result;
    }

    public boolean updateReceipt(Receipt receipt)
    {
        boolean result = false;
        if(openDatabase())
        {
            result = dbAdapter.updateReceipt(receipt.getId(), receipt.getName(), receipt.getPhoto(), receipt.getTimestamp(),
                    receipt.getLocationLat(), receipt.getLocationLong(), receipt.getSum(), receipt.getTax(), receipt.getComment(),
                    receipt.getReceiptAccountId());
            closeDatabase();
        }
        showResult(result);
        return result;
    }

    public boolean deleteReceiptAccount(ReceiptAccount receiptAccount)
    {
        boolean result = false;
        if(openDatabase())
        {
            result = dbAdapter.deleteReceiptAccount(receiptAccount.getCode());
            closeDatabase();
        }
        showResult(result);
        return result;
    }

    public ArrayList<ReceiptAccount> getReceiptAccounts()
    {
        ArrayList<ReceiptAccount> receiptAccountList = null;
        if(openDatabase())
        {
            Cursor cursor = dbAdapter.fetchReceiptAccounts();
            if (cursor != null)
            {
                ReceiptAccount receiptAccount;
                receiptAccountList = new ArrayList<ReceiptAccount>();
                while (!cursor.isAfterLast())
                {
                    receiptAccount = new ReceiptAccount(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID)), cursor.getInt(cursor
                            .getColumnIndex(DbAdapter.KEY_CODE)), cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_NAME)));
                    receiptAccountList.add(receiptAccount);
                    cursor.moveToNext();
                }
            }
            closeDatabase();
        }
        return receiptAccountList;
    }

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

    private boolean insertReceiptAccount(ReceiptAccount receiptAccount)
    {
        boolean result = false;
        if(openDatabase())
        {
            result = dbAdapter.createReceiptAccount(receiptAccount.getCode(), receiptAccount.getName());
            closeDatabase();
        }
        showResult(result);
        return result;
    }

    private boolean updateReceiptAccount(ReceiptAccount receiptAccount)
    {
        boolean result = false;
        if(openDatabase())
        {
            result = dbAdapter.updateReceiptAccount(receiptAccount.getRowId(), receiptAccount.getCode(), receiptAccount.getName());
            closeDatabase();
        }
        showResult(result);
        return result;
    }

    public HashMap<String, Integer> getAllSettings()
    {
        HashMap<String, Integer> settingsMap = null;
        if(openDatabase())
        {
            Cursor cursor = dbAdapter.fetchAllSettings();
            settingsMap = new HashMap<String, Integer>();
            if (cursor != null)
            {
                while (!cursor.isAfterLast())
                {
                    settingsMap.put(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_NAME)),
                            cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SETTING_VALUE)));
                    cursor.moveToNext();
                }
            }
            closeDatabase();
        }
        
        return settingsMap;
    }

    public int getSettingValue(String name)
    {
        int value = -1;
        if(openDatabase())
        {
            Cursor cursor = dbAdapter.fetchSetting(name);
            if (cursor != null)
            {
                value = cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SETTING_VALUE));
            }
            closeDatabase();
        }
        return value;
    }

    public void saveSetting(Setting setting)
    {
        updateSetting(setting);
    }

    private boolean updateSetting(Setting setting)
    {
        boolean result = false;
        if(openDatabase())
        {
            result = dbAdapter.updateSetting(setting.getName(), setting.getValue());
            closeDatabase();
        }
        showResult(result);
        return result;
    }

    private ArrayList<Receipt> buildReceiptList(Cursor cursor)
    {
        ArrayList<Receipt> receiptList = null;
        if (cursor != null)
        {
            Receipt receipt;
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

    private Receipt buildReceipt(Cursor cursor)
    {
        Receipt receipt = null;
        if (cursor.getCount() > 0)
        {
            receipt = new Receipt(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID)), cursor.getString(cursor
                    .getColumnIndex(DbAdapter.KEY_NAME)), cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_PHOTO)),
                    cursor.getLong(cursor.getColumnIndex(DbAdapter.KEY_TIMESTAMP)), cursor.getString(cursor
                            .getColumnIndex(DbAdapter.KEY_LOCATION_LAT)), cursor.getString(cursor
                            .getColumnIndex(DbAdapter.KEY_LOCATION_LONG)), cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_SUM)),
                    cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_TAX)), cursor.getString(cursor
                            .getColumnIndex(DbAdapter.KEY_COMMENT)), cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ACCOUNT_ID)));
        }
        return receipt;
    }

    private void catchSQLException(SQLException e)
    {
        Log.d(context.getString(R.string.tag_receipttracker), e.getMessage());
        showToast(MESSAGE_COULD_NOT_OPEN);
    }
    
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

    private void closeDatabase()
    {
        dbAdapter.close();
    }

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

    public void showToast(String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
