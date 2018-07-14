package com.ntt.ecl.webrtc.sample_p2p_videochat.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ntt.ecl.webrtc.sample_p2p_videochat.MyAppProperties;
import com.ntt.ecl.webrtc.sample_p2p_videochat.MyDataActivity;
import com.ntt.ecl.webrtc.sample_p2p_videochat.MyWebSocketClient;

import org.java_websocket.drafts.Draft_6455;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class MySocketService extends Service {

    public MyWebSocketClient myWebSocketClient;
    private String TAG;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.TAG = this.getClass().toString();
        Log.d(TAG, "Service socket start!");

        // Obtain all app properties
        MyAppProperties.init(this.getApplicationContext());
        Log.d(TAG, MyAppProperties.getProperty("MyWebSocket.hostname"));

        try {
            if (myWebSocketClient != null && myWebSocketClient.isOpen()) {
                myWebSocketClient.close();
            }
            myWebSocketClient = new MyWebSocketClient(this.getApplicationContext(), new URI("ws://" + MyAppProperties.getProperty("MyWebSocket.hostname") + ":" + MyAppProperties.getProperty("MyWebSocket.port")), new Draft_6455());
            myWebSocketClient.setConnectionLostTimeout(10000);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            myWebSocketClient.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MyDataActivity.setMyWebSocketClient(myWebSocketClient);

        while (!myWebSocketClient.isOpen()) {
            try {
                Thread.sleep(1000);
                myWebSocketClient.reconnectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "Service socket stop!");
        return START_STICKY;
    }

    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.i(TAG, "Uncaught exception start!");
            Log.i(TAG, ex.getLocalizedMessage());

            //Same as done in onTaskRemoved()
            PendingIntent service = PendingIntent.getService(
                    getApplicationContext(),
                    1001,
                    new Intent(getApplicationContext(), MySocketService.class),
                    PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
            System.exit(2);
        }
    };

    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            Log.e(TAG, "Running");
        }
    };

    @Override
    public void onDestroy() {
        try {
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this.getClass().toString());
        sendBroadcast(intent);
    }

}
