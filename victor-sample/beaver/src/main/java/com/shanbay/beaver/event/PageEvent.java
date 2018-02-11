package com.shanbay.beaver.event;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by chan on 2017/6/6.
 */

public class PageEvent extends Event implements Parcelable {

	public static final int EVENT_ENTER = 0x01;
	public static final int EVENT_EXIT = 0x02;

	@PageEventTag
	public int event;
	public String screenName;
	public String screenTitle;

	@IntDef({EVENT_ENTER, EVENT_EXIT})
	@Retention(RetentionPolicy.SOURCE)
	public @interface PageEventTag {

	}

	public PageEvent() {
		super(Event.PAGE_TYPE);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.event);
		dest.writeString(this.screenName);
		dest.writeString(this.screenTitle);
		dest.writeInt(this.type);
	}

	protected PageEvent(Parcel in) {
		this.event = in.readInt();
		this.screenName = in.readString();
		this.screenTitle = in.readString();
		this.type = in.readInt();
	}

	public static final Creator<PageEvent> CREATOR = new Creator<PageEvent>() {
		@Override
		public PageEvent createFromParcel(Parcel source) {
			return new PageEvent(source);
		}

		@Override
		public PageEvent[] newArray(int size) {
			return new PageEvent[size];
		}
	};
}
