package com.way.chat.activity;

import java.util.LinkedList;

import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.util.SharePreferenceUtil;

import android.app.Application;
import android.app.NotificationManager;

public class MyApplication extends Application {
	private Client client;
	private static boolean isClientStart;
	private NotificationManager mNotificationManager;
	private int newMsgNum = 0;
	private LinkedList<RecentChatEntity> mRecentList;
	private RecentChatAdapter mRecentAdapter;
	private int recentNum = 0;

	@Override
	public void onCreate() {
		SharePreferenceUtil util = new SharePreferenceUtil(this,
				Constants.SAVE_USER);
		System.out.println(util.getIp() + " " + util.getPort());
		client = new Client(util.getIp(), util.getPort());
		client.start();
		isClientStart=true;
		
		mRecentList = new LinkedList<RecentChatEntity>();
		mRecentAdapter = new RecentChatAdapter(getApplicationContext(),
				mRecentList);
		super.onCreate();
		
		System.out.println("MyApplication  onCreate ......isClientStart:"+isClientStart);
	}

	public Client getClient() {
		return client;
	}

	public boolean isClientStart() {
		System.out.println("MyApplication  isClientStart:"+isClientStart);
		return isClientStart;
	}

	public void setClientStart(boolean isClientStart) {
		this.isClientStart = isClientStart;
	}

	public NotificationManager getmNotificationManager() {
		return mNotificationManager;
	}

	public void setmNotificationManager(NotificationManager mNotificationManager) {
		this.mNotificationManager = mNotificationManager;
	}

	public int getNewMsgNum() {
		return newMsgNum;
	}

	public void setNewMsgNum(int newMsgNum) {
		this.newMsgNum = newMsgNum;
	}

	public LinkedList<RecentChatEntity> getmRecentList() {
		return mRecentList;
	}

	public void setmRecentList(LinkedList<RecentChatEntity> mRecentList) {
		this.mRecentList = mRecentList;
	}

	public RecentChatAdapter getmRecentAdapter() {
		return mRecentAdapter;
	}

	public void setmRecentAdapter(RecentChatAdapter mRecentAdapter) {
		this.mRecentAdapter = mRecentAdapter;
	}

	public int getRecentNum() {
		return recentNum;
	}

	public void setRecentNum(int recentNum) {
		this.recentNum = recentNum;
	}
}
