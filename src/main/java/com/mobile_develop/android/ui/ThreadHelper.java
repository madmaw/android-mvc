package com.mobile_develop.android.ui;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class ThreadHelper {

    public static interface Handle {

    }

    private class TimeThread extends Thread {
        private class Entry implements Handle {
            private long timeMillis;
            private Runnable r;

            public Entry(Runnable r, long timeMillis) {
                this.r = r;
                this.timeMillis = timeMillis;
            }

            public long getTimeMillis() {
                return timeMillis;
            }

            public Runnable getRunnable() {
                return r;
            }
        }

        private List<Entry> entries;

        public TimeThread() {
            this.entries = new ArrayList<Entry>();
        }

        public Entry add(Runnable r, long timeMillis) {
            int index = this.entries.size();
            for( int i=0; i<this.entries.size(); i++ ) {
                Entry entry = this.entries.get(i);
                if( entry.getTimeMillis() > timeMillis ) {
                    index = i;
                    break;
                }
            }
            Entry entry = new Entry(r, timeMillis);
            this.entries.add(index, entry);
            if( index == 0 ) {
                ThreadHelper.this.notify();
            }
            return entry;
        }

        public void cancel(Handle handle) {
            int index = this.entries.indexOf(handle);
            this.entries.remove(index);
            if( index == 0 ) {
                ThreadHelper.this.notify();
            }
        }

        @Override
        public void run() {
            boolean done = false;
            while( !done ) {
                synchronized (ThreadHelper.this) {
                    if( entries.size() > 0 ) {
                        Entry entry = entries.get(0);
                        long now = System.currentTimeMillis();
                        long diff = entry.getTimeMillis() - now;
                        if( diff <= 0 ) {
                            // execute and remove
                            entries.remove(0);
                            entry.getRunnable().run();
                        } else {
                            try {
                                ThreadHelper.this.wait(diff);
                            } catch( InterruptedException ex ) {
                                // do nothing
                            }
                        }
                    } else {
                        done = true;
                        timeThread = null;
                    }
                }
            }
        }
    }

	private Activity activity;
	private Thread uiThread;
    private Executor expensiveExecutor;
    private TimeThread timeThread;
	
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

    public Handle threadAtTime(Runnable r, long timeMillis) {
        synchronized (this) {
            if( timeThread == null ) {
                timeThread = new TimeThread();
                timeThread.start();
            }
            return timeThread.add(r, timeMillis);
        }
    }

    public void cancel(Handle handle) {
        synchronized( this ) {
            if( timeThread != null ) {
                timeThread.cancel(handle);
            }
        }
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
