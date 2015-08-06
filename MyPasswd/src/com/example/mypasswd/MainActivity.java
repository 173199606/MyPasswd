package com.example.mypasswd;

import java.util.List;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.activity_main);
	       
	       
	       Intent intent = new Intent("com.example.mypasswd.Action.MyReceiver");
	       sendBroadcast(intent);
	       
	       
	      // finish();
	   }


}
