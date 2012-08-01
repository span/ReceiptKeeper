package net.danielkvist.util;

public class Setting
{

    public static final int SETTING_STORAGE_LOCAL = 0;
    public static final int SETTING_STORAGE_CLOUD = 1;
    

    public static final String SETTING_FIELD_LOCATION = "location";
    public static final String SETTING_FIELD_SUM = "sum";
    public static final String SETTING_FIELD_TAX = "tax";
    public static final String SETTING_FIELD_COMMENT = "comment";
    
    
    private int value;
    private String name;

    public Setting()
    {
        
    }
    
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
     * @param value the value to set
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
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }


}
