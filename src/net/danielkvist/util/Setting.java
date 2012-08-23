package net.danielkvist.util;

/**
 * This is a simple data holder class that is used to store setting names and values. It also keeps static references to
 * the name of the setting fields.
 * 
 * @author Daniel Kvist
 * 
 */
public class Setting
{

    public static final int SETTING_STORAGE_LOCAL = 0;
    public static final int SETTING_STORAGE_CLOUD = 1;

    public static final String SETTING_FIELD_LOCATION = "location";
    public static final String SETTING_FIELD_SUM = "sum";
    public static final String SETTING_FIELD_TAX = "tax";
    public static final String SETTING_FIELD_COMMENT = "comment";
    public static final String SETTING_FIELD_ACCOUNT = "account";

    private int value;
    private String name;

    /**
     * Empty constructor that initiates the value to 0 and name to empty string
     */
    public Setting()
    {
        this.value = 0;
        this.name = "";
    }

    /**
     * Constructore which instantiates the class with the given values
     * 
     * @param name
     *            name of Setting
     * @param value
     *            value of Setting
     */
    public Setting(String name, int value)
    {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue()
    {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(int value)
    {
        this.value = value;
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

}
