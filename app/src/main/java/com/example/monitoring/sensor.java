package com.example.monitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class sensor extends  AppCompatActivity{
    /* constants */
    private static final int POLL_INTERVAL = 300;
    /** running state **/
    private boolean mRunning = false;
    /** config state **/
    private int mThreshold;
    int RECORD_AUDIO = 0;
    private PowerManager.WakeLock mWakeLock;
    private Handler mHandler = new Handler();
    /* sound data source */
    private DetectNoise mSensor;

    public TextView tv_noice, status;


    /****************** Define runnable thread again and again detect noise *********/
    private Runnable mSleepTask = new Runnable() {
        public void run() {
            Log.i("Noise", "runnable mSleepTask");
            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);
            if ((amp > mThreshold)) {
                try {
                    callForHelp(amp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("Noise", "==== onCreate ===");
            }
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Defined SoundLevelView in main.xml file
        setContentView(R.layout.activity_sensor);
        tv_noice =  findViewById(R.id.tv_noice);
        status =  findViewById(R.id.status);

        // Used to record voice
        mSensor = new DetectNoise();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "monitoring:MyTag");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("Noise", "==== onResume ===");

        initializeApplicationConstants();
        if (!mRunning) {
            mRunning = true;
            start();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i("Noise", "==== onStop ===");
        //Stop noise monitoring
        stop();
    }


    private void start() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);
        }

        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }
    private void stop() {
        Log.d("Noise", "==== Stop Noise Monitoring===");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        mRunning = false;
    }

    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 8;
    }

    private void updateDisplay(String s, double signalEMA) {
        status.setText(s);
        //bar.setProgress((int)signalEMA);
        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA + "   dB");
    }

    private void callForHelp(double signalEMA) throws IOException {
        //stop();
        // Show alert when noise thersold crossed
        Toast.makeText(getApplicationContext(), "Noise Thersold Crossed, do here your stuff.",
                Toast.LENGTH_LONG).show();

        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA + " dB");
    }



//END sensor CLASS
}
