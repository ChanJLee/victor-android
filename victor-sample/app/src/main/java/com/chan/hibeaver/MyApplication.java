package com.chan.hibeaver;

import android.app.Application;

import com.shanbay.beaver.ShanbayBeaver;
import com.shanbay.beaver.config.BeaverConfig;

/**
 * Created by chan on 2017/6/7.
 */

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		BeaverConfig.Builder builder = new BeaverConfig.Builder();
		builder.mode(BeaverConfig.MODE_ACQUISITION);
		builder.url("shanbay://localhost:56790/acquisition");
		ShanbayBeaver.init(this, builder.build());
	}
}
