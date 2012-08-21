package net.danielkvist.receipttracker.fragment;

import android.app.Fragment;
import android.app.LocalActivityManager;
import android.os.Bundle;

/**
 * This is a fragment that will be used during transition from activities to fragments. Although
 * LocalActivityManager is deprecated this is being recommended at the bug tracker for AOSP. See
 * this URL for more information: http://code.google.com/p/android/issues/detail?id=15347
 */
@SuppressWarnings("deprecation")
public class LocalActivityManagerFragment extends Fragment {

    private static final String TAG = LocalActivityManagerFragment.class.getSimpleName();
    private static final String KEY_STATE_BUNDLE = "localActivityManagerState";
    
    
    private LocalActivityManager mLocalActivityManager;
    
    
    
    protected LocalActivityManager getLocalActivityManager() {
        return mLocalActivityManager;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle state = null;
        if(savedInstanceState != null) {
            state = savedInstanceState.getBundle(KEY_STATE_BUNDLE);
        }
        
        mLocalActivityManager = new LocalActivityManager(getActivity(), true);
        mLocalActivityManager.dispatchCreate(state);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_STATE_BUNDLE, mLocalActivityManager.saveInstanceState());
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mLocalActivityManager.dispatchResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mLocalActivityManager.dispatchPause(getActivity().isFinishing());
    }    
    
    @Override
    public void onStop() {
        super.onStop();
        mLocalActivityManager.dispatchStop();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalActivityManager.dispatchDestroy(getActivity().isFinishing());
    }
}
