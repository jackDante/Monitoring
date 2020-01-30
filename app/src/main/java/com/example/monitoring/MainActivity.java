package com.example.monitoring;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected NumberPicker np;
    private int Threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        np = findViewById(R.id.numberPicker);

        np.setMinValue(1);
        np.setMaxValue(20);
        np.setValue(8);

        np.setOnValueChangedListener(onValueChangeListener);
    }

    NumberPicker.OnValueChangeListener onValueChangeListener =
            new 	NumberPicker.OnValueChangeListener(){
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    Threshold = numberPicker.getValue();
                }
            };


    public void onClickParent(View w){
        Intent i= new Intent( this, ServerActivity.class);
        i.putExtra("intThreshold", Threshold);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void closeApp (View view){
        finishAndRemoveTask();
    }


}
