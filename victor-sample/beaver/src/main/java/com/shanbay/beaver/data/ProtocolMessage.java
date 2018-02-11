package com.shanbay.beaver.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by chan on 2017/6/9.
 */

public class ProtocolMessage<T> {


	public static final int TYPE_MOTION_DOWN = 40;
	public static final int TYPE_MOTION_UP = 39;
	public static final int TYPE_MOTION_MOVE = 38;
	public static final int TYPE_HEART_BEAT = 37;
	public static final int TYPE_RESIZE = 36;
	public static final int TYPE_KEY_CODE = 35;

	public static final String KEY_TYPE = "type";

	@IntDef({TYPE_MOTION_DOWN, TYPE_MOTION_UP, TYPE_MOTION_MOVE, TYPE_HEART_BEAT, TYPE_RESIZE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Type {

	}

	@Type
	public int type;
	public T data;

	public ProtocolMessage() {
	}

	public ProtocolMessage(@Type int type, T data) {
		this.type = type;
		this.data = data;
	}
}
