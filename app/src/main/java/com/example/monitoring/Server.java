package com.example.monitoring;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;

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

	static ServerActivity activity;

	static ServerSocket serverSocket;

	//-------------------------
	private Socket socket;
	private boolean connected = false;
	//-------------------------

	static String message = "";
	static final int socketServerPORT = 8080;




	public Server(ServerActivity activity) {
		this.activity = activity;
		SocketServerThread socketServerThread = new SocketServerThread();
		socketServerThread.execute();
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


	protected class SocketServerThread extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				serverSocket = new ServerSocket(socketServerPORT);

				while(true) {

					//Listens for a connection to be made to this socket and accepts it.
					//The method blocks until a connection is made.
					socket = serverSocket.accept();
					connected = true; //in questo modo attivo la notifica quando sento rumore


					message += "-> "
							+ socket.getInetAddress() + " : "
							+ socket.getPort() +  " is connected!"  +
							"\n";

					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							activity.msg.setText(message);
						}
					});

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	//END SocketServerThread
	}




	protected class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		private String msgReply;


		SocketServerReplyThread(Socket socket, String mex) {
			hostThreadSocket = socket;
			msgReply = mex;
		}

		@Override
		public void run() {

			try {
				OutputStream o = hostThreadSocket.getOutputStream();
				PrintStream out = new PrintStream(o, true);

				out.println(msgReply);
				out.close();

				// testing --- if(socket.isClosed()) message += "[SocketChiuso!] ";
				message += "[Server] replayed: " + msgReply + "\n";
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.msg.setText(message);
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "[SocketServerReplyT] ---getOutputStream--- " + e.toString() + "\n";
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



	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();
					if (inetAddress.isSiteLocalAddress()) {
						ip += "SERVER running at : " + inetAddress.getHostAddress() + "\n";
					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "[getIpAddress]Something Wrong! " + e.toString() + "\n";
		}
		return ip;
	}



	public void notifica(String msg) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if(!socket.isClosed()) {
			SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, msg);
			socketServerReplyThread.run();
		}

	}

	//notifica se il server ha accettato una richiesta
	protected boolean exist() {
		return connected;
	}

	protected void closeServer() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		finish();
	}



//END class Server
}
