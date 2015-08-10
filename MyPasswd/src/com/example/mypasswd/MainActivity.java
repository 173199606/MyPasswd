package com.example.mypasswd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mypasswd.lockview.MyLockView;
import com.example.mypasswd.lockview.MyLockView.OnSetPasswdListener;

public class MainActivity extends Activity implements OnSetPasswdListener{

	private MyLockView mylockview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.activity_main);
	       
	       mylockview = (MyLockView) findViewById(R.id.lockview);
	       mylockview.setOnSetPasswdListener(this);
	       
	       Intent intent = new Intent("com.example.mypasswd.Action.MyReceiver");
	       sendBroadcast(intent);
	   }

	@Override
	public void onPasswdEvent(boolean bSuccess) {
		System.out.println(bSuccess);
		if (bSuccess) {
			Toast.makeText(this, "success to set password!", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, "the two password is not the same, please try again!", Toast.LENGTH_LONG).show();
		}
	}



}
