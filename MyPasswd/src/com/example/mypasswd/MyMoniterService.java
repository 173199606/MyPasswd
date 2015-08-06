package com.example.mypasswd;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.mypasswd.lockview.Utils;

public class MyMoniterService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//如果MyService被杀，则重启之
		new Thread(){
			public void run() {
				while (true) {
					if (!Utils.getInstance(MyMoniterService.this).isServiceWork("com.example.mypasswd.MyService")) {
						Log.i("xxj", "MyService stopped, starting it now");
						Intent intent = new Intent("com.example.mypasswd.Action.MyService");
						MyMoniterService.this.startService(intent);
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
		
		
		return Service.START_REDELIVER_INTENT;
	}
	
	 

}
