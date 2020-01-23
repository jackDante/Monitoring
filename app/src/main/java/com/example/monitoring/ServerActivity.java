package com.example.monitoring;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;




public class ServerActivity extends AppCompatActivity {


    com.example.monitoring.Server server;

    public TextView infoip, msg, tv_noice, level;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        infoip =  findViewById(R.id.infoip);
        msg =  findViewById(R.id.msg);
        tv_noice =  findViewById(R.id.textView9);
        level = findViewById(R.id.textView10);
        //InetAddress a = new InetAddress();

        //I receive the intThreshold from the MainActivity (NumberPicker)
        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("intThreshold", 8);
        if(intValue==0) intValue=8;
        mThreshold = intValue;

        // Show alert when noise thersold crossed
        level.setText("Thersold = " + intValue);

        server = new com.example.monitoring.Server(this);

        infoip.setText(server.getIpAddress()+"PORT: "+server.getPort());

        // Defined SoundLevelView in main.xml file
        // Used to record voice
        mSensor = new DetectNoise();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "monitoring:MyTag");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //server.onDestroy();
    }






    /****************** Define runnable thread again and again detect noise *********/
    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");
            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);
            if ((amp > mThreshold)) {
                callForHelp(amp);
                //Log.i("Noise", "==== onCreate ===");
            }
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        //Log.i("Noise", "==== onResume ===");

        //initializeApplicationConstants();
        if (!mRunning) {
            mRunning = true;
            start();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Log.i("Noise", "==== onStop ===");
        //Stop noise monitoring
        stop();
    }


    private void start() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
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
        //mThreshold = 10;
    }

    private void updateDisplay(String status, double signalEMA) {
        //mStatusView.setText(status);
        Log.d("SOUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }

    private void callForHelp(double signalEMA) {
        //stop();
        // Show alert when noise thersold crossed
        //Toast.makeText(getApplicationContext(), "Noise Thersold Crossed, sending out an sos!",
        //        Toast.LENGTH_LONG).show();

        if (server.exist()) {
            server.notifica("SOS baby!");
            server.closeServer();
            server = new com.example.monitoring.Server(this);

            //Intent i = new Intent(this, MainActivity.class);
            //startActivity(i);
        }

        Log.d("SOUND", String.valueOf(signalEMA));
    }

//END CLASS ServerActivity
}