package com.ntt.ecl.webrtc.sample_p2p_videochat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by jeremy on 10/06/2017.
 * https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/ChatClient.java
 */

public class MyWebSocketClient extends WebSocketClient {

    private final String TAG = this.getClass().toString();
    private String myMessage;
    private int myCode;
    private String myReason;
    private boolean myRemote;
    private Context context;

    public MyWebSocketClient(Context context, URI serverUri , Draft draft) {
        super( serverUri, draft );
        this.context = context;
    }

    public MyWebSocketClient(URI serverURI ) {
        super( serverURI );
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        /*MyDataActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                MyDataActivity.getMainActivity().getTextView().append("\n"+ "opened connection. Timeout="+getConnectionLostTimeout()+"    "+getResourceDescriptor() );
                MyDataActivity.getMainActivity().getbConnect().setEnabled(false);
            }
        });*/
    }

    @Override
    public void onMessage(final String message ) {
        System.out.println( "received: " + message );
        myMessage = message;
        if(myMessage!=null){
            MyDataActivity.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if(message!=null && message.equals("Ring !")){
                        Log.d(TAG,"Lancement IHM!");
                        /*Lancement de l'IHM*/
                        Intent it = new Intent(context, MainActivity.class);
                        /*it.setFlags(Intent.ACTION_SCREEN_ON);
                        it.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                        //it.addFlags();*/
                        context.startActivity(it);
                    }
                    //MyDataActivity.getMainActivity().getTextView().append("\n"+myMessage);
                    /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyDataActivity.getContext(), "Perso")
                            .setSmallIcon(R.drawable.tile)
                            .setContentTitle("Interphone")
                            .setContentText(myMessage)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyDataActivity.getContext());
                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(new Double(Math.random()).intValue(), mBuilder.build());
                */}
            });
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote ) {
        this.myCode=code;
        this.myReason=reason;
        this.myRemote=remote;
        MyDataActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                // The codecodes are documented in class org.java_websocket.framing.CloseFrame
                //MyDataActivity.getMainActivity().getTextView().append("\n"+"Connection closed by " + ( myRemote ? "remote peer" : "us" ) + ". reason=" +myReason);
                Log.i(TAG, "\n"+"Connection closed by " + ( myRemote ? "remote peer" : "us" ) + ". reason=" +myReason);
                //MyDataActivity.getMainActivity().getbConnect().setEnabled(true);
                //MyDataActivity.getMyWebSocketClientManager().reconnect();
                MyDataActivity.getMyWebSocketClient().reconnect();
                //Log.i(TAG, "try to connect !");

            }
        });

    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
        MyDataActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                MyDataActivity.getMainActivity().getbConnect().setEnabled(true);
            }
        });
    }

    @Override
    public void send(String string){
        if(this.isOpen()) {
            super.send(string);
        }
    }

}
