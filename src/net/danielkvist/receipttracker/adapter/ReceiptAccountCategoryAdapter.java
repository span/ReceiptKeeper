package net.danielkvist.receipttracker.adapter;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * This is a custom adapter for the ReceiptAccount category spinners. Since it
 * is not user changeable we create a Set of Strings that we use to power the
 * adapter. It takes the usual Context, text view resource id and a List of the
 * receipt accounts as parameters.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptAccountCategoryAdapter extends ArrayAdapter<String> {

	private List<String> list;

	/**
	 * The constructor that calls the super method and then adds the list to the
	 * adapter and saves it for future reference
	 * 
	 * @param context
	 *            the context
	 * @param textViewResourceId
	 *            the text view id
	 * @param list
	 *            the list
	 */
	public ReceiptAccountCategoryAdapter(Context context,
			int textViewResourceId, List<String> list) {
		super(context, textViewResourceId);
		this.list = list;
		this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.addAll(list);
	}

	/**
	 * We override getItem to be able to load the string resource instead of the
	 * resource name that is stored in the database.
	 */
	@Override
	public String getItem(int position) {
		return getContext().getResources().getString(
				getContext().getResources().getIdentifier(list.get(position),
						"string", "net.danielkvist.receipttracker"));
	}

	/**
	 * Finds the position in the Set based on the category name. Returns the
	 * "none" category if no other is found.
	 * 
	 * @param category
	 *            the category to find the position for
	 * @return an int representing the position if the category exists, 0
	 *         otherwise
	 */
	public int findCategoryPosition(String category) {
		int i = 0;
		if (list.contains(category)) {
			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext()) {
				if (category.equals(iterator.next())) {
					return i;
				}
				i++;
			}
			return findCategoryPosition("none");
		}
		return 0;
	}
}
