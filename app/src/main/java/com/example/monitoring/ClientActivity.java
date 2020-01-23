package com.example.monitoring;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


public class ClientActivity extends Activity {


    TextView response;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;

    private SharedPreferences mPreferences;

    private String sharedPrefFile =
            "com.example.android.monitoring";
    private String ip;
    private String port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        get();//last ip and port

        editTextAddress =  findViewById(R.id.addressEditText);
        editTextPort =  findViewById(R.id.portEditText);
        editTextAddress.setText(ip);
        editTextPort.setText(port);

        buttonConnect =  findViewById(R.id.connectButton);
        buttonClear =  findViewById(R.id.clearButton);
        response = findViewById(R.id.responseTextView);


        buttonConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Client myClient = new Client(editTextAddress.getText().toString(),
                        Integer.parseInt(editTextPort.getText().toString()),
                        response);
                myClient.execute();
            }
        });

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextAddress.setText("");
                editTextPort.setText("");
            }
        });
    }

    private void get(){
        String sharedPrefFile = "com.example.simplesavingsapp";
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        ip = mPreferences.getString("ip", "192.168.1.0");
        port = mPreferences.getString("port", "8080");
    }

    private void set() {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("ip", editTextAddress.getText().toString());
        preferencesEditor.putString("port", editTextPort.getText().toString());
        preferencesEditor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        set();
    }

    @Override
    protected void onResume() {
        super.onResume();
        get();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        set();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void closeApp (View view){
        finishAndRemoveTask();
    }



    public class Client extends AsyncTask<Void, String, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        TextView textResponse;
        boolean status = false;

        Client(String addr, int port, TextView textResponse) {
            dstAddress = addr;
            dstPort = port;
            this.textResponse=textResponse;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
            } catch (IOException e) {
                e.printStackTrace();
                //manage Exception Handling
                return null;
            }

            if(socket != null) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textResponse.setText("The monitoring is running...");
                        }
                    });

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                    byte[] buffer = new byte[1024];

                    int bytesRead;
                    InputStream inputStream = socket.getInputStream();

                    /*
                     * notice: inputStream.read() will block if no data return
                     */
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        response += byteArrayOutputStream.toString("UTF-8");
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textResponse.setText(response);
                        }
                    });

                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response = "UnknownHostException: " + e.toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response = "IOException: " + e.toString();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                            status = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }


/*
        protected void onProgressUpdate(String... s) {
            //activity.infoip.setText(String.valueOf(progress[0]));
            TextView t1 = findViewById(R.id.responseTextView);
            t1.setText(String.valueOf(s));
            if(response.equals("Welcome from Server! \n"))
                t1.setText(" HO LETTO IL CONTENUTO!");
        }
*/


        @Override
        protected void onPostExecute(Void result) {
            if(status) {
                Toast.makeText(getApplicationContext(), "Noise Thersold Crossed!",
                        Toast.LENGTH_LONG).show();
                super.onPostExecute(result);

                addNotification();
                comebackhome();
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textResponse.setText("Error: IP or port is incorrect!");
                    }
                });
            }

        }

//END Class AsyncTask
    }



    private void addNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("MONITORING") // title for notification
                .setContentText("Dear, You have to help your baby!")// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), ClientActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());

    }
    private void comebackhome() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

//END Class ClientActivity
}
