package com.shanbay.beaver.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chan on 17/6/28.
 */

public class DialogViewEvent extends Event implements Parcelable {

    public String viewType;
    public String text;
    public String screenName;
    public String screenTitle;
    public String value;

    public DialogViewEvent() {
        super(DIALOG_VIEW_TYPE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.viewType);
        dest.writeString(this.text);
        dest.writeString(this.screenName);
        dest.writeString(this.screenTitle);
        dest.writeString(this.value);
        dest.writeInt(this.type);
    }

    protected DialogViewEvent(Parcel in) {
        this.viewType = in.readString();
        this.text = in.readString();
        this.screenName = in.readString();
        this.screenTitle = in.readString();
        this.value = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<DialogViewEvent> CREATOR = new Parcelable.Creator<DialogViewEvent>() {
        @Override
        public DialogViewEvent createFromParcel(Parcel source) {
            return new DialogViewEvent(source);
        }

        @Override
        public DialogViewEvent[] newArray(int size) {
            return new DialogViewEvent[size];
        }
    };
}
