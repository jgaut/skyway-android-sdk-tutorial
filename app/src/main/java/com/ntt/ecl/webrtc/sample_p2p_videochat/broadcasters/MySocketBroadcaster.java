package com.ntt.ecl.webrtc.sample_p2p_videochat.broadcasters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.ntt.ecl.webrtc.sample_p2p_videochat.services.MySocketService;

public class MySocketBroadcaster extends BroadcastReceiver {

    private String TAG;

    public MySocketBroadcaster() {
        // TODO Auto-generated constructor stub
        this.TAG = this.getClass().toString();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Broadcaster socket debut !");
        if (Build.MODEL.equals("iot_rpi3")) {

        }else {
            Intent it = new Intent(context, MySocketService.class);
            context.startService(it);
        }
        Log.d(TAG,"Broadcaster socket fin !");

    }
}
