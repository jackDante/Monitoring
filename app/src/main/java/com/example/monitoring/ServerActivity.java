package com.example.monitoring;

import android.net.DhcpInfo;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;


import java.net.InetAddress;

import androidx.appcompat.app.AppCompatActivity;

public class ServerActivity extends Activity {


    com.example.monitoring.Server server;
    public TextView infoip, msg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        infoip =  findViewById(R.id.infoip);
        msg =  findViewById(R.id.msg);
        //InetAddress a = new InetAddress();
        server = new com.example.monitoring.Server(this);

        infoip.setText(server.getIpAddress()+":"+server.getPort());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }


}