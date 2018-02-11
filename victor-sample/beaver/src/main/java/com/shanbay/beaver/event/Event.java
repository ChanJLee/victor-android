package com.shanbay.beaver.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by chan on 2017/6/6.
 */

public abstract class Event {

    public static final int VIEW_TYPE = 0x01;
    public static final int PAGE_TYPE = 0x02;
    public static final int APP_TYPE = 0x04;
    public static final int DIALOG_VIEW_TYPE = 0x08;

    @EventTag
    public int type;

    @IntDef({VIEW_TYPE, PAGE_TYPE, APP_TYPE, DIALOG_VIEW_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventTag {

    }

    public Event(@EventTag int type) {
        this.type = type;
    }

    public Event() {
    }
}
