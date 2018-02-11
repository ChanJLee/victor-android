package com.shanbay.beaver.service.sender;

import com.google.gson.Gson;
import com.shanbay.beaver.event.Event;

/**
 * Created by chan on 2017/6/9.
 */

public abstract class AbstractBeaverSender {
    protected String mUrl;
    protected Callback mCallback;
    private Gson mGson;

    public AbstractBeaverSender(String url) {
        mUrl = url;
        mGson = new Gson();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public abstract <T extends Event> void send(T data);

    public abstract void release();

    public interface Callback {
        void onError(Exception e);
    }
}
