package com.way.util;

import com.way.chat.common.util.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// user's passwd
	public void setPasswd(String passwd) {
		editor.putString("passwd", passwd);
		editor.commit();
	}

	public String getPasswd() {
		return sp.getString("passwd", "");
	}

	// user's ID
	public void setId(String id) {
		editor.putString("id", id);
		editor.commit();
	}

	public String getId() {
		return sp.getString("id", "");
	}

	// user's name
	public String getName() {
		return sp.getString("name", "");
	}

	public void setName(String name) {
		editor.putString("name", name);
		editor.commit();
	}

	// user's email
	public String getEmail() {
		return sp.getString("email", "");
	}

	public void setEmail(String email) {
		editor.putString("email", email);
		editor.commit();
	}

	// user's image
	public Integer getImg() {
		return sp.getInt("img", 0);
	}

	public void setImg(int i) {
		editor.putInt("img", i);
		editor.commit();
	}

	// ip
	public void setIp(String ip) {
		editor.putString("ip", ip);
		editor.commit();
	}

	public String getIp() {
		return sp.getString("ip", Constants.SERVER_IP);
	}

	// port
	public void setPort(int port) {
		editor.putInt("port", port);
		editor.commit();
	}

	public int getPort() {
		return sp.getInt("port", Constants.SERVER_PORT);
	}

	// running in backgroup or not
	public void setIsStart(boolean isStart) {
		editor.putBoolean("isStart", isStart);
		editor.commit();
	}

	public boolean getIsStart() {
		return sp.getBoolean("isStart", false);
	}

	// first time using the app?
	public void setIsFirst(boolean isFirst) {
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst() {
		return sp.getBoolean("isFirst", true);
	}
}
