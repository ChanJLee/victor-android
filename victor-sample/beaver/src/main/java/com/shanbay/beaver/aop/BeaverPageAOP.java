package com.shanbay.beaver.aop;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.shanbay.beaver.event.PageEvent;
import com.shanbay.beaver.service.BeaverService;
import com.shanbay.beaver.utils.AOPUtil;

/**
 * Created by chan on 2017/6/7.
 */

public class BeaverPageAOP implements Application.ActivityLifecycleCallbacks {

	private static BeaverPageAOP sBeaverPageAOP;

	private BeaverPageAOP() {
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		startBeaverService(activity, PageEvent.EVENT_ENTER);
	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {

	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		startBeaverService(activity, PageEvent.EVENT_EXIT);
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

	private static BeaverPageAOP getInstance() {
		if (sBeaverPageAOP == null) {
			synchronized (BeaverPageAOP.class) {
				if (sBeaverPageAOP == null) {
					sBeaverPageAOP = new BeaverPageAOP();
				}
			}
		}

		return sBeaverPageAOP;
	}

	private static void startBeaverService(Activity activity, @PageEvent.PageEventTag int event) {
		Intent intent = new Intent(activity, BeaverService.class);
		PageEvent pageEvent = new PageEvent();
		pageEvent.screenName = activity.getClass().getCanonicalName();
		pageEvent.screenTitle = AOPUtil.getActivityTitle(activity);
		pageEvent.event = event;
		intent.putExtra(BeaverService.KEY_EVENT, BeaverService.EVENT_CHECK_POINT);
		intent.putExtra(BeaverService.KEY_DATA, pageEvent);
		activity.startService(intent);
	}
}
