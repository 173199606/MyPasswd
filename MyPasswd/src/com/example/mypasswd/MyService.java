package com.example.mypasswd;

import java.util.ArrayList;
import java.util.List;

import com.example.mypasswd.lockview.Utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("xxj","MyService created!");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("xxj", "onStartCommand");
		new Thread() {
			public void run() {
				/** ��ȡϵͳ���� ActivityManager */
				ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

				while (true) {
					/** ��ȡ��ǰ�������е�����ջ�б� Խ�ǿ�����ǰ���е�����ջ�ᱻ���ڵ�һλ��֮����Դ����� */
					List<RunningTaskInfo> runningTasks = manager
							.getRunningTasks(1);

					/** ��õ�ǰ��˵�����ջ����ǰ̨����ջ */
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);

					/** ��ȡǰ̨����ջ����� Activity */
					ComponentName topActivity = runningTaskInfo.topActivity;

					/** ��ȡӦ�õİ��� */
					String packageName = topActivity.getPackageName();

					if (packageName.equals("com.eg.android.AlipayGphone")) {
						if (!((MyApplication) getApplication())
								.isAlreadyUnlock()) {
							((MyApplication) getApplication()).setAppIcon(getAppIcon(packageName));
							Intent intent = new Intent(MyService.this,
									MyGesturePasswdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
							startActivity(intent);
							
							
						}
					} else if (!packageName.equals("com.example.mypasswd")) {
						((MyApplication) getApplication())
								.setAlreadyUnlock(false);
					}

					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
		//���MyMoniterService��ɱ��������֮
		new Thread(){
			public void run() {
				while (true) {
					if (!Utils.getInstance(MyService.this).isServiceWork("com.example.mypasswd.MyMoniterService")) {
						Log.i("xxj", "MyService stopped, starting it now");
						Intent intent = new Intent("com.example.mypasswd.Action.MyMoniterService");
						MyService.this.startService(intent);
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

		// ����������Խ���ӦAP��ADJ��ʱ��ߵ�2
		startForeground(1, new Notification());

		return Service.START_REDELIVER_INTENT;
	}

	private Drawable getAppIcon(String packagename) {
		PackageManager pm = this.getPackageManager();    ; 
		try {    
            ApplicationInfo info = pm.getApplicationInfo(packagename, 0);     
            return info.loadIcon(pm);    
       } catch (NameNotFoundException e) {    
           // TODO Auto-generated catch block    
           e.printStackTrace();    
             
       }  

		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("xxj", "MyService onDestroy");
		Intent intent = new Intent("com.example.mypasswd.Action.MyService");
		sendBroadcast(intent);
	}

}
