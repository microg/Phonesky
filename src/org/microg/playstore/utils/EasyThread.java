package org.microg.playstore.utils;

import android.util.Log;

public class EasyThread<T> {
	private State state = State.RUNNING;
	private T result;
	private Throwable throwable;
	private ResultCallback<T> resultCallback;

	private EasyThread() {
	}

	public static EasyThread<Void> start(final Runnable runnable) {
		final EasyThread<Void> thread = new EasyThread<Void>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
					thread.done(null);
				} catch (Throwable throwable) {
					thread.thrown(throwable);
				}
			}
		}).start();
		return thread;
	}

	public static <T> EasyThread<T> start(final ResultRunnable<T> runnable) {
		final EasyThread<T> thread = new EasyThread<T>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d("StoreTest", "before");
				try {
					thread.done(runnable.run());
				} catch (Throwable throwable) {
					thread.thrown(throwable);
				}
				Log.d("StoreTest", "after");
			}
		}).start();
		return thread;
	}

	private synchronized void done(T result) {
		state = State.DONE;
		this.result = result;
		notifyCallback();
	}

	public synchronized boolean isDone() {
		return state != State.RUNNING;
	}

	private void notifyCallback() {
		if ((state == State.DONE) && (resultCallback != null)) {
			resultCallback.done(result);
		} else if ((state == State.THROWN) && (resultCallback != null)) {
			resultCallback.thrown(throwable);
		}
		Log.d("StoreTest", "notified");
	}

	public synchronized void onResult(ResultCallback<T> resultCallback) {
		if (this.resultCallback != null) {
			throw new RuntimeException("Currently only one resultCallback is supported.");
		}
		Log.d("StoreTest", "resultlistener");
		this.resultCallback = resultCallback;
		notifyCallback();
	}

	private synchronized void thrown(Throwable throwable) {
		state = State.THROWN;
		this.throwable = throwable;
		notifyCallback();
	}

	private enum State {
		RUNNING, DONE, THROWN
	}

	public interface ResultCallback<T> {
		void done(T result);

		void thrown(Throwable t);
	}

	public interface ResultRunnable<T> {
		T run();
	}
}
