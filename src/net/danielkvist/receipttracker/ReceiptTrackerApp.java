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
    public BitmapLoader bitmapLoader;

    @Override
    public void onCreate()
    {
        super.onCreate();
        bitmapLoader = BitmapLoader.getInstance(this);
    }

}
