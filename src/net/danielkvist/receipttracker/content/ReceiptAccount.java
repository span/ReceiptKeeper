package net.danielkvist.receipttracker.content;

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

    private String name;
    private long code;
    private boolean userAdded;
    private long rowId;
    private Context context;

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
     */
    public ReceiptAccount(long rowId, long code, String name)
    {
        this.rowId = rowId;
        this.code = code;
        this.setName(name);
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
        this.name = name;
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

    @Override
    public int compareTo(ReceiptAccount another)
    {
        return (int) (this.getCode() - another.getCode());
    }

}
