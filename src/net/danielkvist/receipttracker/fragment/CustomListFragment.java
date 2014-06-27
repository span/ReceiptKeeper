package net.danielkvist.receipttracker.fragment;

import net.danielkvist.receipttracker.content.Receipt;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ListView;

/**
 * This is a base class for any ListFragments that are used in the application.
 * It handles basic things like repositioning the list, callbacks and sets the
 * item checked or not. It has a custom Interface that the Activities that holds
 * the fragments can use to communicate with the fragment.
 * 
 * @author Daniel Kvist
 * 
 */
public class CustomListFragment extends ListFragment {
	protected static final String STATE_ACTIVATED_POSITION = "activated_position";
	protected Callbacks callbacks = dummyCallbacks;
	protected int activatedPosition = ListView.INVALID_POSITION;

	/**
	 * The custom interface that allows callbacks for id's and receipts
	 */
	public interface Callbacks {
		public void onItemSelected(String id);

		public void onItemSelected(Receipt receipt);
	}

	private static Callbacks dummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}

		@Override
		public void onItemSelected(Receipt receipt) {
		}
	};

	/**
	 * We want to retain the instance of the fragment so we set the
	 * setRetainInstance to true
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	};

	/**
	 * When the fragment is being attached we can save a reference to the
	 * activity that we use for the callback methods.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		callbacks = (Callbacks) activity;
	}

	/**
	 * Make sure to set the main activity back to some dummu callbacks since we
	 * don't need them any more when we're being detached.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = dummyCallbacks;
	}

	/**
	 * Make sure to save the position of the ListView when we're being
	 * deactivated
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (activatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
		}
	}

	/**
	 * Set the type of ListView we want, ie if we want to be able to select
	 * multiple items or not
	 * 
	 * @param activateOnItemClick
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	/**
	 * Resets the actual position of the List when we're being resumed and
	 * reattached to the activity
	 * 
	 * @param position
	 */
	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(activatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		activatedPosition = position;
	}
}
