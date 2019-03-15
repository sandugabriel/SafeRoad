package com.example.sandugabriel.saferoad;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class GPSTracker extends Service implements LocationListener {
    private static final String LOG_TAG = "GPSTracker";

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // 100 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute

    private Context mContext;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Session mSession;

    private LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        initLocationManager();
    }

    public void initLocationManager() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                this.showSettingsAlert();
            } else {
                this.canGetLocation = true;
                // First get lastLocation from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                    );
                }
                // if GPS Enabled get lat/long using GPS Services
                else if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                    );
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        Log.i(LOG_TAG, "Stopping GPSTracker");
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(GPSTracker.this);
            }

            Intent intent = new Intent(mContext, GPSTracker.class);
            mContext.stopService(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is not enabled.");

        // Setting Dialog Message
        alertDialog.setMessage("Please go to the settings menu to enable.");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mSession != null) {
            mSession.addLocationData(new LocationData(
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getSpeed(),
                    Session.DATE_FORMATTER.format(new Date()))
            );
        }

        Toast.makeText(
                mContext,
                "Your Location is - \nLat: "
                        + location.getLatitude()
                        + "\nLong: "
                        + location.getLongitude(),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void setSession(Session session) {
        mSession = session;
    }

}