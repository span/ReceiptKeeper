package net.danielkvist.util;

import java.util.ArrayList;

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

    private static final int DATA_TYPE_RECEIPT = 0;
    private static final int DATA_TYPE_SETTING = 1;
    
    private Context context;

    public Communicator(Context context)
    {
        this.context = context;
    }
    
    public boolean saveReceipt(Receipt receipt)
    {
        return saveData(DATA_TYPE_RECEIPT, receipt);
    }
    
    public void saveSetting(Setting setting)
    {
        saveData(DATA_TYPE_SETTING, setting);
    }
    
    public boolean saveData(int type, Object data)
    {
        boolean result = false;
        DbAdapter dbAdapter = new DbAdapter(context);
        try
        {
            dbAdapter.open();
            switch(type)
            {
                case DATA_TYPE_SETTING:
                    Setting setting = (Setting) data;
                    result = dbAdapter.updateSetting(setting.getName(), setting.getValue());
                    break;
                case DATA_TYPE_RECEIPT:
                    Receipt receipt = (Receipt) data;
                    result = dbAdapter.createReceipt(receipt.getName(), receipt.getPhoto(), receipt.getDate(), receipt.getTime(), 
                            receipt.getLocationLat(), receipt.getLocationLong(), receipt.getSum(), receipt.getTax(), receipt.getComment());
                    break;
            }
            if(result)
            {
                Toast.makeText(context, "Data was saved to database!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context, "Could not save to database... try again and please report it to the developer!", Toast.LENGTH_LONG).show();
            }
        }
        catch (SQLException e) 
        {
            Log.d("ReceiptTracker", e.getMessage());
            Toast.makeText(context, "Could not open database... try again and please report it to the developer!", Toast.LENGTH_LONG).show();
        }
        return result;
    }
    
    public ArrayList<Receipt> getAllReceipts()
    {
        // TODO Get receipts from db and return list
        ArrayList<Receipt> receiptList = null;
        DbAdapter dbAdapter = new DbAdapter(context);
        try
        {
            dbAdapter.open();
            Cursor c = dbAdapter.fetchAllReceipts();
            
            receiptList = new ArrayList<Receipt>();
            
        }
        catch (SQLException e) 
        {
            Log.d("ReceiptTracker", e.getMessage());
            Toast.makeText(context, "Could not open database... try again and please report it to the developer!", Toast.LENGTH_LONG).show();
        }
        return receiptList;
    }
    
    public ArrayList<Setting> getAllSettings()
    {
        // TODO Get settings from db and return list
        return null;
    }
    
    

}
