package com.example.sandugabriel.saferoad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.Toast;



import java.io.IOException;

public class CameraActivity extends Activity implements AccelerometerListener {
    private static final String LOG_TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private Boolean mIsRecording = false;
    private GPSTracker mGPSTracker;
    private Session mSession;
    private FloatingActionButton mCaptureButton;
    private Chronometer mChronometer;
    private Context mContext;
    private GLSurfaceView mGLView;
    CameraPreview surface_view;
    private WindowManager mWindowManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mContext = this;
        mCamera = getCameraInstance();
        mGPSTracker = new GPSTracker(CameraActivity.this);

        mChronometer = (Chronometer) findViewById(R.id.camera_chronometer);

        // Create our Preview view and set it as the content of our activity.

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Add a listener to the Capture button
        mCaptureButton = (FloatingActionButton) findViewById(R.id.camera_capture);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if GPS enabled
                if (!mGPSTracker.canGetLocation()) {
                    mGPSTracker.showSettingsAlert();
                }

                if (mIsRecording) {
                    // stop recording and release camera
                    mChronometer.stop();
                    mChronometer.setVisibility(View.GONE);

                    mCaptureButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_videocam_white_48dp));

                    mMediaRecorder.stop();  // stop the recording
                    releaseMediaRecorder(); // release the MediaRecorder object
                    mCamera.lock();         // take camera access back from MediaRecorder

                    mGPSTracker.stop();
                    mSession.flushLocationData();

                    mIsRecording = false;
                } else {
                    mSession = new Session();
                    mGPSTracker.setSession(mSession);

                    // initialize video camera
                    if (prepareVideoRecorder()) {
                        // Camera is available and unlocked, MediaRecorder is prepared,
                        // now you can start recording
                        mCaptureButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_stop_white_48dp));
                        mMediaRecorder.start();

                        mChronometer.setText("00:00");
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.setVisibility(View.VISIBLE);
                        mChronometer.start();
                        mIsRecording = true;
                    } else {
                        releaseMediaRecorder();
                    }
                }
            }
        });
    }

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.i(LOG_TAG, "Camera not available!");
        }
        return c; // returns null if camera is unavailable
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }*/

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        mGPSTracker.stop();
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onDestroy Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private boolean prepareVideoRecorder() {
        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(mSession.getVideoPath());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.i(LOG_TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.i(LOG_TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {


    }

    @Override
    public void onShake(float force) {
        if (mIsRecording) {
            Toast.makeText(this, "Motion detected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DangerActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onStop Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(AccelerometerManager.isSupported(this)){
            AccelerometerManager.startListening(this);
        }
    }
}