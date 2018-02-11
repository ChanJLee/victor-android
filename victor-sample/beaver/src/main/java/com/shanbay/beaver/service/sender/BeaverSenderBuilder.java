package com.shanbay.beaver.service.sender;

import android.support.annotation.NonNull;

import com.shanbay.beaver.config.BeaverConfig;

/**
 * Created by chan on 17/6/26.
 */

public class BeaverSenderBuilder {
    private static BeaverSenderBuilder sBeaverSenderBuilder;

    private Interceptor mInterceptor;

    private BeaverSenderBuilder() {

    }

    public static BeaverSenderBuilder getInstance() {
        if (sBeaverSenderBuilder == null) {
            synchronized (BeaverSenderBuilder.class) {
                if (sBeaverSenderBuilder == null) {
                    sBeaverSenderBuilder = new BeaverSenderBuilder();
                }
            }
        }
        return sBeaverSenderBuilder;
    }

    public AbstractBeaverSender build(@NonNull String url, @BeaverConfig.ConfigTag int mode) {
        if (mInterceptor != null) {
            AbstractBeaverSender abstractBeaverSender = mInterceptor.intercept(url, mode);
            if (abstractBeaverSender != null) {
                return abstractBeaverSender;
            }
        }

        switch (mode) {
            case BeaverConfig.MODE_NORMAL:
                return new NormalModelBeaverSender(url);
            case BeaverConfig.MODE_ACQUISITION:
                return new AcquisitionModeBeaverSender(url);
            default:
                return null;
        }
    }

    public void setInterceptor(Interceptor interceptor) {
        mInterceptor = interceptor;
    }

    public interface Interceptor {
        AbstractBeaverSender intercept(@NonNull String url, @BeaverConfig.ConfigTag int mode);
    }
}
