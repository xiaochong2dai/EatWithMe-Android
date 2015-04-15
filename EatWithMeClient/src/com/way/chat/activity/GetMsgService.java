package com.way.chat.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.client.ClientInputThread;
import com.way.client.ClientOutputThread;
import com.way.client.MessageListener;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;

/**
 * 
 * 
 * @author way
 * 
 */
public class GetMsgService extends Service {
	private static final int MSG = 0x001;
	private MyApplication application;
	private Client client;
	private NotificationManager mNotificationManager;
	private boolean isStart = false;
	private Notification mNotification;
	private Context mContext = this;
	private SharePreferenceUtil util;
	private MessageDB messageDB;
	private BroadcastReceiver backKeyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Toast.makeText(context, “App is running background“, 0).show();
			setMsgNotification();
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG:
				int newMsgNum = application.getNewMsgNum();
				newMsgNum++;
				application.setNewMsgNum(newMsgNum);
				TranObject<TextMessage> textObject = (TranObject<TextMessage>) msg
						.getData().getSerializable("msg");
				// System.out.println(textObject);
				if (textObject != null) {
					int form = textObject.getFromUser();
					String content = textObject.getObject().getMessage();

					ChatMsgEntity entity = new ChatMsgEntity("",
							MyDate.getDateEN(), content, -1, true);
					messageDB.saveMsg(form, entity);

					
					int icon = R.drawable.notify_newmessage;
					CharSequence tickerText = form + ":" + content;
					long when = System.currentTimeMillis();
					mNotification = new Notification(icon, tickerText, when);
					mNotification.flags = Notification.FLAG_NO_CLEAR;					
					mNotification.defaults |= Notification.DEFAULT_SOUND;
					mNotification.defaults |= Notification.DEFAULT_VIBRATE;
					mNotification.contentView = null;

					Intent intent = new Intent(mContext,
							FriendListActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(
							mContext, 0, intent, 0);
					mNotification.setLatestEventInfo(mContext, util.getName()
							+ " (" + newMsgNum + "message(s))", content,
							contentIntent);
				}
				mNotificationManager.notify(Constants.NOTIFY_ID, mNotification);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		messageDB = new MessageDB(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BACKKEY_ACTION);
		registerReceiver(backKeyReceiver, filter);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		application = (MyApplication) this.getApplicationContext();
		client = application.getClient();
		application.setmNotificationManager(mNotificationManager);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		util = new SharePreferenceUtil(getApplicationContext(),
				Constants.SAVE_USER);
		isStart = client.start();
		application.setClientStart(isStart);
		System.out.println("client start:" + isStart);
		if (isStart) {
			ClientInputThread in = client.getClientInputThread();
			in.setMessageListener(new MessageListener() {

				@Override
				public void Message(TranObject msg) {
					// System.out.println("GetMsgService:" + msg);
					if (util.getIsStart()) {
						if (msg.getType() == TranObjectType.MESSAGE) {
							Message message = handler.obtainMessage();
							message.what = MSG;
							message.getData().putSerializable("msg", msg);
							handler.sendMessage(message);
						}
					} else {
						Intent broadCast = new Intent();
						broadCast.setAction(Constants.ACTION);
						broadCast.putExtra(Constants.MSGKEY, msg);
						sendBroadcast(broadCast);
					}
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (messageDB != null)
			messageDB.close();
		unregisterReceiver(backKeyReceiver);
		mNotificationManager.cancel(Constants.NOTIFY_ID);
		if (isStart) {
			ClientOutputThread out = client.getClientOutputThread();
			TranObject<User> o = new TranObject<User>(TranObjectType.LOGOUT);
			User u = new User();
			u.setId(Integer.parseInt(util.getId()));
			o.setObject(u);
			out.setMsg(o);
			out.setStart(false);
			client.getClientInputThread().setStart(false);
		}
		// Intent intent = new Intent(this, GetMsgService.class);
		// startService(intent);
	}

	/**
	 * 
	 */
	private void setMsgNotification() {
		int icon = R.drawable.notify;
		CharSequence tickerText = "";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.notify_view);
		contentView.setTextViewText(R.id.notify_name, util.getName());
		contentView.setTextViewText(R.id.notify_msg, "EatWithMe is running backgroud");
		contentView.setTextViewText(R.id.notify_time, MyDate.getDate());
		mNotification.contentView = contentView;

		Intent intent = new Intent(this, FriendListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(Constants.NOTIFY_ID, mNotification);
	}
}
