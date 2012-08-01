package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MyMapActivity extends MapActivity
{
    public static GeoPoint currentGeoPoint;
    private MapView mapView;
    private MyLocationOverlay mlo;

    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.my_map_activity);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        MapController mapController = mapView.getController();
        mapController.setZoom(18);

        mlo = new MyLocationOverlay(this, mapView);
        mlo.enableMyLocation();

        mapView.getOverlays().add(mlo);
        mapView.postInvalidate();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mlo.enableMyLocation();
        mlo.runOnFirstFix(new Runnable()
        {
            public void run()
            {
                currentGeoPoint = mlo.getMyLocation();
                mapView.getController().animateTo(currentGeoPoint);
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mlo.disableMyLocation();
    }

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
    }
}
