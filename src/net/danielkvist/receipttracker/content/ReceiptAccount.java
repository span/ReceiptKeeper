package net.danielkvist.receipttracker.content;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;

/**
 * This is a simple data holder class for the Receipt Accounts. Please note that in order for toString to return a
 * complete String you need to set a Context with setContext before calling toString.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptAccount implements Comparable<ReceiptAccount>
{

    public static final String INVALID_ACCOUNT_MESSAGE = "You must enter a valid code before saving, duplicates are not allowed nor is an empty field.";
    public static long DEFAULT_ACCOUNT = 9999;
    private String name;
    private long code;
    private boolean userAdded;
    private long rowId;
    private Context context;
    private String category;

    /**
     * Constructor of the receipt account, initiates the parameters and sets a flag if it's a user added account or not
     * depending on the code. Any code with less then 4 digits is considered user added.
     * 
     * @param rowId
     *            the rowId in the database table
     * @param code
     *            the account code
     * @param name
     *            the account name
     * @param category
     *            the category name
     */
    public ReceiptAccount(long rowId, long code, String name, String category)
    {
        this.rowId = rowId;
        this.code = code;
        this.setName(name);
        this.category = category;
        this.setUserAdded(code < 1000);
    }

    /**
     * @return the code
     */
    public long getCode()
    {
        return code;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name.trim();
    }

    /**
     * @return the userAdded
     */
    public boolean isUserAdded()
    {
        return userAdded;
    }

    /**
     * @param userAdded
     *            the userAdded to set
     */
    public void setUserAdded(boolean userAdded)
    {
        this.userAdded = userAdded;
    }

    /**
     * 
     * @return the rowId
     */
    public long getRowId()
    {
        return rowId;
    }

    /**
     * Sets the code to the passed in valuye
     * 
     * @param code
     *            the code
     */
    public void setCode(long code)
    {
        this.code = code;
    }

    /**
     * Sets a context that is needed for the toString method to work properly
     * 
     * @param context
     *            the context
     */
    public void setContext(Context context)
    {
        this.context = context;
    }
    

    /**
     * Sets the category of this ReceiptAccount
     * 
     * @param category
     *            the category to set
     */
    public void setCategory(String category)
    {
        this.category = category;
    }

    /**
     * Gets the category of this ReceiptAccount
     * 
     * @return the category
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * This toString method needs a context to be provided with setContext to be able to return the appropriate name and
     * code result. If no Context is provided, only the code will be returned.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (context != null)
        {
            sb.append(getCode());
            sb.append(" - ");
            if (isUserAdded())
            {
                sb.append(getName());
            }
            else
            {
                // Since we store the "name" in the database we need to fetch the actual string resource that goes by
                // that name. This is why we need the Context.
                sb.append(context.getResources().getString(
                        context.getResources().getIdentifier(getName(), "string", "net.danielkvist.receipttracker")));
            }
        }
        else
        {
            sb.append(getCode());
        }

        return sb.toString();
    }

    /**
     * Compares the codes of the receipt. Used for ascending sort.
     */
    @Override
    public int compareTo(ReceiptAccount another)
    {
        return (int) (this.getCode() - another.getCode());
    }

    /**
     * Checks if the name and code are the same on the current receipt account and the other but does not compare the
     * exact references. Also note that the other must have a rowId greater then 0 to be true.
     */
    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ReceiptAccount))
            return false;
        ReceiptAccount receiptAccount = (ReceiptAccount) other;
        return receiptAccount.getName().equals(this.getName()) && receiptAccount.getCode() == this.getCode()
                && receiptAccount.getRowId() > 0;
    }

    /**
     * Checks if a receipt account is unique and doesn't already exist in the list.
     * 
     * @param receiptAccount
     *            the account to check
     * @param receiptAccounts
     *            the list of all accounts
     * @return true if unique
     */
    private static boolean isUnique(ReceiptAccount receiptAccount, List<ReceiptAccount> receiptAccounts)
    {
        boolean result = true;
        for (ReceiptAccount ra : receiptAccounts)
        {
            if (receiptAccount.equals(ra) && receiptAccount.getRowId() < 0)
            {
                result = false;
            }
        }
        return result;
    }

    /**
     * Checks to see if the receipt account is valid by first making sure it's unique and then that its name is not an
     * empty string and that the code is greater then 0.
     * 
     * @param receiptAccount
     * @param receiptAccounts
     * @return
     */
    public static boolean isValid(ReceiptAccount receiptAccount, List<ReceiptAccount> receiptAccounts)
    {
        return isUnique(receiptAccount, receiptAccounts) && !receiptAccount.getName().equals("") && receiptAccount.getCode() > 0;
    }
    
    /**
     * Creates a Set with strings representing the categories that are in the receiptAccounts List and returns it.
     * @param receiptAccounts the list with receipt accounts
     * @return a Set of strings with the categories
     */
    public static Set<String> getCategoriesFromList(List<ReceiptAccount> receiptAccounts)
    {
        Set<String> set = new HashSet<String>();
        for(ReceiptAccount ra : receiptAccounts)
        {
            set.add(ra.getCategory());
        }
        return set;
    }
}
