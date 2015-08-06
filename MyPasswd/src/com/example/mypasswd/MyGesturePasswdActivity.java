package com.example.mypasswd;

import com.example.mypasswd.lockview.MyLockView;
import com.example.mypasswd.lockview.MyLockView.OnGestureLockListener;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyGesturePasswdActivity extends Activity implements OnGestureLockListener {
	private ImageView imageView;
	private MyLockView mylockview;
	private TextView tv_msg;
	private ViewStub passwd_sub;  
	View inflated_view ;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mygesturepasswd);
		mylockview = (MyLockView)findViewById(R.id.mylockview);
		imageView = (ImageView)findViewById(R.id.iv_appicon);
		tv_msg = (TextView)findViewById(R.id.tv_msg);
		passwd_sub = (ViewStub) findViewById(R.id.stub_passwd); 
		
		mylockview.setOnGestureLockListener(this);
		imageView.setImageDrawable(((MyApplication) getApplication()).getAppIcon());
		
	}
	
	@Override
	public void onBackPressed() {
		
//		super.onBackPressed();
//		ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);       
//		manager.killBackgroundProcesses("com.eg.android.AlipayGphone");    
	}
	
	@Override
	public void onLockEvent(int lockEvent, int times) {
		switch (lockEvent) {
		case MyLockView.UNLOCKED:
			((MyApplication)getApplication()).setAlreadyUnlock(true);
			finish();
			break;
		case MyLockView.UNLOCK_FAILED:
			if (times > 0) {
				tv_msg.setText(String.format("解锁失败，你还有%d次机会", times));
			} else {
				tv_msg.setText(String.format("手势解锁失败"));
				inflated_view = passwd_sub.inflate();
			}
			
			break;

		default:
			break;
		}
		
	}
	
	public void onClick(View v) {
		EditText et = (EditText) inflated_view.findViewById(R.id.et_passwd);  
		if (et.getText().toString().equals("841126")) {
			((MyApplication)getApplication()).setAlreadyUnlock(true);
			finish();
		} else {
			Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show();
		}

	}
}
