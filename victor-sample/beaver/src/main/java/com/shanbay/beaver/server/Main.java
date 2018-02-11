package com.shanbay.beaver.server;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.view.InputDeviceCompat;
import android.view.InputEvent;
import android.view.MotionEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shanbay.beaver.data.KeyCode;
import com.shanbay.beaver.data.Motion;
import com.shanbay.beaver.data.ProtocolMessage;
import com.shanbay.beaver.data.Window;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

public class Main {

    private static final int WEB_LISTEN_PORT = 56789;
    private static final int ANDROID_LISTEN_PORT = 56790;

    private static InputManager sInputManager;
    private static Method sInjectInputEventMethod;
    private static float sScreenWidth = 1080;
    private static float sScreenHeight = 1920;
    private static int sPictureWidth = 360;
    private static int sPictureHeight = 640;
    private static int sRotate = 0;
    private static Thread sSendImageThread;
    private static int sMode;

    public static void main(String[] args) {
        Looper.prepare();

        try {
            if (args != null && args.length == 2) {
                sScreenWidth = Float.parseFloat(args[0]);
                sScreenHeight = Float.parseFloat(args[1]);
            }

            System.out.println("screen width: " + sScreenWidth + " screen height: " + sScreenHeight);

            sInputManager = (InputManager) InputManager.class.getDeclaredMethod("getInstance").invoke(null);
            sMode = (int) InputManager.class.getDeclaredField("INJECT_INPUT_EVENT_MODE_ASYNC").get(null);
            sInjectInputEventMethod = InputManager.class.getMethod("injectInputEvent", InputEvent.class, Integer.TYPE);
            System.out.println("hooked input service");

            WebSocketServer webSocketServer = new BeaverServer(new InetSocketAddress(WEB_LISTEN_PORT));
            webSocketServer.start();
            System.out.println("listen port: " + WEB_LISTEN_PORT);

            AndroidInputHandler androidInputHandler = new AndroidInputHandler(webSocketServer.connections());
            androidInputHandler.listen();

            Looper.loop();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    private static class AndroidInputHandler implements Runnable {
        private final Collection<WebSocket> mWebSockets;

        AndroidInputHandler(Collection<WebSocket> webSockets) {
            mWebSockets = webSockets;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(ANDROID_LISTEN_PORT);
                while (true) {

                    System.out.println("wait android input port: " + ANDROID_LISTEN_PORT);
                    Socket socket = serverSocket.accept();
                    System.out.println("received android input");

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String content = null;
                    while (!socket.isInputShutdown() && (content = bufferedReader.readLine()) != null) {
                        stringBuilder.append(content);
                    }

                    String json = stringBuilder.toString();
                    System.out.println("write to web: " + json);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    out.write(new byte[]{0x02});
                    out.write(json.getBytes());
                    byte[] data = out.toByteArray();

                    synchronized (mWebSockets) {
                        for (WebSocket webSocket : mWebSockets) {
                            webSocket.send(data);
                            System.out.println("wrote android event");
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("error: " + e.getMessage());
            }
        }

        public void listen() {
            new Thread(this).start();
        }
    }

    private static void handleDownMotion(ProtocolMessage<Motion> motionProtocolMessage) {
        float x = motionProtocolMessage.data.x * (sScreenWidth / sPictureWidth);
        float y = motionProtocolMessage.data.y * (sScreenHeight / sPictureHeight);
        System.out.println("down x: " + x + " y: " + y);
        injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_DOWN, SystemClock.uptimeMillis(), x, y, 1.0f);
    }

    private static void handleUpMotion(ProtocolMessage<Motion> motionProtocolMessage) {
        float x = motionProtocolMessage.data.x * (sScreenWidth / sPictureWidth);
        float y = motionProtocolMessage.data.y * (sScreenHeight / sPictureHeight);
        System.out.println("up x: " + x + " y: " + y);
        injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_UP, SystemClock.uptimeMillis(), x, y, 1.0f);
    }

    private static void handleMoveMotion(ProtocolMessage<Motion> motionProtocolMessage) {
        float x = motionProtocolMessage.data.x * (sScreenWidth / sPictureWidth);
        float y = motionProtocolMessage.data.y * (sScreenHeight / sPictureHeight);
        System.out.println("move x: " + x + " y: " + y);
        injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_MOVE, SystemClock.uptimeMillis(), x, y, 1.0f);
    }

    private static void handleHeartBeat() {
    }

    private static void handleResize(ProtocolMessage<Window> windowProtocolMessage) {
        sPictureWidth = windowProtocolMessage.data.w;
        sPictureHeight = windowProtocolMessage.data.h;
        sRotate = windowProtocolMessage.data.r;
    }

    private static void handleKeyCode(ProtocolMessage<KeyCode> keyCodeProtocolMessage) {

        String command = null;
        System.out.println("handle key code: " + keyCodeProtocolMessage.data.keyCode);
        switch (keyCodeProtocolMessage.data.keyCode) {
            case KeyCode.KEY_CODE_BACK:
                command = "input keyevent KEYCODE_BACK";
                break;
            case KeyCode.KEY_CODE_HOME:
                command = "input keyevent KEYCODE_HOME";
                break;
            case KeyCode.KEY_CODE_MENU:
                command = "input keyevent KEYCODE_APP_SWITCH";
                break;
            default:
                return;
        }

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static class SendScreenShotThread implements Runnable {

        private static final byte CODE_IMAGE = 0x01;

        private final Collection<WebSocket> mWebSockets;
        String mSurfaceName;

        SendScreenShotThread(Collection<WebSocket> webSockets) {
            mWebSockets = webSockets;
            if (Build.VERSION.SDK_INT <= 17) {
                mSurfaceName = "android.view.Surface";
            } else {
                mSurfaceName = "android.view.SurfaceControl";
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (!mWebSockets.isEmpty()) {
                        Bitmap bitmap = (Bitmap) Class.forName(mSurfaceName)
                                .getDeclaredMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE})
                                .invoke(null, sPictureWidth, sPictureHeight);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(sRotate);
                        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        out.write(new byte[]{CODE_IMAGE});
                        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        byte[] data = out.toByteArray();
                        synchronized (mWebSockets) {
                            for (WebSocket webSocket : mWebSockets) {
                                webSocket.send(data);
                            }
                        }
                    }

                    Thread.sleep(50);
                } catch (Exception e) {
                    System.out.println("error: " + e.getMessage());
                    break;
                }
            }
        }
    }

    private static void injectMotionEvent(int inputSource, int action, long when, float x, float y, float pressure) {
        try {
            System.out.println("sending event, mode: " + sMode);
            MotionEvent event = MotionEvent.obtain(when, SystemClock.uptimeMillis(), action, x, y, pressure, 1.0f, 0, 1.0f, 1.0f, 0, 0);
            event.setSource(inputSource);
            sInjectInputEventMethod.invoke(sInputManager, event, sMode);
            event.recycle();
            System.out.println("sent event");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }


    public static class BeaverServer extends WebSocketServer {
        private Gson mGson;

        public BeaverServer(InetSocketAddress address) {
            super(address);
            mGson = new Gson();
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            System.out.println("client open");
            if (sSendImageThread == null) {
                synchronized (BeaverServer.class) {
                    if (sSendImageThread == null) {
                        System.out.println("new send screen thread");
                        sSendImageThread = new Thread(new SendScreenShotThread(connections()));
                        sSendImageThread.start();
                        System.out.println("prepare to send screen shot");
                    }
                }
            }
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("client close");
        }

        @Override
        public void onMessage(WebSocket conn, String json) {
            try {
                System.out.println("web: " + json);
                JSONObject event = new JSONObject(json);
                int eventType = event.getInt(ProtocolMessage.KEY_TYPE);
                switch (eventType) {
                    case ProtocolMessage.TYPE_MOTION_DOWN:
                        ProtocolMessage<Motion> message = mGson.fromJson(json, new TypeToken<ProtocolMessage<Motion>>() {
                        }.getType());
                        handleDownMotion(message);
                        break;
                    case ProtocolMessage.TYPE_MOTION_UP:
                        message = mGson.fromJson(json, new TypeToken<ProtocolMessage<Motion>>() {
                        }.getType());
                        handleUpMotion(message);
                        break;
                    case ProtocolMessage.TYPE_MOTION_MOVE:
                        message = mGson.fromJson(json, new TypeToken<ProtocolMessage<Motion>>() {
                        }.getType());
                        handleMoveMotion(message);
                        break;
                    case ProtocolMessage.TYPE_HEART_BEAT:
                        handleHeartBeat();
                        break;
                    case ProtocolMessage.TYPE_RESIZE:
                        ProtocolMessage<Window> windowProtocolMessage = mGson.fromJson(json, new TypeToken<ProtocolMessage<Window>>() {
                        }.getType());
                        handleResize(windowProtocolMessage);
                        break;
                    case ProtocolMessage.TYPE_KEY_CODE:
                        ProtocolMessage<KeyCode> keyCodeProtocolMessage = mGson.fromJson(json, new TypeToken<ProtocolMessage<KeyCode>>() {
                        }.getType());
                        handleKeyCode(keyCodeProtocolMessage);
                        break;
                }
            } catch (Exception e) {
                System.out.println("error: " + e.getMessage());
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.out.println("have exception!");
        }

        @Override
        public void onStart() {
        }
    }
}