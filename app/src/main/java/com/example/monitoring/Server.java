package com.example.monitoring;

import android.os.StrictMode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
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
    private int count = 0;
	//-------------------------

	static String message = "";
	static final int socketServerPORT = 8080;



	public Server(ServerActivity activity) {
		this.activity = activity;
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
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



	protected class SocketServerThread extends Thread {

		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(socketServerPORT);

				//Listens for a connection to be made to this socket and accepts it.
				//The method blocks until a connection is made.
				socket = serverSocket.accept();

/*
				SocketServerReplyThread socketServerReplyThread =
						new SocketServerReplyThread(socket, "Welcome from Server! \n");
				socketServerReplyThread.run();

				Thread.sleep(1000);

				SocketServerReplyThread socketServerReplyThread2 =
						new SocketServerReplyThread(socket, "Welcome222 from Server! \n");
				socketServerReplyThread2.run();
*/

				message += "#" + count + " from "
						+ socket.getInetAddress() + ":"
						+ socket.getPort() + "\n";

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.msg.setText(message);
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	//END SocketServerThread
	}


	protected class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		private String msgReply;


		private OutputStream outputStream;
		private PrintStream printStream;


		SocketServerReplyThread(Socket socket, String mex) {
			hostThreadSocket = socket;
			msgReply = mex;
		}


		@Override
		public void run() {

			try {
				/*
				outputStream = hostThreadSocket.getOutputStream();

				printStream = new PrintStream(outputStream, true);
				printStream.println(msgReply);
				printStream.close();
				*/

				outputStream = hostThreadSocket.getOutputStream();
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
				BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
				PrintWriter out = new PrintWriter(bufferedWriter, true);
				out.println(msgReply);

				if(msgReply.equals("santoDios"))
					out.close();
					//printStream.close();



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
								+ inetAddress.getHostAddress() + "\n";
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

	public void notifica(String msg){
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		SocketServerReplyThread socketServerReplyThread =
				new SocketServerReplyThread(socket, msg);
		socketServerReplyThread.run();
	}





//END class Server
}
