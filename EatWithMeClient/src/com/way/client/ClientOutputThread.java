package com.way.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;

/**
 * 
 * 
 * @author way
 * 
 */
public class ClientOutputThread extends Thread {
	private Socket socket;
	private ObjectOutputStream oos;
	private boolean isStart = true;
	private TranObject msg;

	public ClientOutputThread(Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	
	public void setMsg(TranObject msg) {
		this.msg = msg;
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				if (msg != null) {
					System.out.println("msg:"+msg);
					oos.writeObject(msg);
					oos.flush();
					if (msg.getType() == TranObjectType.LOGOUT) {
						
						break;
					}
					synchronized (this) {
						wait();
					}
				}
			}
			oos.close();
			if (socket != null && !socket.isClosed())
				socket.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
