package com.example.mypasswd.lockview;

import java.util.List;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class Utils {
	private static Utils instance = null;
	static Context mContext = null;
	
//	final static String bg_color = "#000";
//	final static String default_circle = "#FFFFFF";
//	final static String small_right_circle = "#00A0E5";
//	final static String big_right_circle = "#ADD5E6";
//	final static String small_wrong_circle = "#F7564A";
//	final static String big_wrong_circle = "#EDACA7";
	
	
	public static Utils getInstance(Context context) {
		
		if (instance == null) {
			mContext = context;
			instance = new Utils();
		}
		
		return instance;
	}

	
	public int dp2px(int dp) {
		return (int)mContext.getResources().getDisplayMetrics().density * dp;
	}
	
	public int getScreenWidth() {
		int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
		int heightPixels = mContext.getResources().getDisplayMetrics().heightPixels;
		return widthPixels < heightPixels ? widthPixels:heightPixels;
	}
	
	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
	 * @return true代表正在运行，false代表服务没有正在运行
	 * 
	 * note: 该函数被循环调用，所以将变量定义在函数体外，否则内存消耗太大，垃圾回收太平凡
	 */
	ActivityManager myAM = (ActivityManager) mContext
			.getSystemService(Context.ACTIVITY_SERVICE);
	List<RunningServiceInfo> myList;
	String mName;
	public boolean isServiceWork(String serviceName) {
		myList = myAM.getRunningServices(200);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
	
}
