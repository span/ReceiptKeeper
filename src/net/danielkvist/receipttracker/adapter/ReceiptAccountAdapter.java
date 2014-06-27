package net.danielkvist.receipttracker.adapter;

import java.util.Collections;
import java.util.List;

import net.danielkvist.receipttracker.content.ReceiptAccount;
import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * This is a custom adapter for the Spinners that show a receipt accounts code
 * and name. Since the ReceiptAccounts toString method needs a context to be
 * able to get the string resources we override the getItem method and make sure
 * we set a context before toString is executed.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptAccountAdapter extends ArrayAdapter<ReceiptAccount> {
	private Context context;
	private List<ReceiptAccount> receiptAccountList;

	/**
	 * Constructor that takes the context and text view resource together with a
	 * List of the receipt accounts. It calls the super method and then sets a
	 * default dropdown resource and also stores the list for future reference.
	 * 
	 * @param context
	 *            the context
	 * @param resource
	 *            the text view resource
	 * @param receiptAccountList
	 *            the list with receipt accounts
	 */
	public ReceiptAccountAdapter(Context context, int resource,
			List<ReceiptAccount> receiptAccountList) {
		super(context, resource, receiptAccountList);
		this.context = context;
		this.receiptAccountList = receiptAccountList;
		this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	/**
	 * We override getItem to make sure we have a Context set on the
	 * ReceiptAccount instance since the toString method of the instance needs
	 * it to be able to properly process the text resources.
	 */
	@Override
	public ReceiptAccount getItem(int position) {
		ReceiptAccount receiptAccount = receiptAccountList.get(position);
		receiptAccount.setContext(context);
		return receiptAccount;
	}

	/**
	 * Find out which receipt in the list that was selected.
	 * 
	 * @param code
	 *            the code of the receipt account whose position you want
	 * @return the position in the original List or 0
	 */
	public int findReceiptAccountPosition(long code) {
		int i = 0;
		while (i < receiptAccountList.size()) {
			if (code == receiptAccountList.get(i).getCode())
				return i;
			i++;
		}
		return 0;
	}

	/**
	 * Sorts the list after a new addition and the calls the super method. We
	 * want to sort to have the dropdown in a nice ascending order.
	 */
	@Override
	public void notifyDataSetChanged() {
		Collections.sort(receiptAccountList);
		super.notifyDataSetChanged();
	}

}
