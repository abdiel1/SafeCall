package EmergencyProtocol;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;


/**
 * Created by abdielrosado on 3/29/16.
 */
public class LocationManagement implements LocationListener {

    public static final int GPS_TIME_INTERVAL = 900000;
    private static LocationManager locationManager;
    private static Location myLocation;
    private boolean requestingUpdates;

    public LocationManagement(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, 100, this);
        } catch (SecurityException e) {

        }
        requestingUpdates = true;
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
