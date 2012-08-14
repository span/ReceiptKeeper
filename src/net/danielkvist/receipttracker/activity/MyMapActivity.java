package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.MyOverlays;
import net.danielkvist.util.Setting;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

// WORKAROUND http://code.google.com/p/android/issues/detail?id=15347 MapFragment don't exist
public class MyMapActivity extends MapActivity
{
    public static GeoPoint currentGeoPoint;
    private MapView mapView;
    private MyLocationOverlay mlo;
    private Receipt receipt;
    private GeoPoint gp;
    private LocationManager locationManager;
    private String provider;
    private MapController mapController;

    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.my_map_activity);

        ReceiptFrameActivity parent = (ReceiptFrameActivity) getParent();
        receipt = parent.getReceipt();

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapController = mapView.getController();
        mapController.setZoom(16);

        if (receipt != null)
        {
            currentGeoPoint = new GeoPoint(Integer.parseInt(receipt.getLocationLat()), Integer.parseInt(receipt.getLocationLong()));
            OverlayItem item = new OverlayItem(currentGeoPoint, "", "");
            Drawable marker = this.getResources().getDrawable(R.drawable.ic_launcher);
            MyOverlays mo = new MyOverlays(marker);
            mo.addOverlay(item);
            mapView.getOverlays().add(mo);
        }
        else
        {
            mlo = new MyLocationOverlay(this, mapView);
            mapView.getOverlays().add(mlo);
        }

        mapView.postInvalidate();
        Communicator communicator = new Communicator(this);
        mapView.setVisibility(communicator.getSettingValue(Setting.SETTING_FIELD_LOCATION));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(mlo != null)
        {
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
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(mlo != null)
        {
            mlo.disableMyLocation();
        }
    }

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
    }

    
}
