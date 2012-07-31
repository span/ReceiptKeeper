package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import android.os.Bundle;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MyMapActivity extends MapActivity {
    
    private MapView mapView;
    private MyLocationOverlay mlo;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.my_map_activity);
        
        mapView = (MapView) findViewById(R.id.mapview);
        
        mlo = new MyLocationOverlay(this, mapView);
        mlo.enableMyLocation();
        
        mapView.getOverlays().add(mlo);
        mapView.postInvalidate();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mlo.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mlo.disableMyLocation();
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
