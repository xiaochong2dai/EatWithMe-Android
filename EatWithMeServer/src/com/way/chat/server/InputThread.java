package com.way.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.MyDate;
import com.way.chat.dao.UserDao;
import com.way.chat.dao.impl.UserDaoFactory;

/**
 * 
 * 
 * @author way
 * 
 */
public class InputThread extends Thread {
	private Socket socket;// socket object
	private OutputThread out;
	private OutputThreadMap map;
	private ObjectInputStream ois;
	private boolean isStart = true;

	public InputThread(Socket socket, OutputThread out, OutputThreadMap map) {
		this.socket = socket;
		this.out = out;
		this.map = map;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				readMessage();
			}
			if (ois != null)
				ois.close();
			if (socket != null)
				socket.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readMessage() throws IOException, ClassNotFoundException {
		Object readObject = ois.readObject();
		UserDao dao = UserDaoFactory.getInstance();
		if (readObject != null && readObject instanceof TranObject) {
			TranObject read_tranObject = (TranObject) readObject;
			switch (read_tranObject.getType()) {
			case REGISTER:
				User registerUser = (User) read_tranObject.getObject();
				int registerResult = dao.register(registerUser);
				System.out.println(MyDate.getDateCN() + " New register:"
						+ registerResult);

				TranObject<User> register2TranObject = new TranObject<User>(
						TranObjectType.REGISTER);
				User register2user = new User();
				register2user.setId(registerResult);
				register2TranObject.setObject(register2user);
				out.setMessage(register2TranObject);
				break;
			case LOGIN:
				User loginUser = (User) read_tranObject.getObject();
				System.out.println("server loginUser:"+loginUser);
				ArrayList<User> list = dao.login(loginUser);
				TranObject<ArrayList<User>> login2Object = new TranObject<ArrayList<User>>(
						TranObjectType.LOGIN);
				if (list != null) {// if successfully logged in
					TranObject<User> onObject = new TranObject<User>(
							TranObjectType.LOGIN);
					User login2User = new User();
					login2User.setId(loginUser.getId());
					onObject.setObject(login2User);
					for (OutputThread onOut : map.getAll()) {
						onOut.setMessage(onObject);
					}
					map.add(loginUser.getId(), out);
					login2Object.setObject(list);
				} else {
					login2Object.setObject(null);
				}
				out.setMessage(login2Object);

				System.out.println(MyDate.getDateCN() + " User£º"
						+ loginUser.getId() + " is online");
				break;
			case LOGOUT:
				User logoutUser = (User) read_tranObject.getObject();
				int offId = logoutUser.getId();
				System.out
						.println(MyDate.getDateCN() + " User£º" + offId + " is offline");
				dao.logout(offId);
				isStart = false;
				map.remove(offId);
				out.setMessage(null);
				out.setStart(false);

				TranObject<User> offObject = new TranObject<User>(
						TranObjectType.LOGOUT);
				User logout2User = new User();
				logout2User.setId(logoutUser.getId());
				offObject.setObject(logout2User);
				for (OutputThread offOut : map.getAll()) {
					offOut.setMessage(offObject);
				}
				break;
			case MESSAGE:
				
				int id2 = read_tranObject.getToUser();
				OutputThread toOut = map.getById(id2);
				if (toOut != null) {
					toOut.setMessage(read_tranObject);
				} else {
					TextMessage text = new TextMessage();
					text.setMessage("Your friend is offline.");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					offText.setFromUser(0);
					out.setMessage(offText);
				}
				break;
			case REFRESH:
				List<User> refreshList = dao.refresh(read_tranObject
						.getFromUser());
				TranObject<List<User>> refreshO = new TranObject<List<User>>(
						TranObjectType.REFRESH);
				refreshO.setObject(refreshList);
				out.setMessage(refreshO);
				break;
			default:
				break;
			}
		}
	}
}
