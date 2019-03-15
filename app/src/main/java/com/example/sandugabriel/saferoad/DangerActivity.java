package com.example.sandugabriel.saferoad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DangerActivity extends Activity {
    private TextView textView = null;
    private GestureDetectorCompat gestureDetectorCompat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger);

        // Get the text view.
        textView = (TextView)findViewById(R.id.textView);

        // Create a common gesture listener object.
        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();

        // Set activity in the listener.
        gestureListener.setActivity(this);

        // Create the gesture detector with the gesture listener.
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass activity on touch event to the gesture detector.
        if(gestureDetectorCompat.onTouchEvent(event)){
            Intent myintent = new Intent(this, CameraActivity.class);
            startActivity(myintent);
        }
        // Return true to tell android OS that event has been consumed, do not pass it to other event listeners.
        return true;
    }

}

