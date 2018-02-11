package com.shanbay.beaver.aop;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.shanbay.beaver.event.AppEvent;
import com.shanbay.beaver.service.BeaverService;

/**
 * Created by chan on 2017/6/6.
 */

public class BeaverAppAOP implements Application.ActivityLifecycleCallbacks {

	private static BeaverAppAOP sBeaverAppAOP;
	private int mPageCount = 0;
	private boolean mFirstUse = true;

	private BeaverAppAOP() {
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {
		++mPageCount;
		if (mPageCount != 1) {
			return;
		}

		if (mFirstUse) {
			mFirstUse = false;
			startBeaverService(activity, AppEvent.EVENT_FIRST_USE);
		} else {
			startBeaverService(activity, AppEvent.EVENT_RESUME);
		}
	}

	@Override
	public void onActivityResumed(Activity activity) {

	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {
		--mPageCount;
		if (mPageCount == 0) {
			startBeaverService(activity, AppEvent.EVENT_STOP);
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {

	}

	public static void enable(Context appContext, boolean enable) {

		Application application = null;
		if (appContext instanceof Application) {
			application = (Application) appContext;
		} else {
			application = (Application) appContext.getApplicationContext();
		}

		if (enable) {
			application.registerActivityLifecycleCallbacks(getInstance());
		} else {
			application.unregisterActivityLifecycleCallbacks(getInstance());
		}
	}

	private static BeaverAppAOP getInstance() {
		if (sBeaverAppAOP == null) {
			synchronized (BeaverAppAOP.class) {
				if (sBeaverAppAOP == null) {
					sBeaverAppAOP = new BeaverAppAOP();
				}
			}
		}
		return sBeaverAppAOP;
	}

	private static void startBeaverService(Activity activity, @AppEvent.AppEventTag int event) {
		Intent intent = new Intent(activity, BeaverService.class);
		AppEvent appEvent = new AppEvent();
		appEvent.event = event;
		intent.putExtra(BeaverService.KEY_EVENT, BeaverService.EVENT_CHECK_POINT);
		intent.putExtra(BeaverService.KEY_DATA, appEvent);
		activity.startService(intent);
	}
}
