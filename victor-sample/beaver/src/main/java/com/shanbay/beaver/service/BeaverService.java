package com.shanbay.beaver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.shanbay.beaver.event.Event;
import com.shanbay.beaver.service.sender.AbstractBeaverSender;
import com.shanbay.beaver.service.sender.BeaverSenderBuilder;

/**
 * Created by chan on 2017/6/7.
 */

public class BeaverService extends Service {

    public static final String KEY_EVENT = "event";
    public static final String KEY_DATA = "data";
    public static final String KEY_MODE = "mode";

    public static final int EVENT_CHECK_POINT = 0X01;
    public static final int EVENT_MODE_CHANGE = 0x02;

    public static final int MODE_NORMAL = 0x01;
    public static final int MODE_ACQUISITION = 0x02;

    private AbstractBeaverSender mAbstractBeaverSender;
    private AbstractBeaverSender.Callback mBeaverSenderCallback = new BeaverSenderCallback();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            int event = intent.getIntExtra(KEY_EVENT, 0);
            switch (event) {
                case EVENT_MODE_CHANGE:
                    handleModeChange(intent);
                    break;
                case EVENT_CHECK_POINT:
                    handleCheckPoint(intent);
                    break;
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mAbstractBeaverSender != null) {
            mAbstractBeaverSender.release();
        }
        super.onDestroy();
    }

    private void handleCheckPoint(Intent intent) {
        if (mAbstractBeaverSender == null) {
            return;
        }

        Event event = intent.getParcelableExtra(KEY_DATA);
        mAbstractBeaverSender.send(event);
    }

    private void handleModeChange(Intent intent) {
        int mode = intent.getIntExtra(KEY_MODE, 0);
        String url = intent.getStringExtra(KEY_DATA);

        if (TextUtils.isEmpty(url)) {
            return;
        }

        BeaverSenderBuilder beaverSenderBuilder = BeaverSenderBuilder.getInstance();
        mAbstractBeaverSender = beaverSenderBuilder.build(url, mode);
        if (mAbstractBeaverSender != null) {
            mAbstractBeaverSender.setCallback(mBeaverSenderCallback);
        }
    }

    private class BeaverSenderCallback implements AbstractBeaverSender.Callback {

        @Override
        public void onError(Exception e) {

        }
    }
}
