package net.danielkvist.util;

import java.lang.ref.WeakReference;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

public class BitmapLoader
{

    private static LruCache<String, Bitmap> memoryCache;
    private static BitmapLoader instance;

    private BitmapLoader(Context context)
    {
        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                return bitmap.getByteCount();
            }
        };
    }
    
    public static BitmapLoader getInstance(Context context)
    {
        if(instance == null)
        {
            return new BitmapLoader(context);
        }
        else 
        {
            return instance;
        }
    }
    
    public void loadBitmap(ImageView imageView, String path)
    {
        // FIXME Try to delete image and see what happends when trying to load when file not found or empty string is passed as path (choose default)
        // FIXME Streamline image sizes so all bitmaps can use same cache copy
        // FIXME Use standard icon in list (pass into the asyncdrawable)
        final Bitmap bitmap = getBitmapFromMemCache(path);
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            if (cancelPotentialWork(imageView, path))
            {
                ScaleBitmapFileTask worker = new ScaleBitmapFileTask(imageView, path);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(null, null, worker);
                imageView.setImageDrawable(asyncDrawable);
                worker.execute(75, 75);
            }
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
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

    private static Bitmap decodeSampledBitmap(String pathToBitmap, int newHeight, int newWidth)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToBitmap, options);

        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(pathToBitmap, options);
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int newWidth, int newHeight)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null)
        {
            memoryCache.put(key, bitmap);
        }
    }

    private static Bitmap getBitmapFromMemCache(String key)
    {
        return memoryCache.get(key);
    }

    private static boolean cancelPotentialWork(ImageView imageView, String path)
    {
        final ScaleBitmapFileTask bitmapWorkerTask = getWorkerTask(imageView);

        if (bitmapWorkerTask != null)
        {
            if (!path.equals(bitmapWorkerTask.pathToBitmap))
            {
                bitmapWorkerTask.cancel(true);
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    private static ScaleBitmapFileTask getWorkerTask(ImageView imageView)
    {
        if (imageView != null)
        {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable)
            {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private static class AsyncDrawable extends BitmapDrawable
    {
        private final WeakReference workerReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, ScaleBitmapFileTask worker)
        {
            super(res, bitmap);
            workerReference = new WeakReference(worker);
        }

        public ScaleBitmapFileTask getBitmapWorkerTask()
        {
            return (ScaleBitmapFileTask) workerReference.get();
        }
    }

    private static class ScaleBitmapFileTask extends AsyncTask
    {

        private final WeakReference imageViewReference;
        public String pathToBitmap;

        public ScaleBitmapFileTask(ImageView imageView, String pathToBitmap)
        {
            imageViewReference = new WeakReference(imageView);
            this.pathToBitmap = pathToBitmap;
        }

        @Override
        public void onPostExecute(Object obj)
        {
            Bitmap bitmap = (Bitmap) obj;
            if (isCancelled())
            {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null)
            {
                final ImageView imageView = (ImageView) imageViewReference.get();
                final ScaleBitmapFileTask worker = getWorkerTask(imageView);

                if (this == worker && imageView != null)
                {
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        protected Object doInBackground(Object... params)
        {
            int newHeight = (Integer) params[0];
            int newWidth = (Integer) params[1];
            Bitmap bitmap = BitmapLoader.decodeSampledBitmap(pathToBitmap, newHeight, newWidth);
            addBitmapToMemoryCache(pathToBitmap, bitmap); 
            return bitmap;
        }

    }
}
