package com.shanbay.beaver.service.sender;

import com.shanbay.beaver.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chan on 2017/6/9.
 */

public class NormalModelBeaverSender extends AbstractBeaverSender {
    private static final int MAX_EVENT_CACHE = 10;

    private List<Event> mEvents = new ArrayList<>();

    public NormalModelBeaverSender(String url) {
        super(url);
    }

    @Override
    public synchronized <T extends Event> void send(T data) {
        //累计到比如十个的时候发送
        mEvents.add(data);
        if (mEvents.size() >= MAX_EVENT_CACHE) {
            List<Event> copy = new ArrayList<>();
            copy.addAll(mEvents);
            mEvents.clear();
            sendEvents(copy);
        }
    }

    @Override
    public void release() {
        sendEvents(mEvents);
    }

    public void sendEvents(List<Event> events) {

    }
}
