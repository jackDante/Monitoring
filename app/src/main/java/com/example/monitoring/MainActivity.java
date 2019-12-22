package com.example.monitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickParent(View w){
        Intent i= new Intent( this, ServerActivity.class);
        startActivity(i);
    }
    public void onClickChild(View w){
        Intent i= new Intent( this, ClientActivity.class);
        startActivity(i);
    }
    public void onClickSensor(View w){
        Intent i= new Intent( this, sensor.class);
        startActivity(i);
    }
}
