package com.way.chat.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.client.ClientOutputThread;
import com.way.util.DialogFactory;
import com.way.util.Encode;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;

/**
 * Log In
 * 
 * @author way
 * 
 */
public class LoginActivity extends MyActivity implements OnClickListener {
	private Button mBtnRegister;
	private Button mBtnLogin;
	private EditText mAccounts, mPassword;
	private CheckBox mAutoSavePassword;
	private MyApplication application;

	private View mMoreView;
	private ImageView mMoreImage;
	private View mMoreMenuView;
	private MenuInflater mi;
	private boolean mShowMenu = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		application = (MyApplication) this.getApplicationContext();
		initView();
		mi = new MenuInflater(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNetworkAvailable()) {
			Intent service = new Intent(this, GetMsgService.class);
			startService(service);
		} else {
			toast(this);
		}
	}

	public void initView() {
		mAutoSavePassword = (CheckBox) findViewById(R.id.auto_save_password);
		mMoreView = findViewById(R.id.more);
		mMoreMenuView = findViewById(R.id.moremenu);
		mMoreImage = (ImageView) findViewById(R.id.more_image);
		mMoreView.setOnClickListener(this);

		mBtnRegister = (Button) findViewById(R.id.regist_btn);
		mBtnRegister.setOnClickListener(this);

		mBtnLogin = (Button) findViewById(R.id.login_btn);
		mBtnLogin.setOnClickListener(this);

		mAccounts = (EditText) findViewById(R.id.lgoin_accounts);
		mPassword = (EditText) findViewById(R.id.login_password);
		if (mAutoSavePassword.isChecked()) {
			SharePreferenceUtil util = new SharePreferenceUtil(
					LoginActivity.this, Constants.SAVE_USER);
			mAccounts.setText(util.getId());
			mPassword.setText(util.getPasswd());
		}
	}

	/**
	 * 
	 * 
	 * @param bShow
	 *            
	 */
	public void showMoreView(boolean bShow) {
		if (bShow) {
			mMoreMenuView.setVisibility(View.GONE);
			mMoreImage.setImageResource(R.drawable.login_more_up);
			mShowMenu = true;
		} else {
			mMoreMenuView.setVisibility(View.VISIBLE);
			mMoreImage.setImageResource(R.drawable.login_more);
			mShowMenu = false;
		}
	}

	/**
	 * 
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.more:
			showMoreView(!mShowMenu);
			break;
		case R.id.regist_btn:
			goRegisterActivity();
			break;
		case R.id.login_btn:
			login();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 */
	public void goRegisterActivity() {
		Intent intent = new Intent();
		intent.setClass(this, RegisterActivity.class);
		startActivity(intent);
	}

	/**
	 * 
	 */
	private Dialog mDialog = null;

	private void showRequestDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "Verifying account...");
		mDialog.show();
	}

	/**
	 * 
	 */
	private void login() {
		String accounts = mAccounts.getText().toString();
		String password = mPassword.getText().toString();
		if (accounts.length() == 0 || password.length() == 0) {
			DialogFactory.ToastDialog(this, "Login", "Acoount or password can't be empty");
		} else {
			showRequestDialog();
			if (application.isClientStart()) {
				Client client = application.getClient();
				ClientOutputThread out = client.getClientOutputThread();
				TranObject<User> o = new TranObject<User>(TranObjectType.LOGIN);
				User u = new User();
				u.setId(Integer.parseInt(accounts));
				u.setPassword(Encode.getEncode("MD5", password));
				o.setObject(u);
				out.setMsg(o);
			} else {
				if (mDialog.isShowing())
					mDialog.dismiss();
				DialogFactory.ToastDialog(LoginActivity.this, "Login",
						"Server is not open");
			}
		}
	}

	@Override
	public void getMessage(TranObject msg) {
		if (msg != null) {
			System.out.println("Login:" + msg);
			switch (msg.getType()) {
			case LOGIN:
				List<User> list = (List<User>) msg.getObject();
				if (list!=null&&list.size() > 0) {
					SharePreferenceUtil util = new SharePreferenceUtil(
							LoginActivity.this, Constants.SAVE_USER);
					util.setId(mAccounts.getText().toString());
					util.setPasswd(mPassword.getText().toString());
					util.setEmail(list.get(0).getEmail());
					util.setName(list.get(0).getName());
					util.setImg(list.get(0).getImg());

					UserDB db = new UserDB(LoginActivity.this);
					db.addUser(list);

					Intent i = new Intent(LoginActivity.this,
							FriendListActivity.class);
					i.putExtra(Constants.MSGKEY, msg);
					startActivity(i);

					if (mDialog.isShowing())
						mDialog.dismiss();
					finish();
					Toast.makeText(getApplicationContext(), "Login Successfully", 0).show();
				} else {
					DialogFactory.ToastDialog(LoginActivity.this, "Login",
							"Account or password wrong!");
					if (mDialog.isShowing())
						mDialog.dismiss();
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mi.inflate(R.menu.login_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_menu_setting:
			setDialog();
			break;
		case R.id.login_menu_exit:
			exitDialog(LoginActivity.this, "Hint", "Really wanna quit？");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		exitDialog(LoginActivity.this, "Hint", "Really wanna quit？");
	}

	/**
	 * 
	 * 
	 * @param context
	 *            
	 * @param title
	 *            
	 * @param msg
	 *            
	 */
	private void exitDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (application.isClientStart()) {				
							Intent service = new Intent(LoginActivity.this,
									GetMsgService.class);
							stopService(service);
						}
						close();
					}
				}).setNegativeButton("Cancel", null).create().show();
	}

	/**
	 * 
	 */
	private void setDialog() {
		final View view = LayoutInflater.from(LoginActivity.this).inflate(
				R.layout.setting_view, null);
		new AlertDialog.Builder(LoginActivity.this).setTitle("set ip, port")
				.setView(view)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 
						EditText ipEditText = (EditText) view
								.findViewById(R.id.setting_ip);
						EditText portEditText = (EditText) view
								.findViewById(R.id.setting_port);
						String ip = ipEditText.getText().toString();
						String port = portEditText.getText().toString();
						SharePreferenceUtil util = new SharePreferenceUtil(
								LoginActivity.this, Constants.IP_PORT);
						if (ip.length() > 0 && port.length() > 0) {
							util.setIp(ip);
							util.setPort(Integer.valueOf(port));
							Toast.makeText(getApplicationContext(),
									"Saved, restart to be affective", 0).show();
							finish();
						} else {
							Toast.makeText(getApplicationContext(),
									"ip or port can't be empty", 0).show();
						}
					}
				}).setNegativeButton("Cancel", null).create().show();
	}

	/**
	 * 
	 * 
	 * @param context
	 * @return
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager mgr = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	private void toast(Context context) {
		new AlertDialog.Builder(context)
				.setTitle("Hint")
				.setMessage("Network is not open")
				.setPositiveButton("Open",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								startActivity(intent);
							}
						}).setNegativeButton("Cancel", null).create().show();
	}
}
