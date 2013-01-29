package com.mobile_develop.android.ui;

import android.app.Activity;
import android.util.Log;

import java.util.concurrent.Executor;

public class ThreadHelper {
	private Activity activity;
	private Thread uiThread;
    private Executor expensiveExecutor;
	
	public ThreadHelper(Activity activity, Executor expensiveExecutor) {
		this.activity = activity;
        this.expensiveExecutor = expensiveExecutor;
	}
	
	public ThreadHelper(Activity activity, Executor expensiveExecutor, Thread uiThread) {
		this(activity, expensiveExecutor);
		this.uiThread = uiThread;
	}
	
	public boolean isUIThread() {
		if( this.uiThread == null ) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					uiThread = Thread.currentThread();
				}
			};
			forceInvokeAndWait(r);
		} 
		Thread currentThread = Thread.currentThread();
		return this.uiThread == currentThread;
	}
	
	public void invoke(Runnable r) {
		this.activity.runOnUiThread(r);
	}
	
	public void invokeLater(final Runnable r) {
		if( isUIThread() ) {
			// thread then run
			Runnable later = new Runnable() {
				
				@Override
				public void run() {
					invoke(r);
				}
			};
			Thread thread = new Thread(later);
			thread.start();
		} else {
			invoke(r);
		}
	}
	
	public void invokeAndWait(Runnable r) {
		if( isUIThread() ) 
		{
			r.run();
		}
		else
		{
			forceInvokeAndWait(r);
		}
	}
	
	private void forceInvokeAndWait(Runnable r)
	{
		final Object lock = new Object(); 
		SecretRunnable secretRunnable = new SecretRunnable(r, lock);
		synchronized(lock)
		{
			invoke(secretRunnable);
			try
			{
				lock.wait();
			} catch( Exception ex ) {
				Log.w(getClass().getSimpleName(), "could not finish waiting", ex);
			}
			Throwable error = secretRunnable.getError();
			if( error != null ) {
				throw new RuntimeException("failed to execute asynchronous operation", error);
			}
		}		
	}

    public void threadExpensiveOperation(Runnable r) {
        // we only have one of these at a time
        expensiveExecutor.execute(r);
    }
	
	private class SecretRunnable implements Runnable {
		Runnable r;
		Object lock;
		Throwable error;
		
		public SecretRunnable(Runnable r, Object lock) {
			this.r = r;
			this.lock = lock;
		}

		@Override
		public void run() {
			synchronized(lock) {
				try {
					r.run();
				} catch( Throwable error ) {
					Log.e(getClass().getSimpleName(), "error in async method", error );
					this.error = error;
				}
				lock.notify();
			} 
		}
		
		public Throwable getError() {
			return this.error;
		}
	}
	
}
