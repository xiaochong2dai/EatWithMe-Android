package com.way.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * client
 * 
 * @author way
 * 
 */
public class Client {

	private Socket socket;
	private ClientThread clientThread;
	private String ip;
	private int port;

	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public boolean start() {
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), 3000);
			if (socket.isConnected()) {
				clientThread = new ClientThread(socket);
				clientThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// get thread from client
	public ClientInputThread getClientInputThread() {
		return clientThread.getIn();
	}

	
	public ClientOutputThread getClientOutputThread() {
		return clientThread.getOut();
	}


	public void setIsStart(boolean isStart) {
		clientThread.getIn().setStart(isStart);
		clientThread.getOut().setStart(isStart);
	}
	
	public class ClientThread extends Thread {

		private ClientInputThread in;
		private ClientOutputThread out;

		public ClientThread(Socket socket) {
			in = new ClientInputThread(socket);
			out = new ClientOutputThread(socket);
		}

		public void run() {
			in.setStart(true);
			out.setStart(true);
			in.start();
			out.start();
		}


		public ClientInputThread getIn() {
			return in;
		}


		public ClientOutputThread getOut() {
			return out;
		}
	}
}
