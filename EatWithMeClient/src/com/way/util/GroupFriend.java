package com.way.util;

import java.util.List;

import com.way.chat.common.bean.User;

/**
 * 
 * 
 * @author way
 * 
 */
public class GroupFriend {
	private String groupName;// group name
	private List<User> groupChild;// group users

	public GroupFriend() {
		super();
	}

	public GroupFriend(String groupName, List<User> groupChild) {
		super();
		this.groupName = groupName;
		this.groupChild = groupChild;
	}

	public void add(User u) {// add users to group
		groupChild.add(u);
	}

	public void remove(User u) {// remove users from group
		groupChild.remove(u);
	}

	public void remove(int index) {//  remove users from group
		groupChild.remove(index);
	}

	public int getChildSize() {// group size
		return groupChild.size();
	}

	public User getChild(int index) {// get user from index
		return groupChild.get(index);
	}

	// get...set...
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<User> getGroupChild() {
		return groupChild;
	}

	public void setGroupChild(List<User> groupChild) {
		this.groupChild = groupChild;
	}

}
