package com.example.sandugabriel.saferoad;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hasya on 2016-11-12.
 */

public class Session {
    private static final String LOG_TAG = "Session";

    private ArrayList<LocationData> mLocationDataList;
    private String mId;

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public Session() {
        mId = DATE_FORMATTER.format(new Date());
        mLocationDataList = new ArrayList<>();
    }

    // Public methods.

    public void flushLocationData() {
        try {
            OutputStreamWriter out;
            File outputFile = new File(getLocationDataPath());

            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            out = new OutputStreamWriter(new FileOutputStream(outputFile));

            // Write header.
            out.write("Timestamp, Latitude, Longitude, Speed");
            out.write("\n");

            // Write location data.
            for (int i = 0; i < mLocationDataList.size(); i++) {
                out.write(mLocationDataList.get(i).toString());
                out.write("\n");
            }

            // Close stream writer.
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters.

    public static String getDir() {
        String p = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
        File mediaStorageDir = new File(
                p

        );

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.i(LOG_TAG, "failed to create directory");
            return null;
        }

        return mediaStorageDir.getPath();
    }

    public String getId() {
        return mId;
    }

    public String getLocationDataPath() {
        String directory = getDir();

        if (directory == null) {
            return null;
        }

        return (directory + File.separator + mId + ".csv");
    }

    public String getVideoPath() {
        String directory = getDir();

        if (directory == null) {
            return null;
        }

        return (directory + File.separator + mId + ".mp4");
    }

    // Setters.

    public void addLocationData(LocationData locationData) {
        mLocationDataList.add(locationData);
    }
}
