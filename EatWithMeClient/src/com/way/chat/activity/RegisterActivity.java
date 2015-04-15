package com.way.chat.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.client.Client;
import com.way.client.ClientOutputThread;
import com.way.util.DialogFactory;
import com.way.util.Encode;

public class RegisterActivity extends MyActivity implements OnClickListener {

	private Button mBtnRegister;
	private Button mRegBack;
	private EditText mEmailEt, mNameEt, mPasswdEt, mPasswdEt2;
	
	private MyApplication application;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		application = (MyApplication) this.getApplicationContext();
		initView();
	}

	public void initView() {
		mBtnRegister = (Button) findViewById(R.id.register_btn);
		mRegBack = (Button) findViewById(R.id.reg_back_btn);
		mBtnRegister.setOnClickListener(this);
		mRegBack.setOnClickListener(this);

		mEmailEt = (EditText) findViewById(R.id.reg_email);
		mNameEt = (EditText) findViewById(R.id.reg_name);
		mPasswdEt = (EditText) findViewById(R.id.reg_password);
		mPasswdEt2 = (EditText) findViewById(R.id.reg_password2);

	}

	private Dialog mDialog = null;

	private void showRequestDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "Registering...");
		mDialog.show();
	}

	@Override
	public void onBackPressed() {
		toast(RegisterActivity.this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.register_btn:
			// showRequestDialog();
			regist();
			break;
		case R.id.reg_back_btn:
			toast(RegisterActivity.this);
			break;
		default:
			break;
		}
	}

	private void toast(Context context) {
		new AlertDialog.Builder(context).setTitle("Register")
				.setMessage("Quit registering？")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setNegativeButton("Cancel", null).create().show();
	}

	private void regist() {
		String email = mEmailEt.getText().toString();
		String name = mNameEt.getText().toString();
		String passwd = mPasswdEt.getText().toString();
		String passwd2 = mPasswdEt2.getText().toString();
		if (email.equals("") || name.equals("") || passwd.equals("")
				|| passwd2.equals("")) {
			DialogFactory.ToastDialog(RegisterActivity.this, "Register",
					"Options with * is required.");
		} else {
			if (passwd.equals(passwd2)) {
				showRequestDialog();
				
				if (application.isClientStart()) {
					Client client = application.getClient();
//					Client client = GetMsgService.client;
					ClientOutputThread out = client.getClientOutputThread();
					TranObject<User> o = new TranObject<User>(
							TranObjectType.REGISTER);
					User u = new User();
					u.setEmail(email);
					u.setName(name);
					u.setPassword(Encode.getEncode("MD5", passwd));
					o.setObject(u);
					out.setMsg(o);
				} else {
					if (mDialog.isShowing())
						mDialog.dismiss();
					DialogFactory.ToastDialog(this, "Register", "Server is not open");
				}

			} else {
				DialogFactory.ToastDialog(RegisterActivity.this, "Register",
						"Two passwords not match.");
			}
		}
	}

	@Override
	public void getMessage(TranObject msg) {
		// TODO Auto-generated method stub
		switch (msg.getType()) {
		case REGISTER:
			User u = (User) msg.getObject();
			int id = u.getId();
			if (id > 0) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}
				DialogFactory.ToastDialog(RegisterActivity.this, "Register",
						"Please remember your account：" + id);
			} else {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}
				DialogFactory.ToastDialog(RegisterActivity.this, "Register",
						"Sorry, no account available.");
			}
			break;

		default:
			break;
		}
	}
}
