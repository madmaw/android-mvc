package com.mobile_develop.android.ui;

import java.util.concurrent.Executor;

import android.app.Activity;

public class AndroidUIThreadExecutor implements Executor {

	private Activity activity;
	
	public AndroidUIThreadExecutor(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void execute(final Runnable r) {
		activity.runOnUiThread(r);
	}

}
