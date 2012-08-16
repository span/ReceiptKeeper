package net.danielkvist.receipttracker.content;

public class ReceiptAccount
{

    private String name;
    private long code;
    private boolean userAdded;
    private long rowId;

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
     * @param name the name to set
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
     * @param userAdded the userAdded to set
     */
    public void setUserAdded(boolean userAdded)
    {
        this.userAdded = userAdded;
    }

    public long getRowId()
    {
        return rowId;
    }

}
