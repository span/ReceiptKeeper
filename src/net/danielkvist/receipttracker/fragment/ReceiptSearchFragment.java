package net.danielkvist.receipttracker.fragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.task.ScaleBitmapFileTask;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class ReceiptSearchFragment extends CustomListFragment implements OnDateSetListener, View.OnClickListener
{
    private static final int TIME_NOT_SET = -1;
    private static final int TIME_FROM = 0;
    private static final int TIME_TO = 1;
    private int timeToSet = TIME_NOT_SET;
    private ArrayList<Receipt> receiptList;
    private Communicator communicator;
    private String searchQuery = "";
    private TextView dateFromView;
    private TextView dateToView;
    private Button searchButton;
    private SearchView searchView;
    private long timeFrom = 0;
    private long timeTo = System.currentTimeMillis();

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
    {
        @Override
        public boolean onQueryTextChange(String newText)
        {
            // TODO Try to implement auto update of the list when typing
            searchQuery = newText;
            if(!newText.equals(""))
            {
                populateList();
            }
            
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query)
        {
            searchQuery = query;
            populateList();
            return true;
        }
    };
    private ImageButton timeStampFromButton;
    private ImageButton timeStampToButton;
    private LinearLayout filterContainer;
    private TextView filterHeader;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        communicator = new Communicator(getActivity());
        populateList();
        setHasOptionsMenu(true);
    }

    private void filterList()
    {
        Iterator<Receipt> iterator = receiptList.iterator();
        while (iterator.hasNext())
        {
            Receipt r = iterator.next();
            long timestamp = r.getTimestamp();
            if (timestamp < timeFrom || timestamp > timeTo)
            {
                iterator.remove();
            }
        }
    }

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

    private void showDateDialog()
    {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setCallback(this);
        datePickerFragment.show(getFragmentManager(), null);
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_search:
                // FIXME Make sure that search works on small screen where
                // search is in the 3-dot menu
                getActivity().onSearchRequested();
                return true;
            default:
                return false;
        }

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(receiptList.get(position));
    }

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

    private void populateList()
    {
        if (searchQuery.equals("") && timeToSet == TIME_NOT_SET)
        {
            receiptList = communicator.getReceipts(10);
        }
        else if(searchQuery.equals(""))
        {
            receiptList = communicator.fetchReceipts(timeFrom, timeTo);
        }
        else if(timeToSet == TIME_NOT_SET)
        {
            receiptList = communicator.searchReceipts(searchQuery);
        }
        else
        {
            receiptList = communicator.searchReceipts(searchQuery);
            filterList();
        }
        setListAdapter(new ReceiptAdapter(getActivity(), R.layout.row, receiptList));
    }

    private class ReceiptAdapter extends ArrayAdapter<Receipt>
    {

        private ArrayList<Receipt> items;
        private Context context;

        public ReceiptAdapter(Context context, int textViewResourceId, ArrayList<Receipt> items)
        {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.row, null);
            }
            Receipt r = items.get(position);
            if (r != null)
            {
                TextView name = (TextView) view.findViewById(R.id.row_name);
                TextView timestamp = (TextView) view.findViewById(R.id.row_timestamp);
                ImageView image = (ImageView) view.findViewById(R.id.row_image);
                name.setText(r.getName());
                timestamp.setText(r.getDate(context));
                new ScaleBitmapFileTask(image, r.getPhoto()).execute(75, 75);

            }
            return view;
        }
    }

    @Override
    public void onClick(View v)
    {
        
        if(v.getId() == dateFromView.getId())
        {
            timeToSet = TIME_FROM;
            showDateDialog();
        }
        else if(v.getId() == dateToView.getId())
        {
            timeToSet = TIME_TO;
            showDateDialog();
        }
        else if(v.getId() == timeStampFromButton.getId())
        {
            timeToSet = TIME_FROM;
            showDateDialog();
        }
        else if(v.getId() == timeStampToButton.getId())
        {
            timeToSet = TIME_TO;
            showDateDialog();
        }
        else if(v.getId() == searchButton.getId())
        {
            populateList();
        }
        else if(v.getId() == filterHeader.getId())
        {
            if(filterContainer.getVisibility() == View.VISIBLE)
            {
                filterContainer.setVisibility(View.GONE);
                filterHeader.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_input_add, 0, 0, 0);
                timeToSet = TIME_NOT_SET;
                timeFrom = 0;
                timeTo = System.currentTimeMillis();
                dateFromView.setText(getString(R.string.select_from_date));
                dateToView.setText(getString(R.string.select_to_date));
                populateList();
            }
            else
            {
                filterContainer.setVisibility(View.VISIBLE);
                filterHeader.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_close_clear_cancel, 0, 0, 0);
            }
            
        }
        
    }

}
