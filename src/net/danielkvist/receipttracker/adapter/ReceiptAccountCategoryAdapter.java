package net.danielkvist.receipttracker.adapter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.danielkvist.receipttracker.content.ReceiptAccount;
import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * This is a custom adapter for the ReceiptAccount category spinners. Since it is not user changeable we create a Set of
 * Strings that we use to power the adapter. It takes the usual Context, text view resource id and a List of the receipt
 * accounts as parameters.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptAccountCategoryAdapter extends ArrayAdapter<String>
{

    private Set<String> set;

    /**
     * The constructor that calls the super method and then creates a Set of Strings which is used to power the adapter.
     * 
     * @param context
     * @param textViewResourceId
     * @param receiptAccountList
     */
    public ReceiptAccountCategoryAdapter(Context context, int textViewResourceId, List<ReceiptAccount> receiptAccountList)
    {
        super(context, textViewResourceId);
        this.set = getCategorySet(receiptAccountList);
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.addAll(set);
    }

    /**
     * Helper method to loop through the List of receipt accounts and create the Set
     * 
     * @param receiptAccountList
     *            the list of accounts
     * @return a Set of Strings
     */
    private Set<String> getCategorySet(List<ReceiptAccount> receiptAccountList)
    {
        Set<String> categorySet = new HashSet<String>();
        for (ReceiptAccount ra : receiptAccountList)
        {
            categorySet.add(ra.getCategory());
        }
        return categorySet;
    }

    /**
     * Finds the position in the Set based on the category name. Returns the "none" category if no other is found.
     * 
     * @param category
     *            the category to find the position for
     * @return an int representing the position
     */
    public int findCategoryPosition(String category)
    {
        int i = 0;
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext())
        {
            if (category.equals(iterator.next()))
            {
                return i;
            }
            i++;
        }
        return findCategoryPosition("none");
    }
}
