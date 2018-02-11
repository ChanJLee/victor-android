package com.shanbay.beaver;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.shanbay.beaver.aop.BeaverAppAOP;
import com.shanbay.beaver.aop.BeaverPageAOP;
import com.shanbay.beaver.aop.BeaverViewAOP;
import com.shanbay.beaver.config.BeaverConfig;
import com.shanbay.beaver.service.BeaverService;

/**
 * Created by chan on 2017/6/6.
 */

public class ShanbayBeaver {
	private static ShanbayBeaver sShanbayBeaver;
	private Context mContext;

	private ShanbayBeaver(Context context) {
		mContext = context;
	}

	/**
	 * 初始化配置
	 *
	 * @param appContext
	 */
	public static void init(Context appContext, BeaverConfig beaverConfig) {
		if (sShanbayBeaver == null) {
			synchronized (ShanbayBeaver.class) {
				if (sShanbayBeaver == null) {
					sShanbayBeaver = new ShanbayBeaver(appContext);
					sShanbayBeaver.setBeaverConfig(beaverConfig);
				}
			}
		}
	}

	/**
	 * 当没有调用init方法的时候返回的是为null
	 *
	 * @return 返回ShanbayBeaver的实例
	 */
	@Nullable
	public static ShanbayBeaver getIntance() {
		return sShanbayBeaver;
	}

	public void setBeaverConfig(BeaverConfig beaverConfig) {
		BeaverAppAOP.enable(mContext, beaverConfig.enableAppEvent);
		BeaverViewAOP.enable(beaverConfig.enableViewEvent);
		BeaverPageAOP.enable(mContext, beaverConfig.enablePageEvent);

		Intent intent = new Intent(mContext, BeaverService.class);
		intent.putExtra(BeaverService.KEY_EVENT, BeaverService.EVENT_MODE_CHANGE);
		intent.putExtra(BeaverService.KEY_DATA, beaverConfig.url);
		intent.putExtra(BeaverService.KEY_MODE, beaverConfig.mode);
		mContext.startService(intent);
	}
}
