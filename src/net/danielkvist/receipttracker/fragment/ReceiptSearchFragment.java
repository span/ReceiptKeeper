package net.danielkvist.receipttracker.fragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.ReceiptTrackerApp;
import net.danielkvist.receipttracker.adapter.ReceiptSearchAdapter;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.BitmapLoader;
import net.danielkvist.util.Communicator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * This Fragment handles the search mechanism in the application. It is also responsible to adding date filters to allow
 * a search to be limited to specific dates and/or select all receipts within a specific date interval. It extends the
 * CustomListFragment so that it has Callbacks interfaces for communicating with the parent activity.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptSearchFragment extends CustomListFragment implements OnDateSetListener, View.OnClickListener,
        DialogInterface.OnClickListener
{
    private static final int TIME_NOT_SET = -1;
    private static final int TIME_FROM = 0;
    private static final int TIME_TO = 1;
    public static final int ID = 2;
    private int timeToSet = TIME_NOT_SET;
    private int containerHeight;
    private ArrayList<Receipt> receiptList;
    private Communicator communicator;
    private String searchQuery = "";
    private TextView dateFromView;
    private TextView dateToView;
    private Button searchButton;
    private SearchView searchView;
    private long timeFrom = 0;
    private long timeTo = System.currentTimeMillis();
    private ImageButton timeStampFromButton;
    private ImageButton timeStampToButton;
    private LinearLayout filterContainer;
    private TextView filterHeader;
    private BitmapLoader bitmapLoader;
    private ReceiptSearchAdapter adapter;

    /**
     * This listener listens for changes in the text in the search box and also for the submission of a query.
     */
    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
    {
        @Override
        public boolean onQueryTextChange(String newText)
        {
            searchQuery = newText;
            adapter.getFilter().filter(newText);
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query)
        {
            searchQuery = query;
            adapter.getFilter().filter(query);
            return true;
        }
    };

    /**
     * Populates the search list with a default selection and adds the possibility on hooking into the options menu.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        communicator = new Communicator(getActivity());
        bitmapLoader = ((ReceiptTrackerApp) getActivity().getApplication()).bitmapLoader;
        populateList();
        setHasOptionsMenu(true);
    }

    /**
     * Adds a globalLayoutListener so that we can measure the height of the hidden filter container. This uses a
     * deprecated method removeGlobalOnLayoutListener because the new version removeOnGlobalLayoutListener is only
     * available from API 15.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        final ViewTreeObserver vto = filterContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                // Need to measure the height of the filter container to be able to animate it
                containerHeight = filterContainer.getHeight();
                filterContainer.setVisibility(View.GONE);
                filterContainer.getLayoutParams().height = 0;
                filterContainer.requestLayout();
                ViewTreeObserver obs = filterContainer.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * Sets up the View components that are used in the UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_receipt_search, container, false);

        dateFromView = (TextView) rootView.findViewById(R.id.date_from);
        dateFromView.setOnClickListener(this);

        dateToView = (TextView) rootView.findViewById(R.id.date_to);
        dateToView.setOnClickListener(this);

        timeStampFromButton = (ImageButton) rootView.findViewById(R.id.timestamp_from_button);
        timeStampFromButton.setOnClickListener(this);

        timeStampToButton = (ImageButton) rootView.findViewById(R.id.timestamp_to_button);
        timeStampToButton.setOnClickListener(this);

        searchButton = (Button) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        filterHeader = (TextView) rootView.findViewById(R.id.filter_header);
        filterHeader.setOnClickListener(this);
        filterContainer = (LinearLayout) rootView.findViewById(R.id.filter_container);

        return rootView;
    }

    /**
     * Helper method to show the DatePickerFragment. Using a temporary listener until bug has been fixed.
     */
    private void showDateDialog()
    {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        // datePickerFragment.setCallback(this);
        datePickerFragment.setAcceptDateListener(this);
        datePickerFragment.show(getFragmentManager(), null);
    }

    /**
     * Hooks into the options menu and sets the search item to visible and adds some listeners to it.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setVisible(true);

        searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(queryTextListener);

        super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handles a menu item selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_search:
                getActivity().onSearchRequested();
                return true;
            default:
                return false;
        }

    }

    /**
     * Click listener for the list that calls back to the parent Activity to show a detail view of the selected Receipt.
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        callbacks.onItemSelected(receiptList.get(position));
    }

    // WORKAROUND this listener is not currently being used because of system bug detailed in DatePickerFragment
    /**
     * Implements the onDateSet listener that is currently not being used due to system bug. Instead we are now using
     * onClick to handle the date selection. This is currently being called from DialogInterface.onClick.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();

        if (timeToSet == TIME_FROM)
        {
            calendar.set(year, month, day, 0, 0, 0);
            Date date = calendar.getTime();
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
            timeFrom = date.getTime();
            dateFromView.setText(dateFormat.format(date));
        }
        else
        {
            calendar.set(year, month, day, 23, 59, 59);
            Date date = calendar.getTime();
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
            timeTo = date.getTime();
            dateToView.setText(dateFormat.format(date));
        }
    }

    /**
     * Implements the temporary onClick listener to handle the clicks from the accept button in the DatePickerFragment.
     * Sets the date on the Receipt and in the corresponding TextView.
     */
    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        DatePicker picker = ((DatePickerDialog) dialog).getDatePicker();
        onDateSet(picker, picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
    }

    /**
     * Updates and populates the list and the data that is to be shown via the adapter.
     */
    private void populateList()
    {
        if (searchQuery.equals("") && timeToSet == TIME_NOT_SET)
        {
            receiptList = communicator.getReceipts(20);
        }
        else
        {
            receiptList = communicator.getReceipts(timeFrom, timeTo);
        }
        adapter = new ReceiptSearchAdapter(getActivity(), R.layout.row, receiptList, bitmapLoader);
        if (!searchQuery.equals(""))
        {
            adapter.getFilter().filter(searchQuery);
        }
        setListAdapter(adapter);
    }

    /**
     * Click handler for date selection and filtering
     */
    @Override
    public void onClick(View v)
    {
        if (v.getId() == dateFromView.getId() || v.getId() == timeStampFromButton.getId())
        {
            timeToSet = TIME_FROM;
            showDateDialog();
        }
        else if (v.getId() == dateToView.getId() || v.getId() == timeStampToButton.getId())
        {
            timeToSet = TIME_TO;
            showDateDialog();
        }
        else if (v.getId() == searchButton.getId())
        {
            populateList();
        }
        else if (v.getId() == filterHeader.getId())
        {
            if (filterContainer.getVisibility() == View.VISIBLE)
            {
                filterHeader.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_input_add, 0, 0, 0);
                dateFromView.setText(getString(R.string.select_from_date));
                dateToView.setText(getString(R.string.select_to_date));
                timeFrom = 0;
                timeTo = System.currentTimeMillis();
                if (timeToSet != TIME_NOT_SET)
                {
                    timeToSet = TIME_NOT_SET;
                    populateList();
                }
            }
            else
            {
                filterHeader.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_close_clear_cancel, 0, 0, 0);
            }
            toggleFilters(filterContainer);

        }

    }

    /**
     * Helper method to toggle the visibility of the filter container, uses a ValueAnimator to animate the height.
     * 
     * @param v
     *            the view (LinearLayout) to animate
     */
    private void toggleFilters(final LinearLayout v)
    {
        ValueAnimator va = ValueAnimator.ofInt(0, containerHeight);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            public void onAnimationUpdate(ValueAnimator animation)
            {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().height = value.intValue();
                v.requestLayout();
            }
        });

        if (v.getVisibility() == View.GONE)
        {
            v.setVisibility(View.VISIBLE);
            va.start();

        }
        else
        {
            va.reverse();
            va.addListener(new AnimatorListenerAdapter()
            {
                public void onAnimationEnd(Animator animation)
                {
                    v.setVisibility(View.GONE);
                }
            });
        }
    }

}
