package net.danielkvist.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapScaler
{
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            if (width > height)
            {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } 
            else
            {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        
        return inSampleSize;
    }
    
    public static Bitmap decodeSampledBitmap(String pathToBitmap, int newHeight, int newWidth)
    {   
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToBitmap, options);
        
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;
        
        return BitmapFactory.decodeFile(pathToBitmap, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int newWidth, int newHeight)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;
        
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
