package emergency_protocol;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


/**
 * Created by abdielrosado on 3/29/16.
 */
public class LocationManagement implements LocationListener {

    public static final int GPS_TIME_INTERVAL = 900000;
    private static LocationManager locationManager;
    private static Location myLocation;
    private boolean requestingUpdates;
    private static LocationManagement locationManagement;

    private LocationManagement(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, 100, this);
        } catch (SecurityException e) {

        }
        requestingUpdates = true;
    }

    public static LocationManagement getInstance(Context context){
        if(locationManagement == null){
            locationManagement = new LocationManagement(context);
        }
        return locationManagement;
    }


    public void deactivateLocation() {
        if (locationManager != null && requestingUpdates) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {

            }
            requestingUpdates = false;
        }
    }

    public void requestLocation() {

        try {
            myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, 100, this);
        } catch (SecurityException e) {
            return;
        }
    }

    public String getLocation(){
        if(myLocation != null){
            return myLocation.getLatitude() + " " + myLocation.getLongitude();
        } else{
            return null;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        deactivateLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
