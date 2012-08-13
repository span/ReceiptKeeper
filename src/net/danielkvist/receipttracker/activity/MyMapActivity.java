package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
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
        // FIXME Only use location from receipt, not my location
        super.onCreate(icicle);
        setContentView(R.layout.my_map_activity);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        MapController mapController = mapView.getController();
        mapController.setZoom(16);

        mlo = new MyLocationOverlay(this, mapView);

        mapView.getOverlays().add(mlo);
        mapView.postInvalidate();
        
        Communicator communicator = new Communicator(this);
        mapView.setVisibility(communicator.getSettingValue(Setting.SETTING_FIELD_LOCATION));
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
