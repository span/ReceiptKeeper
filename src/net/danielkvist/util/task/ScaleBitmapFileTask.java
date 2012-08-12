package net.danielkvist.util.task;

import java.lang.ref.WeakReference;

import net.danielkvist.util.BitmapScaler;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class ScaleBitmapFileTask extends AsyncTask
{

    private final WeakReference imageViewReference;
    private String pathToBitmap;

    public ScaleBitmapFileTask(ImageView imageView, String pathToBitmap)
    {
        imageViewReference = new WeakReference(imageView);
        this.pathToBitmap = pathToBitmap;
    }

    @Override
    public void onPostExecute(Object obj)
    {
        Bitmap bitmap = (Bitmap) obj;
        if (imageViewReference != null && bitmap != null)
        {
            final ImageView imageView = (ImageView) imageViewReference.get();
            if (imageView != null)
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
        return BitmapScaler.decodeSampledBitmap(pathToBitmap, newHeight, newWidth);
    }

}
