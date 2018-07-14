package com.ntt.ecl.webrtc.sample_p2p_videochat;

import android.content.Intent;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * Created by jeremy on 09/06/2017.
 */

/*
https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/ChatServer.java
*/

public class MyWebSocketServer extends WebSocketServer {

    private String TAG = this.getClass().toString();

    public MyWebSocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    /*public MyWebSocketServer(InetSocketAddress address) {
        super(address);
    }*/


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        MyLog.logEvent("WebSocketServer opened=" + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        MyLog.logEvent("WebSocketServer closed;code=" + code + ";reason=" + reason + ";remote=" + remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        MyLog.logEvent("WebSocketServer received message=" + message);
        MyLog.logEvent(message);
        if (message != null) {
            this.sendToAll("Server received this message : " +message);
            if (message.equals("open door")) {
                MyDataActivity.getMyGpio().openDoor();
            } else if (message.equals("echo")) {

            } else if (message.equals("ring")) {
                MyDataActivity.getMyGpio().mGpioRingCallback().onGpioEdge(MyDataActivity.getMyGpio().getGpioTestRing());

            } else if (message.startsWith("call_me:")){
                String[] skyWay = message.split(":");
                Intent myIntent = new Intent(MyDataActivity.getMainActivity(), PeerActivity.class);
                myIntent.putExtra("skyWayId", skyWay[1]); //Optional parameters
                MyDataActivity.getMainActivity().startActivity(myIntent);
                this.sendToAll("Server will call : "+message);

            } else if (message.equals("next ring")){
                MyDataActivity.getMyGpio().setTimeNextRing(System.currentTimeMillis() / 1000);
            } else{
                Log.i(TAG, "message : \""+message+"\" non valide.");
            }
        } else {

        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        MyLog.logEvent("WebSocketServer error : " + ex.getMessage());
    }

    @Override
    public void onStart() {
        MyLog.logEvent("WebSocketServer started=" + this.getAddress().toString());
    }

    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
                MyLog.logEvent("WebSocketServer sent message=echo to " + c.getRemoteSocketAddress());
            }
        }
    }
}
