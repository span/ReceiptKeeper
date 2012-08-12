package net.danielkvist.receipttracker.content;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

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
    }

    public Receipt(Parcel in)
    {
        this.id = in.readInt();
        this.timestamp = in.readLong();
        
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

    public Receipt(int id, String name, String photo, long timestamp, String locationLat,
            String locationLong, String sum, String tax, String comment)
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
    }

    

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
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
     * @param name
     *            the name to set
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
     * @param photo
     *            the photo to set
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
    
    public String getDate(Context context)
    {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(timestamp);
        calendar.setTime(date);
        
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
        return dateFormat.format(date);
    }
    
    public String getTime(Context context)
    {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(timestamp);
        calendar.setTime(date);
        
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context.getApplicationContext());
        return timeFormat.format(date);
    }
    
    public String getDateAndTime(Context context)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getDate(context)).append(" - ").append(getTime(context));
        return sb.toString();
    }

    /**
     * @param timestamp
     *            the timestamp to set
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
     * @param locationLat
     *            the locationLat to set
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
     * @param locationLong
     *            the locationLong to set
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
     * @param sum
     *            the sum to set
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
     * @param tax
     *            the tax to set
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
     * @param comment
     *            the comment to set
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    
    private String getDateAndTime()
    {
        return String.valueOf(timestamp);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" ");
        sb.append(getDateAndTime());
        return sb.toString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeLong(timestamp);
        dest.writeStringArray(new String[] { name, photo, locationLat, locationLong, sum, tax, comment });
    }

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
