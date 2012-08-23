package net.danielkvist.util;

import java.lang.ref.WeakReference;

import net.danielkvist.receipttracker.R;
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

/**
 * This Singleton utility class uses an AsyncTask to load bitmaps in the background. It also provides a default Bitmap
 * that is being displayed in the ImageView that is to be populated while the real Bitmap is loading. The class also
 * keeps its own memory cache with the help of LruCache.
 * 
 * @author Daniel Kvist
 * 
 */
public class BitmapLoader
{

    private static final int THUMBNAIL_SIZE_DP = 60;
    private static LruCache<String, Bitmap> memoryCache;
    private static BitmapLoader instance;
    private int thumbnailSize;
    private Context context;
    private Bitmap defaultBitmap;

    /**
     * Construcor that takes a context as a parameter to be able to calculate how big the memory cache should be. It
     * also decodes the default bitmap.
     * 
     * @param context
     */
    private BitmapLoader(Context context)
    {
        this.context = context;

        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;
        final float scale = context.getResources().getDisplayMetrics().density;

        thumbnailSize = (int) (THUMBNAIL_SIZE_DP * scale + 0.5f);
        memoryCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                return bitmap.getByteCount();
            }
        };

        defaultBitmap = decodeSampledBitmapFromResource(context.getResources(), R.drawable.ic_launcher, thumbnailSize, thumbnailSize);
    }

    /**
     * This returns the current instance of the class if there is one, if not it creates a new instance.
     * 
     * @param context
     *            the application context
     * @return an instance of the class
     */
    public static BitmapLoader getInstance(Context context)
    {
        if (instance == null)
        {
            return new BitmapLoader(context);
        }
        else
        {
            return instance;
        }
    }

    /**
     * Called to load Bitmap into an ImageView.
     * 
     * @param imageView
     *            the ImageView to populate
     * @param path
     *            the path to the Bitmap
     */
    public void loadBitmap(ImageView imageView, String path)
    {
        final Bitmap bitmap = getBitmapFromMemCache(path);
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        }
        else if (path.equals(""))
        {
            imageView.setImageBitmap(defaultBitmap);
        }
        else
        {
            if (cancelPotentialWork(imageView, path))
            {
                ScaleBitmapFileTask worker = new ScaleBitmapFileTask(imageView, path);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), defaultBitmap, worker);
                imageView.setImageDrawable(asyncDrawable);
                worker.execute(thumbnailSize, thumbnailSize);
            }
        }
    }

    /**
     * Calculates the scaling factors so that the scaling of the Bitmap is done properly without distoring the image.
     * 
     * @param options
     *            the options
     * @param reqWidth
     *            the new width
     * @param reqHeight
     *            the new height
     * @return
     */
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

    /**
     * Decodes the Bitmap which the path is pointing with the dimensions provided.
     * 
     * @param pathToBitmap
     *            the path to the Bitmap
     * @param newHeight
     *            the requested new height
     * @param newWidth
     *            the request new width
     * @return the decoded Bitmap
     */
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

    /**
     * Decodes the resrouce which the resource id is pointing with the dimensions provided.
     * 
     * @param res
     *            the application resources
     * @param resId
     *            the resource id
     * @param newHeight
     *            the requested new height
     * @param newWidth
     *            the request new width
     * @return the decoded Bitmap
     */
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

    /**
     * Adds the current Bitmap to memory cache
     * 
     * @param key
     *            the key to store the Bitmap with
     * @param bitmap
     *            the actual Bitmap
     */
    private static void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null)
        {
            memoryCache.put(key, bitmap);
        }
    }

    /**
     * Returns the Bitmap located by the key.
     * 
     * @param key
     *            the key of the Bitmap
     * @return the Bitmap or null
     */
    private static Bitmap getBitmapFromMemCache(String key)
    {
        return memoryCache.get(key);
    }

    /**
     * Helper method to make sure we are not processing double work when loading a Bitmap.
     * 
     * @param imageView
     *            the ImageView which we are loading into
     * @param path
     *            the path to the image we're decoding
     * @return true if we are already processing the data, false if not
     */
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

    /**
     * Each ImageView is associated with an instance of the AsyncTask that is loading its Bitmap. This method returns a
     * reference to the AsyncTask that's being used.
     * 
     * @param imageView
     *            the ImageView who is related to the task we want
     * @return the AsyncTask if it exists or null
     */
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

    /**
     * An inner helper class that is needed to add a relation between the AsyncTask processing the Bitmap and ImageView
     * and the ImageView itself. This is accomplished by making each Bitmap into a AsyncDrawable which keeps a
     * WeakReference to the task. It has to be weak to avoid memory leaks.
     * 
     */
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

    /**
     * This is the AsyncTask that does the heavy lifting when it comes to loading and decoding the Bitmap into the
     * ImageView. It takes a reference to the ImageView it will populate together with a path to the Bitmap resource.
     */
    private static class ScaleBitmapFileTask extends AsyncTask
    {

        private final WeakReference imageViewReference;
        public String pathToBitmap;

        /**
         * Constructor which takes a reference to the ImageView it will populate together with a path to the Bitmap
         * resource. It creates a WeakReference to the ImageView. The WeakReference makes sure that the ImageView can be
         * garbage collected since there is a possibility that the ImageView will be gone when the task finishes.
         * 
         * @param imageView
         *            the ImageView to populate
         * @param pathToBitmap
         *            the path to the bitmap resource
         */
        public ScaleBitmapFileTask(ImageView imageView, String pathToBitmap)
        {
            imageViewReference = new WeakReference(imageView);
            this.pathToBitmap = pathToBitmap;
        }

        /**
         * When the task is done we do some checking to make sure we have a proper Bitmap, ImageView and that the task
         * that is related to the ImageView is this current task.
         */
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

        /**
         * This is the background work we want to do where we set the new height and width as params[0] and params[1]
         * and then decode the bitmap and add it to the cache.
         */
        @Override
        protected Object doInBackground(Object... params)
        {
            int newHeight = (Integer) params[0];
            int newWidth = (Integer) params[1];
            Bitmap bitmap = BitmapLoader.decodeSampledBitmap(pathToBitmap, newHeight, newWidth);
            if (bitmap != null)
            {
                addBitmapToMemoryCache(pathToBitmap, bitmap);
            }
            return bitmap;
        }

    }
}
