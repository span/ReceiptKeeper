package net.danielkvist.receipttracker.activity;


import net.danielkvist.receipttracker.fragment.ActivityHostFragment;
import android.app.Activity;

public class MyMapFragmentActivity extends ActivityHostFragment {
    
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return MyMapActivity.class;
    }
}
