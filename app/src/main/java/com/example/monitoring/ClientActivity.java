package com.example.monitoring;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;


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
                Client myClient = new Client(editTextAddress.getText().toString(),
                        Integer.parseInt(editTextPort.getText().toString()),
                        response);
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



    public class Client extends AsyncTask<Void, String, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        TextView textResponse;

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
            }

            while(!socket.isClosed()) {
                try {
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

                    //publishProgress(response);


                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textResponse.setText(response);
                        }
                    });

/*
                    if(response.equals("santoDios"))
                        socket.close();
*/

                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response = "UnknownHostException: " + e.toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response = "IOException: " + e.toString();
                }
           }
/*
			finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			*/

			runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textResponse.setText("SONO USCITO dal While!!!");
                        }
                    });

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

        /*
        @Override
        protected void onPostExecute(Void result) {
            //se abbiamo un certo tipo di valore creare notifica push e non settare il text
            textResponse.setText(response);
            if(response.contentEquals("Welcome from Server!"))
                response += " HO LETTO IL CONTENUTO!";
            super.onPostExecute(result);
        }
        */

//END Class AsyncTask
    }

//END Class ClientActivity
}
