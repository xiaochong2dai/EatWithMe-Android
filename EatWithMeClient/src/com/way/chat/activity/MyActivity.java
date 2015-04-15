package com.way.chat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.util.Constants;

/**
 * 
 * 
 * @author way
 * 
 */
public abstract class MyActivity extends Activity {
	/**
	 * 
	 */
	private BroadcastReceiver MsgReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			TranObject msg = (TranObject) intent
					.getSerializableExtra(Constants.MSGKEY);
			if (msg != null) {
				// System.out.println("MyActivity:" + msg);
				getMessage(msg);
			} else {
				close();
			}
		}
	};

	/**
	 * 
	 * 
	 * @param msg
	 *            
	 */
	public abstract void getMessage(TranObject msg);

	/**
	 * 
	 */
	public void close() {
		Intent i = new Intent();
		i.setAction(Constants.ACTION);
		sendBroadcast(i);
		finish();
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION);
		registerReceiver(MsgReceiver, intentFilter);

	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(MsgReceiver);
	}
}
