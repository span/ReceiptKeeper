package net.danielkvist.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import net.danielkvist.receipttracker.R;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationParser implements LocationListener
{
    private final String MESSAGE_PROVIDER_NA = "Provider not available.";
    private double longitude;
    private double latitude;
    private Context context;
    private Location location;
    private List<Address> addresses;
    private GeoPoint currentGeoPoint;
    
    public LocationParser(Context context)
    {
        this.context = context;
        initLocation();
    }
    
    private void initLocation()
    {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        setCurrentGeoPoint(new GeoPoint((int) (latitude*1000000), (int) (longitude*1000000)));

        if (location != null) 
        {
            Thread thread = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        addresses = geocoder.getFromLocation(Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()), 1);
                    } 
                    catch (IOException e)
                    {
                        Log.d(context.getString(R.string.tag_receipttracker), "Failed to init geocoder at location. " +
                                "Lat: " + getLatitude() + " - Long: " + getLongitude());
                        Log.d(context.getString(R.string.tag_receipttracker), e.getMessage());
                    }
                    
                }
            };
            
            thread.start();
        } 
        else 
        {
            latitude = -1;
            longitude = -1;
        }
    }
    
    public String getLatitude() { return latitude == -1 ? MESSAGE_PROVIDER_NA : String.valueOf(latitude); }
    
    public String getLongitude() { return latitude == -1 ? MESSAGE_PROVIDER_NA : String.valueOf(longitude); }
    
    public String getLongLatString() { return "Latitude: " + getLatitude() + " - Longitude: " + getLongitude(); }
    
    public String getAddress() { return addresses == null ? MESSAGE_PROVIDER_NA : addresses.get(0).getAddressLine(0); }

    public String getCity() { return addresses == null ? MESSAGE_PROVIDER_NA : addresses.get(0).getAddressLine(1); }
    
    public String getCountry() { return addresses == null ? MESSAGE_PROVIDER_NA : addresses.get(0).getAddressLine(2); }
    
    public String getFullAddress()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getAddress() + '\n');
        sb.append(getCity() + '\n');
        sb.append(getCountry());
        
        return sb.toString();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        setCurrentGeoPoint(new GeoPoint((int) (latitude*1000000), (int) (longitude*1000000)));
    }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    /**
     * @return the currentGeoPoint
     */
    public GeoPoint getCurrentGeoPoint()
    {
        return currentGeoPoint;
    }

    /**
     * @param currentGeoPoint the currentGeoPoint to set
     */
    public void setCurrentGeoPoint(GeoPoint currentGeoPoint)
    {
        this.currentGeoPoint = currentGeoPoint;
    }

    

}
