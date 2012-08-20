package net.danielkvist.receipttracker;

import net.danielkvist.util.BitmapLoader;
import android.app.Application;


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
