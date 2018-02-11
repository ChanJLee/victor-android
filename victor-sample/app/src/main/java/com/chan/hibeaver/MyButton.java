package com.chan.hibeaver;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chan on 2017/6/9.
 */

public class MyButton extends View {
	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Log.d("chan_debug", "x: " + event.getRawX() + " y: " + event.getRawY());

		return true;
	}
}
