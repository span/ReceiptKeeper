package net.danielkvist.receipttracker.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.MapFragment;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.ReceiptTrackerApp;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.CustomListFragment;
import net.danielkvist.receipttracker.fragment.ReceiptAddFragment;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptResultsFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSearchFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSettingsFragment;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.DropboxHandler;
import net.danielkvist.util.LocationUtils;
import net.danielkvist.util.Setting;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;

/**
 * This is a wrapper activity for the different Fragments that build up the
 * application. This activity is responsible for showing the right fragment
 * according to what action was taken before the activity was launched. The
 * class also implements some custom callbacks to make it possible for the
 * fragments to communicate with the activity.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptFrameActivity extends Activity implements
		CustomListFragment.Callbacks, ReceiptDetailFragment.Callbacks,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

	private static final String TAG = ReceiptFrameActivity.class.getSimpleName();
	private static final int RECEIPT_FRAME_CONTAINER = R.id.receipt_frame_container;
	private Fragment fragment = null;
	private Receipt currentReceipt;
	public ShareActionProvider shareActionProvider;
	private DropboxHandler dropbox;
	private LocationClient locationClient;
	private Location location;

	/**
	 * Receives information about which fragment to show and calls
	 * replaceFragment with the information that is passed in. Also enables UP
	 * navigation.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receipt_frame);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		dropbox = ((ReceiptTrackerApp) getApplication()).getDropbox();

		if (savedInstanceState == null) {
			Receipt r = (Receipt) getIntent().getParcelableExtra(
					Receipt.EXTRA_RECEIPT);
			int fid = getIntent().getIntExtra(ReceiptTrackerApp.ARG_ITEM_ID,
					ReceiptTrackerApp.RECEIPT_DETAIL_FRAGMENT_ID);
			replaceFragment(fid, r, false);
		}

		Communicator communicator = new Communicator(this);
		boolean enabled = communicator.getSettingValue(Setting.SETTING_FIELD_LOCATION) == View.VISIBLE;
		if(enabled) {
			locationClient = new LocationClient(this, this, this);
		}
	}

	/*
     * Called when the Activity becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();
        locationClient.connect();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    public void onStop() {
    	locationClient.disconnect();
    	super.onStop();
    }

	/**
	 * Creates the menu in the ActionBar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Handles the UP navigation when pressing the home button in the top left.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Nothing to do here at this time, needed for implementation of the
	 * Callbacks
	 */
	@Override
	public void onItemSelected(String id) {
	}

	/**
	 * Handles callback from the Fragment and replaces the fragment according to
	 * which Receipt is passed in to show the details of the receipt.
	 */
	@Override
	public void onItemSelected(Receipt receipt) {
		replaceFragment(ReceiptTrackerApp.RECEIPT_DETAIL_FRAGMENT_ID, receipt,
				true);
	}

	/**
	 * Handles callback from the Fragment and replaces the fragment according to
	 * which Receipt is passed in and shows the Add/Edit form for editing the
	 * data in the Receipt.
	 */
	@Override
	public void editSelected(Receipt receipt) {
		replaceFragment(ReceiptTrackerApp.RECEIPT_ADD_FRAGMENT_ID, receipt,
				true);
	}

	/**
	 * Replaces the current fragment with a new one.
	 * 
	 * @param newFragmentId
	 *            The ID of the fragment to show
	 * @param newReceipt
	 *            The Receipt that is passed to the fragment
	 * @param addToBackStack
	 *            If we want to add the fragment to the backStack or not
	 */
	private void replaceFragment(int newFragmentId, Receipt newReceipt,
			boolean addToBackStack) {
		Bundle arguments = new Bundle();
		arguments.putParcelable(Receipt.EXTRA_RECEIPT, newReceipt);

		FragmentTransaction ft = getFragmentManager().beginTransaction();

		currentReceipt = newReceipt;

		switch (newFragmentId) {
		case ReceiptTrackerApp.RECEIPT_ADD_FRAGMENT_ID:
			fragment = new ReceiptAddFragment();
			if (currentReceipt == null) {
				setTitle(getString(R.string.add));
				arguments.putBoolean("add", true);
			} else {
				setTitle(getString(R.string.edit));
				arguments.putBoolean("add", false);
			}
			break;
		case ReceiptTrackerApp.RECEIPT_SEARCH_FRAGMENT_ID:
			fragment = new ReceiptSearchFragment();
			setTitle(getString(R.string.search));
			break;
		case ReceiptTrackerApp.RECEIPT_RESULTS_FRAGMENT_ID:
			fragment = new ReceiptResultsFragment();
			setTitle(getString(R.string.results));
			break;
		case ReceiptTrackerApp.RECEIPT_SETTINGS_FRAGMENT_ID:
			fragment = new ReceiptSettingsFragment();
			setTitle(getString(R.string.settings));
			break;
		case ReceiptTrackerApp.RECEIPT_DETAIL_FRAGMENT_ID:
			fragment = new ReceiptDetailFragment();
			setTitle(getString(R.string.details));
			break;
		}

		if (addToBackStack) {
			ft.addToBackStack(null);
		}

		fragment.setArguments(arguments);
		ft.replace(RECEIPT_FRAME_CONTAINER, fragment);
		ft.commit();
		invalidateOptionsMenu();
	}

	/**
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:
                        Log.d(TAG, getString(R.string.resolved));
                        break;
                    // If any other result was returned by Google Play services
                    default:
                        Log.d(TAG, getString(R.string.no_resolution));
                        break;
                }
            default:
               Log.d(TAG, getString(R.string.unknown_activity_request_code, requestCode));
               break;
        }
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(TAG, getString(R.string.play_services_available));
            return true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), TAG);
            }
            return false;
        }
    }

	/**
	 * Returns the current receipt that is being displayed
	 * 
	 * @return the current receipt
	 */
	public Receipt getReceipt() {
		return currentReceipt;
	}

	/**
	 * Returns the app dropbox object
	 * 
	 * @return the dropbox object
	 */
	public DropboxHandler getDropbox() {
		return dropbox;
	}

	public Location getLocation() {
		if(servicesConnected()) {
			this.location = locationClient.getLastLocation();
			return location;
		} else {
			return null;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.w(TAG, "Could not connect to services.");

		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (result.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
            	result.startResolutionForResult(this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "SendIntentException" + e.getMessage());
            }
        } else {
            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(result.getErrorCode());
        }
	}

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), TAG);
        }
    }

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(TAG, "Connected to sevices.");
		location = locationClient.getLastLocation();
		if(fragment instanceof ReceiptAddFragment) {
			ReceiptAddFragment f = (ReceiptAddFragment) fragment;
			f.centerMap(getLocation());
		}
	}

	@Override
	public void onDisconnected() {
		Log.d(TAG, "Disconnected from sevices.");
	}

	/**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

}
