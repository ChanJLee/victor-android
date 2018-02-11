package com.shanbay.beaver.service.sender;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shanbay.beaver.event.Event;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chan on 2017/6/9.
 */

public class AcquisitionModeBeaverSender extends AbstractBeaverSender {

	private static final Pattern URL_PATTER = Pattern.compile("^shanbay://([^/:]+)(:(\\d+))/([^/]+)$");

	private String mIp;
	private int mPort;
	private Gson mGson;

	public AcquisitionModeBeaverSender(String url) {
		super(url);
		mGson = new Gson();
		Matcher matcher = URL_PATTER.matcher(url);
		if (matcher.matches()) {
			mIp = matcher.group(1);
			mPort = Integer.parseInt(matcher.group(3));
		}
	}

	@Override
	public <T extends Event> void send(final T data) {
		new Thread(new Runnable() {

			private Socket mSocket;

			@Override
			public void run() {
				try {
					mSocket = new Socket(mIp, mPort);
					mSocket.setKeepAlive(true);
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
					String json = mGson.toJson(data, new TypeToken<T>() {
					}.getType());
					bufferedWriter.write(json);
					bufferedWriter.flush();
					bufferedWriter.close();
				} catch (IOException e) {
					if (mCallback != null) {
						mCallback.onError(e);
					}
				}
			}
		}).start();
	}

	@Override
	public void release() {

	}
}
