package com.example.mypasswd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	final String SYSTEM_DIALOG_REASON_KEY = "reason";
    final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("========= onReceiver, action::",intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals("com.example.mypasswd.Action.MyReceiver")) {
			Log.v("TAG", "开机自动服务自动启动.....");  
			Intent i = new Intent(context, MyService.class);
			context.startService(i);
			
			// start moniter service
			Intent i2 = new Intent(context, MyMoniterService.class);
			context.startService(i2);
		}

//		// can not work
//        if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//        	Log.i("xxj","ACTION_CLOSE_SYSTEM_DIALOGS");
//            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//            if (reason != null) {
//                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//                    // 短按home键
//                    Log.i("xxj","show home!");
//                }
//            }
//        }

	}
	
}
