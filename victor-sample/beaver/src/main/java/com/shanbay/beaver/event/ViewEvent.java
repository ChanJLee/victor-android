package com.shanbay.beaver.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chan on 2017/6/6.
 */

public class ViewEvent extends Event implements Parcelable {

	public String viewId;
	public String viewType;
	public String text;
	public String screenName;
	public String screenTitle;
	public String value;

	public ViewEvent() {
		super(Event.VIEW_TYPE);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.viewId);
		dest.writeString(this.viewType);
		dest.writeString(this.text);
		dest.writeString(this.screenName);
		dest.writeString(this.screenTitle);
		dest.writeString(this.value);
		dest.writeInt(this.type);
	}

	protected ViewEvent(Parcel in) {
		this.viewId = in.readString();
		this.viewType = in.readString();
		this.text = in.readString();
		this.screenName = in.readString();
		this.screenTitle = in.readString();
		this.value = in.readString();
		this.type = in.readInt();
	}

	public static final Creator<ViewEvent> CREATOR = new Creator<ViewEvent>() {
		@Override
		public ViewEvent createFromParcel(Parcel source) {
			return new ViewEvent(source);
		}

		@Override
		public ViewEvent[] newArray(int size) {
			return new ViewEvent[size];
		}
	};
}
