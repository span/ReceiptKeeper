package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.MyOverlays;
import net.danielkvist.util.Setting;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

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
        if (mlo != null)
        {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                showAlertMessageGps();
            }
            mlo.enableMyLocation();
            mlo.runOnFirstFix(new Runnable()
            {
                public void run()
                {
                    currentGeoPoint = mlo.getMyLocation();
                    mapView.getController().animateTo(currentGeoPoint);
                }
            });
            if (currentGeoPoint == null)
            {
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null)
                {
                    currentGeoPoint = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
                }
                else
                {
                    currentGeoPoint = new GeoPoint((int) (0 * 1E6), (int) (0 * 1E6));
                }
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mlo != null)
        {
            mlo.disableMyLocation();
        }
    }

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
    }

    private void showAlertMessageGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_gps_message).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(final DialogInterface dialog, final int id)
                    {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick(final DialogInterface dialog,final int id)
                    {
                        dialog.cancel();
                    }
                }).setTitle(R.string.enable_gps_title);
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
