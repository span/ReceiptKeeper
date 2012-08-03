package net.danielkvist.util;

import java.util.ArrayList;
import java.util.HashMap;

import net.danielkvist.receipttracker.activity.MainActivity;
import net.danielkvist.receipttracker.content.Receipt;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

public class Communicator
{

    private static final String MESSAGE_COULD_NOT_SAVE = "Could not save to database... try again and please report it to the developer!";
    private static final String MESSAGE_COULD_NOT_UPDATE = "Could not update the database... try again and please report it to the developer!";
    private static final String MESSAGE_COULD_NOT_OPEN = "Could not open database... try again and please report it to the developer!";
    private static final int DATA_TYPE_RECEIPT = 0;
    private static final int DATA_TYPE_SETTING = 1;

    private Context context;

    public Communicator(Context context)
    {
        this.context = context;
    }

    public boolean saveReceipt(Receipt receipt)
    {
        if(receipt.getId() > 0)
        {
            return updateReceipt(receipt);
        }
        else
        {
            return saveData(DATA_TYPE_RECEIPT, receipt); 
        }
        
    }

    public void saveSetting(Setting setting)
    {
        saveData(DATA_TYPE_SETTING, setting);
    }

    // TODO Refactor into separete saves
    public boolean saveData(int type, Object data)
    {
        boolean result = false;
        DbAdapter dbAdapter = new DbAdapter(context);
        try
        {
            dbAdapter.open();
            switch (type)
            {
                case DATA_TYPE_SETTING:
                    Setting setting = (Setting) data;
                    result = dbAdapter.updateSetting(setting.getName(), setting.getValue());
                    break;
                case DATA_TYPE_RECEIPT:
                    Receipt receipt = (Receipt) data;
                    result = dbAdapter.createReceipt(receipt.getName(), receipt.getPhoto(), receipt.getDate(),
                            receipt.getTime(), receipt.getLocationLat(), receipt.getLocationLong(), receipt.getSum(),
                            receipt.getTax(), receipt.getComment());
                    break;
            }
            if (result)
            {
                showToast("Data was saved to database!");
            } 
            else
            {
                showToast(MESSAGE_COULD_NOT_SAVE);
            }
            dbAdapter.close();
        } 
        catch (SQLException e)
        {
            Log.d("ReceiptTracker", e.getMessage());
            showToast(MESSAGE_COULD_NOT_OPEN);
        }
        return result;
    }

    public ArrayList<Receipt> getAllReceipts()
    {
        Receipt receipt;
        ArrayList<Receipt> receiptList = null;
        DbAdapter dbAdapter = new DbAdapter(context);

        try
        {
            dbAdapter.open();
            Cursor cursor = dbAdapter.fetchAllReceipts();
            receiptList = new ArrayList<Receipt>();
            if (cursor != null)
            {
                while (!cursor.isAfterLast())
                {
                    receipt = new Receipt(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID)),
                            cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_NAME)), cursor.getString(cursor
                                    .getColumnIndex(DbAdapter.KEY_PHOTO)), cursor.getString(cursor
                                    .getColumnIndex(DbAdapter.KEY_DATE)), cursor.getString(cursor
                                    .getColumnIndex(DbAdapter.KEY_TIME)), cursor.getString(cursor
                                    .getColumnIndex(DbAdapter.KEY_LOCATION_LAT)), cursor.getString(cursor
                                    .getColumnIndex(DbAdapter.KEY_LOCATION_LONG)), cursor.getString(cursor
                                    .getColumnIndex(DbAdapter.KEY_SUM)), cursor.getString(cursor
                                    .getColumnIndex(DbAdapter.KEY_TAX)), cursor.getString(cursor
                                    .getColumnIndex(DbAdapter.KEY_COMMENT)));
                    receiptList.add(receipt);
                    cursor.moveToNext();
                }
            }

            dbAdapter.close();
        } catch (SQLException e)
        {
            Log.d("ReceiptTracker", e.getMessage());
            showToast(MESSAGE_COULD_NOT_OPEN);
        }
        return receiptList;
    }

    public HashMap<String, Integer> getAllSettings()
    {
        HashMap<String, Integer> settingsMap = null;
        DbAdapter dbAdapter = new DbAdapter(context);
        try
        {
            dbAdapter.open();
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

            dbAdapter.close();
        } catch (SQLException e)
        {
            Log.d("ReceiptTracker", e.getMessage());
            showToast(MESSAGE_COULD_NOT_OPEN);
        }
        return settingsMap;
    }

    public int getSettingValue(String name)
    {
        int value = -1;
        Cursor cursor = null;
        DbAdapter dbAdapter = new DbAdapter(context);

        try
        {
            dbAdapter.open();
            cursor = dbAdapter.fetchSetting(name);
            if (cursor != null)
            {
                value = cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SETTING_VALUE));
            }

            dbAdapter.close();
        } catch (SQLException e)
        {
            Log.d("ReceiptTracker", e.getMessage());
            showToast(MESSAGE_COULD_NOT_OPEN);
        }
        return value;
    }

    public Receipt getLatestReceipt()
    {
        Receipt receipt = null;
        Cursor cursor = null;
        DbAdapter dbAdapter = new DbAdapter(context);

        try
        {
            dbAdapter.open();
            cursor = dbAdapter.fetchLastReceipt();
            if (cursor != null)
            {
                receipt = new Receipt(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ROWID)),
                        cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_NAME)), cursor.getString(cursor
                                .getColumnIndex(DbAdapter.KEY_PHOTO)), cursor.getString(cursor
                                .getColumnIndex(DbAdapter.KEY_DATE)), cursor.getString(cursor
                                .getColumnIndex(DbAdapter.KEY_TIME)), cursor.getString(cursor
                                .getColumnIndex(DbAdapter.KEY_LOCATION_LAT)), cursor.getString(cursor
                                .getColumnIndex(DbAdapter.KEY_LOCATION_LONG)), cursor.getString(cursor
                                .getColumnIndex(DbAdapter.KEY_SUM)), cursor.getString(cursor
                                .getColumnIndex(DbAdapter.KEY_TAX)), cursor.getString(cursor
                                .getColumnIndex(DbAdapter.KEY_COMMENT)));
            }

            dbAdapter.close();
        } 
        catch (SQLException e)
        {
            Log.d("ReceiptTracker", e.getMessage());
            showToast(MESSAGE_COULD_NOT_OPEN);
        }
        return receipt;
    }

    public boolean updateReceipt(Receipt receipt)
    {
        DbAdapter dbAdapter = new DbAdapter(context);
        boolean result = false;
        try
        {
            dbAdapter.open();
            result = dbAdapter.updateReceipt(receipt.getId(), receipt.getName(), receipt.getPhoto(), receipt.getDate(),
                    receipt.getTime(), receipt.getLocationLat(), receipt.getLocationLong(), receipt.getSum(),
                    receipt.getTax(), receipt.getComment());
            if(!result)
            {
                showToast(MESSAGE_COULD_NOT_UPDATE);
            }
            dbAdapter.close();
        } 
        catch (SQLException e)
        {
            Log.d("ReceiptTracker", e.getMessage());
            showToast(MESSAGE_COULD_NOT_OPEN);
        }
        return result;
    }

    public void showToast(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

}
