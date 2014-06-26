package net.danielkvist.receipttracker;

import net.danielkvist.util.BitmapLoader;
import net.danielkvist.util.DropboxHandler;
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
    private DropboxHandler dropbox;
    public boolean userHasBeenPromptedAboutGPS = false;	

    /**
     * Creates the dropbox and bitmap loader instances that are used app wide
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        bitmapLoader = BitmapLoader.getInstance(this);
        dropbox = new DropboxHandler(this);
    }

    /**
     * When memory is being trimmed, clear the cache so we don't get killed.
     */
    @Override
    public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if(level >= TRIM_MEMORY_MODERATE)
        {
            bitmapLoader.clearCache();
        }
        // TODO When http://code.google.com/p/android/issues/detail?id=35349 is fixed, add check for TRIM_MEMORY_BACKGROUND and 
        // do a trimToSize(size/2) on the cache.
    }

    /**
     * Returns the app dropbox object
     * @return the dropbox api handler.
     */
    public DropboxHandler getDropbox()
	{
		return dropbox;
	}
}
