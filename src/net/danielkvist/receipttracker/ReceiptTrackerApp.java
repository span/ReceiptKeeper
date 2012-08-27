package net.danielkvist.receipttracker;

import net.danielkvist.util.BitmapLoader;
import android.app.Application;

/**
 * This is the main Application wrapper for the app. This class handles some Singleton instances that need to be
 * globally available such as the BitmapLoader and it's cache.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptTrackerApp extends Application
{
    public static final int RECEIPT_ADD_FRAGMENT_ID = 1;
    public static final int RECEIPT_SEARCH_FRAGMENT_ID = 2;
    public static final int RECEIPT_RESULTS_FRAGMENT_ID = 3;
    public static final int RECEIPT_DETAIL_FRAGMENT_ID = 4;
    public static final int RECEIPT_SETTINGS_FRAGMENT_ID = 5;
    public static final String ARG_ITEM_ID = "item_id";
    public BitmapLoader bitmapLoader;

    @Override
    public void onCreate()
    {
        super.onCreate();
        bitmapLoader = BitmapLoader.getInstance(this);
    }
    
    public String translateString(String resourceName)
    {
    	return getResources().getString(getResources().getIdentifier(resourceName, "string", "net.danielkvist.receipttracker"));
    }

}
