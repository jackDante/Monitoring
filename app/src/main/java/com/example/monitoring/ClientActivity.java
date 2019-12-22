package com.example.monitoring;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ClientActivity extends Activity {

    TextView response;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        editTextAddress =  findViewById(R.id.addressEditText);
        editTextPort =  findViewById(R.id.portEditText);
        buttonConnect =  findViewById(R.id.connectButton);
        buttonClear =  findViewById(R.id.clearButton);
        response = findViewById(R.id.responseTextView);


        buttonConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Client myClient = new Client(editTextAddress.getText()
                        .toString(), Integer.parseInt(editTextPort
                        .getText().toString()), response);
                myClient.execute();
            }
        });

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                response.setText("");
            }
        });
    }

}
