package com.way.chat.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.ClientInputThread;
import com.way.client.ClientOutputThread;
import com.way.client.MessageListener;
import com.way.util.GroupFriend;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;

/**
 * FriendList Activity
 * 
 * @author way
 * 
 */
public class FriendListActivity extends MyActivity implements OnClickListener {

	private static final int PAGE1 = 0;
	private static final int PAGE2 = 1;
	private static final int PAGE3 = 2;
	private List<GroupFriend> group;
	//private String[] groupName = { "My friends", "My classmates", "My family" };
	private String[] groupName = { "My friends" };
	private SharePreferenceUtil util;
	private UserDB userDB;
	private MessageDB messageDB;

	private MyListView myListView;
	private MyExAdapter myExAdapter;

	private ListView mRecentListView;
	private int newNum = 0;

	private ListView mGroupListView;

	private ViewPager mPager;
	private List<View> mListViews;
	private LinearLayout layout_body_activity;
	private ImageView img_recent_chat;
	private ImageView img_friend_list;
	private ImageView img_group_friend;

	private ImageView myHeadImage;
	private TextView myName;

	private ImageView cursor;

	private int currentIndex = PAGE2; 
	private int offset = 0;
	private int bmpW;

	private TranObject msg;
	private List<User> list;
	private MenuInflater mi;
	private int[] imgs = { R.drawable.icon, R.drawable.f1, R.drawable.f2,
			R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6,
			R.drawable.f7, R.drawable.f8, R.drawable.f9 };
	private MyApplication application;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_list);
		application = (MyApplication) this.getApplicationContext();
		initData();
		initImageView();
		initUI();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		newNum = application.getRecentNum();
		if (!application.isClientStart()) {
			Intent service = new Intent(this, GetMsgService.class);
			startService(service);
		}
		new SharePreferenceUtil(this, Constants.SAVE_USER).setIsStart(false);
		NotificationManager manager = application.getmNotificationManager();
		if (manager != null) {
			manager.cancel(Constants.NOTIFY_ID);
			application.setNewMsgNum(0);
			application.getmRecentAdapter().notifyDataSetChanged();
		}
		super.onResume();
	}


	private void initData() {
		userDB = new UserDB(FriendListActivity.this);
		messageDB = new MessageDB(this);
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);

		msg = (TranObject) getIntent().getSerializableExtra(Constants.MSGKEY);
		if (msg == null) {
			list = userDB.getUser();
		} else {
			list = (List<User>) msg.getObject();
			userDB.updateUser(list);
		}
		initListViewData(list);
	}

	/**
	 * 
	 * 
	 * @param list
	 *            
	 */
	private void initListViewData(List<User> list) {
		group = new ArrayList<GroupFriend>();
		for (int i = 0; i < groupName.length; ++i) {
			List<User> child = new ArrayList<User>();
			GroupFriend groupInfo = new GroupFriend(groupName[i], child);
			for (User u : list) {
				if (u.getGroup() == i)
					child.add(u);
			}
			group.add(groupInfo);
		}
	}

	/**
	 * 
	 */
	private void initImageView() {
		cursor = (ImageView) findViewById(R.id.tab2_bg);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.topbar_select).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset * 3 + bmpW, 0);
		cursor.setImageMatrix(matrix);
	}

	private void initUI() {
		mi = new MenuInflater(this);
		layout_body_activity = (LinearLayout) findViewById(R.id.bodylayout);

		img_recent_chat = (ImageView) findViewById(R.id.tab1);
		img_recent_chat.setOnClickListener(this);
		img_friend_list = (ImageView) findViewById(R.id.tab2);
		img_friend_list.setOnClickListener(this);

		myHeadImage = (ImageView) findViewById(R.id.friend_list_myImg);
		myName = (TextView) findViewById(R.id.friend_list_myName);

		cursor = (ImageView) findViewById(R.id.tab2_bg);

		myHeadImage.setImageResource(imgs[list.get(0).getImg()]);
		myName.setText(list.get(0).getName());
		layout_body_activity.setFocusable(true);

		mPager = (ViewPager) findViewById(R.id.viewPager);
		mListViews = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(this);
		View lay1 = inflater.inflate(R.layout.tab1, null);
		View lay2 = inflater.inflate(R.layout.tab2, null);
		//View lay3 = inflater.inflate(R.layout.tab3, null);
		mListViews.add(lay1);
		mListViews.add(lay2);
		//mListViews.add(lay3);
		mPager.setAdapter(new MyPagerAdapter(mListViews));
		mPager.setCurrentItem(PAGE2);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mRecentListView = (ListView) lay1.findViewById(R.id.tab1_listView);
		// mRecentAdapter = new RecentChatAdapter(FriendListActivity.this,
		// application.getmRecentList());
		mRecentListView.setAdapter(application.getmRecentAdapter());

		
		myListView = (MyListView) lay2.findViewById(R.id.tab2_listView);
		myExAdapter = new MyExAdapter(this, group);
		myListView.setAdapter(myExAdapter);
		myListView.setGroupIndicator(null);
		myListView.setDivider(null);
		myListView.setFocusable(true);
		myListView.setonRefreshListener(new MyRefreshListener());

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tab1:
			mPager.setCurrentItem(PAGE1);
			break;
		case R.id.tab2:
			mPager.setCurrentItem(PAGE2);
			break;
		//case R.id.tab3:
			//		mPager.setCurrentItem(PAGE3);
			//break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mi.inflate(R.menu.friend_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (messageDB != null)
			messageDB.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.friend_menu_add:
			
			list = userDB.getUser();
			if (list.get(0).getIsOnline() == 0)
				list.get(0).setIsOnline(1);
			else
				list.get(0).setIsOnline(0);
				userDB.updateUser(list);
			initListViewData(list);
			myListView.setAdapter(myExAdapter);
			myExAdapter.updata(group);
			
			//Toast.makeText(getApplicationContext(), "This feature not open yet", 0).show();
			break;
		case R.id.friend_menu_exit:
			exitDialog(FriendListActivity.this, "Hint", "Really wanna quit？");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void exitDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (application.isClientStart()) {
							Intent service = new Intent(
									FriendListActivity.this,
									GetMsgService.class);
							stopService(service);
						}
						close();
					}
				}).setNegativeButton("Cancel", null).create().show();
	}

	@Override
	public void getMessage(TranObject msg) {
		// TODO Auto-generated method stub
		switch (msg.getType()) {
		case MESSAGE:
			newNum++;
			application.setRecentNum(newNum);
			TextMessage tm = (TextMessage) msg.getObject();
			String message = tm.getMessage();
			ChatMsgEntity entity = new ChatMsgEntity("", MyDate.getDateEN(),
					message, -1, true);
			messageDB.saveMsg(msg.getFromUser(), entity);
			Toast.makeText(FriendListActivity.this,
					"New message " + msg.getFromUser() + ":" + message, 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			User user2 = userDB.selectInfo(msg.getFromUser());
			RecentChatEntity entity2 = new RecentChatEntity(msg.getFromUser(),
					user2.getImg(), newNum, user2.getName(), MyDate.getDate(),
					message);
			application.getmRecentAdapter().remove(entity2);
			application.getmRecentList().addFirst(entity2);
			application.getmRecentAdapter().notifyDataSetChanged();
			break;
		case LOGIN:
			User loginUser = (User) msg.getObject();
			Toast.makeText(FriendListActivity.this,
					"Dear！" + loginUser.getId() + "is live!", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			break;
		case LOGOUT:
			User logoutUser = (User) msg.getObject();
			Toast.makeText(FriendListActivity.this,
					"Dear！" + logoutUser.getId() + "is offline!", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.setAction(Constants.BACKKEY_ACTION);
		sendBroadcast(i);

		util.setIsStart(true);
		finish();
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;

		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = null;
			switch (arg0) {
			case PAGE1:
				if (currentIndex == PAGE2) {
					animation = new TranslateAnimation(0, -one, 0, 0);
				} else if (currentIndex == PAGE3) {
					animation = new TranslateAnimation(one, -one, 0, 0);
				}
				break;
			case PAGE2:
				if (currentIndex == PAGE1) {
					animation = new TranslateAnimation(-one, 0, 0, 0);
				} else if (currentIndex == PAGE3) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				}
				break;
			default:
				break;
			}
			currentIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * 
	 * 
	 * @author way
	 * 
	 */
	public class MyRefreshListener implements MyListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			new AsyncTask<Void, Void, Void>() {
				List<User> list;

				protected Void doInBackground(Void... params) {
					if (application.isClientStart()) {
						ClientOutputThread out = application.getClient()
								.getClientOutputThread();
						TranObject o = new TranObject(TranObjectType.REFRESH);
						o.setFromUser(Integer.parseInt(util.getId()));
						out.setMsg(o);
						ClientInputThread in = application.getClient()
								.getClientInputThread();
						in.setMessageListener(new MessageListener() {

							@Override
							public void Message(TranObject msg) {
								// TODO Auto-generated method stub
								if (msg != null
										&& msg.getType() == TranObjectType.REFRESH) {
									list = (List<User>) msg.getObject();
									if (list.size() > 0) {
										// System.out.println("Friend:" + list);
										initListViewData(list);
										myExAdapter.updata(group);
										userDB.updateUser(list);
									}
								}
							}
						});
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					myExAdapter.notifyDataSetChanged();
					myListView.onRefreshComplete();
					Toast.makeText(FriendListActivity.this, "refreshing completed", 0).show();
				}

			}.execute((Void)null);
		}
	}
}
