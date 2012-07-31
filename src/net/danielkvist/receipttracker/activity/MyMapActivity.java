package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import android.os.Bundle;
import com.google.android.maps.MapActivity;

public class MyMapActivity extends MapActivity {
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.my_map_activity);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
