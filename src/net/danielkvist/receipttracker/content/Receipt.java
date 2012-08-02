package net.danielkvist.receipttracker.content;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Receipt implements Parcelable
{
    public static final String EXTRA_RECEIPT = "extra_receipt";

    private int id;
    private String name;
    private String photo;
    private String date;
    private String time;
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
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        locationLat = "";
        locationLong = "";
        sum = "";
        tax = "";
        comment = "";
    }

    public Receipt(Parcel in)
    {
        this.id = in.readInt();
        
        String[] stringData = new String[9];
        in.readStringArray(stringData);

        this.name = stringData[0];
        this.photo = stringData[1];
        this.date = stringData[2];
        this.time = stringData[3];
        this.locationLat = stringData[4];
        this.locationLong = stringData[5];
        this.sum = stringData[6];
        this.tax = stringData[7];
        this.comment = stringData[8];
    }

    public Receipt(int id, String name, String photo, String date, String time, String locationLat,
            String locationLong, String sum, String tax, String comment)
    {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.date = date;
        this.time = time;
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
     * @return the date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * @return the time
     */
    public String getTime()
    {
        return time;
    }

    /**
     * @param time
     *            the time to set
     */
    public void setTime(String time)
    {
        this.time = time;
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeStringArray(new String[] { name, photo, date, time, locationLat, locationLong, sum, tax, comment });
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
