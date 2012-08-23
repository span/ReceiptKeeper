package net.danielkvist.receipttracker.content;

/**
 * This is a simple data holder class for the Receipt Accounts.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptAccount
{

    private String name;
    private long code;
    private boolean userAdded;
    private long rowId;

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

}
