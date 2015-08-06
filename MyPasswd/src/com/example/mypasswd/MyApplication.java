package com.example.mypasswd;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class MyApplication extends Application {
	
	private boolean isAlreadyUnlock = false;
	private Drawable appIcon = null;
	
	public boolean isAlreadyUnlock() {
		return isAlreadyUnlock;
	}

	public void setAlreadyUnlock(boolean isAlreadyUnlock) {
		this.isAlreadyUnlock = isAlreadyUnlock;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
}
