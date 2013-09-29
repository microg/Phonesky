package org.microg.playstore;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class InAppBillingService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
