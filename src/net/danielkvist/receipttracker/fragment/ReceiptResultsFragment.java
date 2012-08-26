package net.danielkvist.receipttracker.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.adapter.ReceiptAccountAdapter;
import net.danielkvist.receipttracker.adapter.ReceiptAccountCategoryAdapter;
import net.danielkvist.receipttracker.content.ReceiptAccount;
import net.danielkvist.util.Communicator;
import android.app.Fragment;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This fragment shows the grand total of income - costs and some spinners which can be used to populate text views with
 * the sum and totals of receipt accounts and receipt account categories.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptResultsFragment extends Fragment implements OnItemSelectedListener
{
	private Communicator communicator;
	private Spinner categorySpinner;
	private List<String> categoryList;
	private List<ReceiptAccount> accountList;
	private Spinner accountSpinner;
	private TextView incomeView;
	private TextView costView;
	private TextView totalView;
	private TextView categoryTotalView;
	private TextView accountTotalView;
	private HashMap<String, List<ReceiptAccount>> categoryAccountMap;
	private List<ReceiptAccount> currentAccountList;
	private ReceiptAccountAdapter receiptAccountAdapter;
	private ReceiptAccountCategoryAdapter receiptAccountCategoryAdapter;

	/**
	 * Just an empty constructor
	 */
	public ReceiptResultsFragment()
	{

	}

	/**
	 * Sets retain instance to true
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		communicator = new Communicator(getActivity());
	}

	/**
	 * Gets the categories and accounts from the database and sets up the view. Also performs initial calculations for
	 * the grand sub total.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_receipt_results, container, false);

		categoryList = new ArrayList<String>();
		categoryList.add("all");
		categoryList.addAll(communicator.getReceiptAccountCategories());

		accountList = communicator.getReceiptAccounts();
		currentAccountList = new ArrayList<ReceiptAccount>();
		currentAccountList.addAll(accountList);

		categorySpinner = (Spinner) rootView.findViewById(R.id.category_spinner);
		receiptAccountCategoryAdapter = new ReceiptAccountCategoryAdapter(getActivity(),
				android.R.layout.simple_spinner_item, categoryList);
		categorySpinner.setAdapter(receiptAccountCategoryAdapter);
		categorySpinner.setOnItemSelectedListener(this);
		categoryTotalView = (TextView) rootView.findViewById(R.id.category_result);

		accountSpinner = (Spinner) rootView.findViewById(R.id.account_spinner);
		receiptAccountAdapter = new ReceiptAccountAdapter(getActivity(), android.R.layout.simple_spinner_item,
				currentAccountList);
		accountSpinner.setAdapter(receiptAccountAdapter);
		accountSpinner.setOnItemSelectedListener(this);
		accountTotalView = (TextView) rootView.findViewById(R.id.account_result);
		// setAccountTotalView(0);

		categoryAccountMap = new HashMap<String, List<ReceiptAccount>>();
		categoryAccountMap.put("all", accountList);

		int income = getCategoryTotal("income");
		incomeView = (TextView) rootView.findViewById(R.id.income);
		incomeView.setText(String.valueOf(income));

		int costs = getCategoryTotal("costs");
		costView = (TextView) rootView.findViewById(R.id.cost);
		costView.setText(String.valueOf(costs));

		totalView = (TextView) rootView.findViewById(R.id.total);
		totalView.setText(String.valueOf(income - costs));

		return rootView;
	}

	/**
	 * Gets the sum of all receipts that has the receipt account that is passed in as a parameter
	 * 
	 * @param receiptAccount
	 *            the receiptAccount which is matched to the receipts
	 * @return the sum of the receipts with the matching receipt account
	 */
	private int getAccountTotal(ReceiptAccount receiptAccount)
	{
		return communicator.getReceiptsSum(receiptAccount.getCode());
	}

	/**
	 * Gets the sum of all receipts that has the receipt account category that is passed in as a parameter
	 * 
	 * @param category
	 *            the receipt account category which is matched to the receipts
	 * @return the sum of the receipts with the matching receipt account category
	 */
	private int getCategoryTotal(String category)
	{
		List<Long> accounts = new ArrayList<Long>();
		for (ReceiptAccount ra : accountList)
		{
			if (ra.getCategory().equals(category))
			{
				accounts.add(ra.getCode());
			}
		}
		return communicator.getReceiptsSum(accounts);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{

		switch (parent.getId())
		{
			case R.id.category_spinner:
				String category = categoryList.get(position);
				updateAccountSpinner(category);
				setCategoryTotalView(category);
				break;
			case R.id.account_spinner:
				setAccountTotalView(position);
				break;
		}
	}

	private void setCategoryTotalView(String category)
	{
		if(category.equals("all"))
		{
			categoryTotalView.setText(R.string.select_a_category_for_details);
		}
		else
		{
			int val = getCategoryTotal(category);
			categoryTotalView.setText(String.valueOf(val));
		}
		setAccountTotalView(0);
	}

	private void updateAccountSpinner(String category)
	{
		List<ReceiptAccount> list = categoryAccountMap.get(category);
		if (list == null)
		{
			list = new ArrayList<ReceiptAccount>();
			for (ReceiptAccount ra : accountList)
			{
				if (ra.getCategory().equals(category))
				{
					list.add(ra);
				}
			}
			categoryAccountMap.put(category, list);
		}
		currentAccountList.clear();
		currentAccountList.addAll(list);
		receiptAccountAdapter.notifyDataSetChanged();
	}

	/**
	 * Sets the account total text view to the sum value of all the receipts that are represented by the receipt account
	 * at the position argument in the account list.
	 * 
	 * @param position
	 *            the index in the list
	 */
	private void setAccountTotalView(int position)
	{
		long val = getAccountTotal(currentAccountList.get(position));
		accountTotalView.setText(String.valueOf(val));
	}

	/**
	 * Nothing selected so we do nothing.
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
	}
}
