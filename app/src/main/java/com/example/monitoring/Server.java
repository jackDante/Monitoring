package com.example.monitoring;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.monitoring.MainActivity;
import com.example.monitoring.ServerActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import androidx.appcompat.app.AppCompatActivity;


public class Server extends AppCompatActivity {

	private int lenght = 3000;

	static ServerActivity activity;

	static ServerSocket serverSocket;

	static String message = "";
	static final int socketServerPORT = 8080;

	private TextView t;


	public Server(ServerActivity activity) {
		this.activity = activity;
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();

		new SimpleAsyncTask().execute(lenght);
	}


	public int getPort() {
		return socketServerPORT;
	}

	public void onDestroy() {
		super.onDestroy();
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


//-------------------------------------------------------------------------------
	protected static class SocketServerThread extends Thread {

		int count = 0;

		@Override
		public void run() {
			try {
				//ServerSocket(int port, int backlog, InetAddress bindAddr)
				//creo nuovo Socket!!!
				serverSocket = new ServerSocket(socketServerPORT);

				while (true) {
					Socket socket = serverSocket.accept();
					count++;
					message += "#" + count + " from "
							+ socket.getInetAddress() + ":"
							+ socket.getPort() + "\n";
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							activity.msg.setText(message);
						}
					});
					// questa operazione permette di inviare qualcosa al client,
					// fare in modo che quando supera una certa soglia parta la Reply
					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
							socket, count);
					//manderemo un valore
					socketServerReplyThread.run();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	//END SocketServerThread
	}

//-------------------------------------------------------------------------------
	protected static class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		int cnt;

		SocketServerReplyThread(Socket socket, int c) {
			hostThreadSocket = socket;
			cnt = c;
		}

		@Override
		public void run() {
			OutputStream outputStream;
			String msgReply = "Hello from Server, you are #" + cnt;

			try {
				outputStream = hostThreadSocket.getOutputStream();
				PrintStream printStream = new PrintStream(outputStream);
				printStream.print(msgReply);
				printStream.close();

				message += "replayed: " + msgReply + "\n";

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.msg.setText(message);
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					activity.msg.setText(message);
				}
			});
		}

	//END SocketServerReplyThread
	}


//-------------------------------------------------------------------------------
	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress
							.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "Server running at : "
								+ inetAddress.getHostAddress();
					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}
		return ip;
	}




	private class SimpleAsyncTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... ints) {
			for(int i=0; i<10; i++) {
				try {
					Thread.sleep(100);
					publishProgress(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return "Task completed!!!";
		}

		protected void onPostExecute(String result) {
			//show final result…
		}
		protected void onProgressUpdate(Integer... progress) {
			activity.infoip.setText(String.valueOf(progress[0]));
		}
	}


//END class Server
}
