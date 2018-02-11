package com.shanbay.beaver.config;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.shanbay.beaver.service.BeaverService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by chan on 2017/6/6.
 */

public class BeaverConfig {

	public static final int MODE_NORMAL = BeaverService.MODE_NORMAL;
	public static final int MODE_ACQUISITION = BeaverService.MODE_ACQUISITION;


	@IntDef({MODE_NORMAL, MODE_ACQUISITION})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ConfigTag {

	}

	public boolean enableAppEvent = true;
	public boolean enableViewEvent = true;
	public boolean enablePageEvent = true;
	@ConfigTag
	public int mode = MODE_NORMAL;

	public String url;


	private BeaverConfig(boolean enableAppEvent, boolean enableViewEvent, boolean enablePageEvent, int mode, String url) {
		this.enableAppEvent = enableAppEvent;
		this.enableViewEvent = enableViewEvent;
		this.enablePageEvent = enablePageEvent;
		this.mode = mode;
		this.url = url;
	}

	public static class Builder {
		public boolean enableAppEvent = true;
		public boolean enableViewEvent = true;
		public boolean enablePageEvent = true;
		@ConfigTag
		public int mode = MODE_NORMAL;
		public String url;

		public Builder enableAppEvent(boolean enableAppEvent) {
			this.enableAppEvent = enableAppEvent;
			return this;
		}

		public Builder enableViewEvent(boolean enableViewEvent) {
			this.enableViewEvent = enableViewEvent;
			return this;
		}

		public Builder enablePageEvent(boolean enablePageEvent) {
			this.enablePageEvent = enablePageEvent;
			return this;
		}

		public Builder mode(@ConfigTag int mode) {
			this.mode = mode;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public BeaverConfig build() {

			if (mode != MODE_NORMAL && mode != MODE_ACQUISITION) {
				throw new IllegalArgumentException("illegal argument mode, value: " + mode);
			}

			if (TextUtils.isEmpty(url)) {
				throw new IllegalArgumentException("url must not be empty");
			}

			return new BeaverConfig(enableAppEvent, enableViewEvent, enablePageEvent, mode, url);
		}
	}
}
