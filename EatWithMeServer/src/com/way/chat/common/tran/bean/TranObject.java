package com.way.chat.common.tran.bean;

import java.io.Serializable;
import java.util.List;

/**
 * transporting object using Socket
 * 
 * @author way
 */
public class TranObject<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TranObjectType type;// message type

	private int fromUser;// from which user
	private int toUser;// send to which user

	private T object;// the transporting object
	private List<Integer> group;// send which user in group

	public TranObject(TranObjectType type) {
		this.type = type;
	}

	public int getFromUser() {
		return fromUser;
	}

	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
	}

	public int getToUser() {
		return toUser;
	}

	public void setToUser(int toUser) {
		this.toUser = toUser;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public TranObjectType getType() {
		return type;
	}
}
