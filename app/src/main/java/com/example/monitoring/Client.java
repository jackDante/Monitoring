package com.example.monitoring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;


public class Client extends AsyncTask<Void, Void, Void> {

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
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
						1024);
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


				onProgressUpdate(byteArrayOutputStream.toString("UTF-8"));


				if(byteArrayOutputStream.toString("UTF-8").contentEquals("santoDios"))
					socket.close();


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

		return null;
	}

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

	protected void onProgressUpdate(String s) {
		//activity.infoip.setText(String.valueOf(progress[0]));
		//textResponse.setText(s);
		//if(response.contentEquals("Welcome from Server!"))
		//	response += " HO LETTO IL CONTENUTO!";
	}


}
