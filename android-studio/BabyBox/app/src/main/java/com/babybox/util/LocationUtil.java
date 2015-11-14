package com.babybox.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by keithlei on 3/16/15.
 */
public class LocationUtil {

    private Location latestLocation;

    private static LocationUtil instance;

    private static LocationListener locationListener;

    private LocationUtil() {
    }

    public static LocationUtil getInstance() {
        if (instance == null) {
            instance = new LocationUtil();
        }
        return instance;
    }

    public Location getLatestLocation() {
        return latestLocation;
    }

    public void setLatestLocation(Location location) {
        Log.d(this.getClass().getSimpleName(), "setLatestLocation: latestLocation=["+location.getLongitude()+","+location.getLatitude()+"]");
        this.latestLocation = location;
    }

    public Location getLastKnownLocation(Context context) {
        Location location = null;
        try {
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                // init location listener
                if (locationListener == null) {
                    locationListener = new LocationListener() {
                        public void onLocationChanged(Location location) {
                            if (location != null) {
                                setLatestLocation(location);

                                try {
                                    // Note: only call once to get latest location
                                    locationManager.removeUpdates(locationListener);
                                } catch (Exception e) {
                                    Log.e(this.getClass().getSimpleName(), "getLastKnownLocation.locationListener.onLocationChanged: Exception: ", e);
                                }
                            }
                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onProviderDisabled(String provider) {
                        }
                    };
                }

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    Log.d(this.getClass().getSimpleName(), "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            setLatestLocation(location);
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Log.d(this.getClass().getSimpleName(), "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                setLatestLocation(location);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "getLastKnownLocation: Exception: ", e);
        }

        return location;
    }
}
