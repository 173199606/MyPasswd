package com.example.mypasswd;

import android.app.Activity;
import android.app.UiAutomation.OnAccessibilityEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MyPasswdActivity extends Activity {
	private EditText et_passwd;
	private ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypasswd);
		et_passwd = (EditText)findViewById(R.id.et_passwd);
		imageView = (ImageView)findViewById(R.id.iv_appicon);
		
		//imageView.setBackgroundDrawable(((MyApplication) getApplication()).getAppIcon());
		imageView.setImageDrawable(((MyApplication) getApplication()).getAppIcon());
		
	}
	
	public void onLogin(View v) {
		if (et_passwd.getText().toString().equals("2486579")) {
			((MyApplication)getApplication()).setAlreadyUnlock(true);
			finish();
		} else {
			Toast.makeText(this, "√‹¬Î¥ÌŒÛ", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onBackPressed() {
		
	}
}
