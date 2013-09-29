package org.microg.playstore;

import android.accounts.*;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import com.google.android.AndroidContext;
import com.google.android.gsf.PrivacyExtension;
import com.google.play.DfeClient;
import com.google.play.DfeContext;
import com.google.tools.RequestContext;

import java.io.IOException;

public class StoreApplication extends Application {
	private static StoreApplication instance;
	private AndroidContext androidContext;
	private DfeContext dfeContext;
	private DfeClient dfeClient;
	private static final String TAG = "StoreApplication";

	public static StoreApplication getInstance() {
		assert instance != null;
		return instance;
	}

	public AndroidContext getAndroidContext() {
		return androidContext;
	}

	public DfeClient getDfeClient() {
		return dfeClient;
	}

	public DfeContext getDfeContext() {
		return dfeContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public void refreshContext() {
		androidContext = PrivacyExtension.getAndroidContext(this);
		dfeContext = new DfeContext(androidContext);
		dfeContext.put(RequestContext.KEY_CLIENT_ID, "am-google");
		dfeContext.put(RequestContext.KEY_FILTER_LEVEL, 3); // TODO
		dfeContext.put(RequestContext.KEY_SMALEST_SCREEN_WIDTH_DP, 384); // TODO
		dfeContext.put(RequestContext.KEY_HTTP_USER_AGENT, "Android-Finsky/4.0.25 (api=3,versionCode=80200025," +
														   "sdk=17,device=mako,hardware=mako,product=occam)"); // TODO
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.google");
		AccountManagerFuture<Bundle> accountManagerFuture =
				am.getAuthToken(accounts[0], "androidmarket", false, null, null);
		try {
			Bundle authTokenBundle = accountManagerFuture.getResult();
			dfeContext.put(RequestContext.KEY_AUTHORIZATION_TOKEN, authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN)); // TODO
		} catch (Exception e) {
			Log.w(TAG, e);
		}
		dfeClient = new DfeClient(dfeContext);
	}
}
