package net.danielkvist.receipttracker.content;

public class ReceiptAccount
{

    private String name;
    private long code;

    public ReceiptAccount(long code, String name)
    {
        this.code = code;
        this.setName(name);
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

}
