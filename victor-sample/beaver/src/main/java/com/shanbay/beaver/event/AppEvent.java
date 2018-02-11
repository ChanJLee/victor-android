package com.shanbay.beaver.event;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by chan on 2017/6/6.
 */

public class AppEvent extends Event implements Parcelable {

	public static final int EVENT_RESUME = 0x01;
	public static final int EVENT_STOP = 0x02;
	public static final int EVENT_FIRST_USE = 0x04;

	@AppEventTag
	public int event;

	@IntDef({EVENT_RESUME, EVENT_STOP, EVENT_FIRST_USE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface AppEventTag {

	}

	public AppEvent() {
		super(Event.APP_TYPE);
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.event);
		dest.writeInt(this.type);
	}

	protected AppEvent(Parcel in) {
		this.event = in.readInt();
		this.type = in.readInt();
	}

	public static final Creator<AppEvent> CREATOR = new Creator<AppEvent>() {
		@Override
		public AppEvent createFromParcel(Parcel source) {
			return new AppEvent(source);
		}

		@Override
		public AppEvent[] newArray(int size) {
			return new AppEvent[size];
		}
	};
}
