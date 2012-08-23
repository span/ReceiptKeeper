package net.danielkvist.receipttracker.content;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is a parcelable class that holds data related to a Receipt.
 * @author Daniel Kvist
 *
 */
public class Receipt implements Parcelable
{
    public static final String EXTRA_RECEIPT = "extra_receipt";

    private int id;
    private String name;
    private String photo;
    private long timestamp;
    private String locationLat;
    private String locationLong;
    private String sum;
    private String tax;
    private String comment;

    private long account_id;
    
    /**
     * Constructor for the class that initiates all strings to empty strings, sets
     * the timestamp to the current time in ms and any integer values gets set to -1.
     */
    public Receipt() 
    { 
        id = -1;
        name = "";
        photo = "";
        timestamp = System.currentTimeMillis();
        locationLat = "";
        locationLong = "";
        sum = "";
        tax = "";
        comment = "";
        account_id = -1;
    }

    /**
     * Constructor for the parcelable interface that re-initiates the values
     * @param in, the Parcel that contains the data
     */
    public Receipt(Parcel in)
    {
        this.id = in.readInt();
        this.timestamp = in.readLong();
        this.account_id = in.readLong();
        
        String[] stringData = new String[7];
        in.readStringArray(stringData);

        this.name = stringData[0];
        this.photo = stringData[1];
        this.locationLat = stringData[2];
        this.locationLong = stringData[3];
        this.sum = stringData[4];
        this.tax = stringData[5];
        this.comment = stringData[6];
    }

    /**
     * Full constructor for the class that initiates all values
     * @param id the id for the receipt
     * @param name the name for the receipt
     * @param photo the path in /mnt/media/... format
     * @param timestamp the timestamp to use for the receipt
     * @param locationLat a string representation of the latitude
     * @param locationLong a string representation of the longitude
     * @param sum the total sum on the receipt
     * @param tax the tax on the receipt
     * @param comment any comment on the receipt
     * @param account_id the account id that should be associated with the receipt
     */
    public Receipt(int id, String name, String photo, long timestamp, String locationLat,
            String locationLong, String sum, String tax, String comment, long account_id)
    {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.timestamp = timestamp;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
        this.sum = sum;
        this.tax = tax;
        this.comment = comment;
        this.account_id = account_id;
    }

    

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id)
    {
        this.id = id;
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
     * @return the photo
     */
    public String getPhoto()
    {
        return photo;
    }

    /**
     * @param photo the photo to set
     */
    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }
    
    /**
     * This method returns the receipts date as a String that is formatted according
     * to the users preferences.
     * @param context
     * @return the string representation of the date
     */
    public String getDate(Context context)
    {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(timestamp);
        calendar.setTime(date);
        
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
        return dateFormat.format(date);
    }
    
    /**
     * This method returns the receipts time as a String that is formatted according
     * to the users preferences.
     * @param context
     * @return the string representation of the time
     */
    public String getTime(Context context)
    {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(timestamp);
        calendar.setTime(date);
        
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context.getApplicationContext());
        return timeFormat.format(date);
    }
    
    /**
     * This method takes the date and time and returns them as one string.
     * @param context
     * @return the date and time as a string
     */
    public String getDateAndTime(Context context)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getDate(context)).append(" - ").append(getTime(context));
        return sb.toString();
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * @return the locationLat
     */
    public String getLocationLat()
    {
        return locationLat;
    }

    /**
     * @param locationLat the locationLat to set
     */
    public void setLocationLat(String locationLat)
    {
        this.locationLat = locationLat;
    }

    /**
     * @return the locationLong
     */
    public String getLocationLong()
    {
        return locationLong;
    }

    /**
     * @param locationLong the locationLong to set
     */
    public void setLocationLong(String locationLong)
    {
        this.locationLong = locationLong;
    }

    /**
     * @return the sum
     */
    public String getSum()
    {
        return sum;
    }

    /**
     * @param sum the sum to set
     */
    public void setSum(String sum)
    {
        this.sum = sum;
    }

    /**
     * @return the tax
     */
    public String getTax()
    {
        return tax;
    }

    /**
     * @param tax the tax to set
     */
    public void setTax(String tax)
    {
        this.tax = tax;
    }

    /**
     * @return the comment
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    
    /**
     * 
     * @return account_id
     */
    public long getReceiptAccountId()
    {
        return account_id;
    }
    
    /**
     * @param accountId the accountId to set
     */
    public void setReceiptAccountId(long accountId)
    {
        this.account_id = accountId;
    }
    
    /**
     * Private helper method to the toString() method
     * @return
     */
    private String getDateAndTime()
    {
        return String.valueOf(timestamp);
    }
    
    /**
     * Returns a string based on the receipt name and date and time
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
//        sb.append(" ");
//        sb.append(getDateAndTime());
        return sb.toString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * Writes the data to the parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeLong(timestamp);
        dest.writeLong(account_id);
        dest.writeStringArray(new String[] { name, photo, locationLat, locationLong, sum, tax, comment });
    }

    /**
     * Parcelable construction
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Receipt createFromParcel(Parcel in)
        {
            return new Receipt(in);
        }

        public Receipt[] newArray(int size)
        {
            return new Receipt[size];
        }
    };

}
