package com.way.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.way.chat.common.util.Constants;
import com.way.chat.common.util.MyDate;

/**
 * 
 * 
 * @author way
 * 
 */
public class Server {
	private ExecutorService executorService;
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private boolean isStarted = true;

	public Server() {
		try {
			executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
					.availableProcessors() * 50);
			serverSocket = new ServerSocket(Constants.SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
			quit();
		}
	}

	public void start() {
		System.out.println(MyDate.getDateEN() + " Server started...");
		try {
			while (isStarted) {
				socket = serverSocket.accept();
				String ip = socket.getInetAddress().toString();
				System.out.println(MyDate.getDateEN() + " User£º" + ip + " connected");
				if (socket.isConnected())
					executorService.execute(new SocketTask(socket));
			}
			if (socket != null)
				socket.close();
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			// isStarted = false;
		}
	}

	private final class SocketTask implements Runnable {
		private Socket socket = null;
		private InputThread in;
		private OutputThread out;
		private OutputThreadMap map;

		public SocketTask(Socket socket) {
			this.socket = socket;
			map = OutputThreadMap.getInstance();
		}

		@Override
		public void run() {
			out = new OutputThread(socket, map);
			in = new InputThread(socket, out, map);
			out.setStart(true);
			in.setStart(true);
			in.start();
			out.start();
		}
	}

	/**
	 * 
	 */
	public void quit() {
		try {
			this.isStarted = false;
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server().start();
	}
}
