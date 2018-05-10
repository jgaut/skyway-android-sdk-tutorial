package com.ntt.ecl.webrtc.sample_p2p_videochat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import com.ntt.ecl.webrtc.sample_p2p_videochat.services.MySocketService;

/**
 * Created by jgautier on 24/09/2017.
 */

public class MyDataActivity {

    private static MainActivity mainActivity;
    private static MyWebSocketServer myWebSocketServer;
    private static MyWebSocketClient myWebSocketClient;
    private static Handler handler;
    private static MyGpio myGpio;
    private static String skyWayId;
    private static PeerActivity peerActivity;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MyDataActivity.context = context;
    }

    private static Context context;


    public static PeerActivity getPeerActivity() {
        return peerActivity;
    }

    public static void setPeerActivity(PeerActivity peerActivity) {
        MyDataActivity.peerActivity = peerActivity;
    }

    public static String getSkyWayId() {
        return MyDataActivity.skyWayId;
    }

    public static void setSkyWayId(String skyWayId) {
        MyDataActivity.skyWayId = skyWayId;
    }


    public static MyGpio getMyGpio() {
        return myGpio;
    }

    public static void setMyGpio(MyGpio myGpio) {
        MyDataActivity.myGpio = myGpio;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static void setHandler(Handler handler) {
        MyDataActivity.handler = handler;
    }

    public static MyWebSocketClient getMyWebSocketClient() {
        Intent it;
        if(myWebSocketClient==null){
            if (Build.MODEL.equals("iot_rpi3")) {

            }else {
                it = new Intent(context, MySocketService.class);
                context.startService(it);
            }
        }
        return myWebSocketClient;
    }

    public static void setMyWebSocketClient(MyWebSocketClient myWebSocketClient) {
        MyDataActivity.myWebSocketClient = myWebSocketClient;
    }

    public static MyWebSocketServer getMyWebSocketServer() {
        return myWebSocketServer;
    }

    public static void setMyWebSocketServer(MyWebSocketServer myWebSocketServer) {
        MyDataActivity.myWebSocketServer = myWebSocketServer;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        MyDataActivity.mainActivity = mainActivity;
    }

}
