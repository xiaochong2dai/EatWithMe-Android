package com.way.chat.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.client.ClientOutputThread;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity
 * 
 * @author way
 */
public class ChatActivity extends MyActivity implements OnClickListener {
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private TextView mFriendName;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;// Message Adapter
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private SharePreferenceUtil util;
	private User user;
	private MessageDB messageDB;
	private MyApplication application;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat);
		application = (MyApplication) getApplicationContext();
		messageDB = new MessageDB(this);
		user = (User) getIntent().getSerializableExtra("user");
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		initView();
		initData();
	}

	/**
	 * 
	 */
	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.chat_send);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.chat_back);
		mBtnBack.setOnClickListener(this);
		mFriendName = (TextView) findViewById(R.id.chat_name);
		mFriendName.setText(util.getName());
		mEditTextContent = (EditText) findViewById(R.id.chat_editmessage);
	}

	/**
	 * 
	 */
	public void initData() {
		List<ChatMsgEntity> list = messageDB.getMsg(user.getId());
		if (list.size() > 0) {
			for (ChatMsgEntity entity : list) {
				if (entity.getName().equals("")) {
					entity.setName(user.getName());
				}
				if (entity.getImg() < 0) {
					entity.setImg(user.getImg());
				}
				mDataArrays.add(entity);
			}
			Collections.reverse(mDataArrays);
		}
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(mAdapter.getCount() - 1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		messageDB.close();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chat_send:
			send();
			break;
		case R.id.chat_back:
			finish();
			break;
		}
	}

	/**
	 * 
	 */
	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setName(util.getName());
			entity.setDate(MyDate.getDateEN());
			entity.setMessage(contString);
			entity.setImg(util.getImg());
			entity.setMsgType(false);

			messageDB.saveMsg(user.getId(), entity);

			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			mListView.setSelection(mListView.getCount() - 1);
			MyApplication application = (MyApplication) this
					.getApplicationContext();
			Client client = application.getClient();
			ClientOutputThread out = client.getClientOutputThread();
			if (out != null) {
				TranObject<TextMessage> o = new TranObject<TextMessage>(
						TranObjectType.MESSAGE);
				TextMessage message = new TextMessage();
				message.setMessage(contString);
				o.setObject(message);
				o.setFromUser(Integer.parseInt(util.getId()));
				o.setToUser(user.getId());
				out.setMsg(o);
			}
		
			RecentChatEntity entity1 = new RecentChatEntity(user.getId(),
					user.getImg(), 0, user.getName(), MyDate.getDate(),
					contString);
			application.getmRecentList().remove(entity1);
			application.getmRecentList().addFirst(entity1);
			application.getmRecentAdapter().notifyDataSetChanged();
		}
	}

	@Override
	public void getMessage(TranObject msg) {
		// TODO Auto-generated method stub
		switch (msg.getType()) {
		case MESSAGE:
			TextMessage tm = (TextMessage) msg.getObject();
			String message = tm.getMessage();
			ChatMsgEntity entity = new ChatMsgEntity(user.getName(),
					MyDate.getDateEN(), message, user.getImg(), true);
			if (msg.getFromUser() == user.getId() || msg.getFromUser() == 0) {

				messageDB.saveMsg(user.getId(), entity);

				mDataArrays.add(entity);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);
				MediaPlayer.create(this, R.raw.msg).start();
			} else {
				messageDB.saveMsg(msg.getFromUser(), entity);
				Toast.makeText(ChatActivity.this,
						"You have message from：" + msg.getFromUser() + ":" + message, 0)
						.show();
				MediaPlayer.create(this, R.raw.msg).start();
			}
			break;
		case LOGIN:
			User loginUser = (User) msg.getObject();
			Toast.makeText(ChatActivity.this, loginUser.getId() + "is live", 0)
					.show();
			MediaPlayer.create(this, R.raw.msg).start();
			break;
		case LOGOUT:
			User logoutUser = (User) msg.getObject();
			Toast.makeText(ChatActivity.this, logoutUser.getId() + "is offline", 0)
					.show();
			MediaPlayer.create(this, R.raw.msg).start();
			break;
		default:
			break;
		}
	}
}