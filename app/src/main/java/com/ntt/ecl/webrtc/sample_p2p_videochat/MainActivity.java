package com.ntt.ecl.webrtc.sample_p2p_videochat;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.splunk.mint.Mint;

import org.java_websocket.WebSocketImpl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by jgautier on 19/01/2018.
 */

public class MainActivity extends Activity {
    private static final String TAG = PeerActivity.class.getSimpleName();

        //Client
    private TextView textView;
    private Button bOpenDoor;
    private Button bEcho;
    private Button bConnect;
    private Button bDisco;
    private Button bRing;
    private Button bCall;
    private Button bNextRing;
    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain all app properties
        MyAppProperties.init(this.getApplicationContext());

        //https => http://stacktips.com/snippet/how-to-trust-all-certificates-for-httpurlconnection-in-android
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }

        //Mint
        //Set the application environment
        /*Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.enableDebugLog();
        Mint.setUserOptOut(false);
        Mint.initAndStartSessionHEC(this.getApplication(), MyAppProperties.getProperty("Splunk.hec.url"), MyAppProperties.getProperty("Splunk.hec.token"));
        //Set some form of userIdentifier for this session
        Mint.setUserIdentifier(MyAppProperties.getProperty("Splunk.id"));
        */
        //
        // Windows title hidden
        //
        Window wnd = getWindow();
        wnd.addFlags(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_peer);

        //
        // Set UI handler
        //
        //MyDataActivity.setHandler(new Handler(Looper.getMainLooper()));

        //Launch specifics tasks for each model
        Log.i(TAG, "Model=" + Build.MODEL);
        if (Build.MODEL.equals("iot_rpi3")) {
            // GPIO
            MyDataActivity.setMyGpio(new MyGpio());

            //WebSocketServer
            WebSocketImpl.DEBUG = Boolean.getBoolean(MyAppProperties.getProperty("MyWebSocket.debug"));
            try {
                InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(MyAppProperties.getProperty("MyWebSocket.hostname")), Integer.parseInt(MyAppProperties.getProperty("MyWebSocket.port")));
                MyWebSocketServer myWebSocketServer = new MyWebSocketServer(Integer.parseInt(MyAppProperties.getProperty("MyWebSocket.port")));
                myWebSocketServer.start();
                MyDataActivity.setMyWebSocketServer(myWebSocketServer);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }


        }else{ // Mobile
            setContentView(R.layout.activity_main);
            WebSocketImpl.DEBUG = Boolean.getBoolean(MyAppProperties.getProperty("MyWebSocket.debug"));
            //MyDataActivity.setMyWebSocketClientManager(new MyWebSocketClientManager());
            textView = (TextView) findViewById(R.id.textView);
            bOpenDoor = (Button) findViewById(R.id.bOpenDoor);
            bEcho = (Button) findViewById(R.id.bEcho);
            bConnect = (Button) findViewById(R.id.bConnect);
            bDisco = (Button) findViewById(R.id.bDisco);
            bRing = (Button) findViewById(R.id.bRing);
            bCall = (Button) findViewById(R.id.bCall);
            bNextRing = (Button) findViewById(R.id.bNextRing);
            relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);

            MyDataActivity.setContext(this);
            MyDataActivity.setHandler(new Handler(Looper.getMainLooper()));

            bOpenDoor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyDataActivity.getMyWebSocketClient().send("open door");
                }
            });

            bEcho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyDataActivity.getMyWebSocketClient()!=null && MyDataActivity.getMyWebSocketClient().isOpen()){
                        MyDataActivity.getMyWebSocketClient().send("echo");
                    }
                }
            });

            bConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyDataActivity.getMyWebSocketClient();
                }
            });

            bDisco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyDataActivity.getMyWebSocketClient()!=null && MyDataActivity.getMyWebSocketClient().isOpen()){
                        MyDataActivity.getMyWebSocketClient().close();
                    }
                }
            });

            bRing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyDataActivity.getMyWebSocketClient()!=null && MyDataActivity.getMyWebSocketClient().isOpen()){
                        MyDataActivity.getMyWebSocketClient().send("ring");
                    }
                }
            });

            bNextRing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyDataActivity.getMyWebSocketClient()!=null && MyDataActivity.getMyWebSocketClient().isOpen()){
                        MyDataActivity.getMyWebSocketClient().send("next ring");
                    }
                }
            });

            bCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent myIntent = new Intent(MainActivity.this, PeerActivity.class);
                        myIntent.putExtra("skyWayId", "0"); //Optional parameters
                        MainActivity.this.startActivity(myIntent);

                }
            });
            //MyDataActivity.getMyWebSocketClientManager().connect();
        }

        MyDataActivity.setMainActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        } else {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }


    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public Button getbOpenDoor() {
        return bOpenDoor;
    }

    public void setbOpenDoor(Button bOpenDoor) {
        this.bOpenDoor = bOpenDoor;
    }

    public Button getbEcho() {
        return bEcho;
    }

    public void setbEcho(Button bEcho) {
        this.bEcho = bEcho;
    }

    public Button getbConnect() {
        return bConnect;
    }

    public void setbConnect(Button bConnect) {
        this.bConnect = bConnect;
    }

    public Button getbRing() {
        return bRing;
    }

    public void setbRing(Button bRing) {
        this.bRing = bRing;
    }

    public Button getbCall() {
        return bCall;
    }

    public void setbCall(Button bCall) {
        this.bCall = bCall;
    }

    public Button getbNextRing() {
        return bNextRing;
    }

    public void setbNextRing(Button bNextRing) {
        this.bNextRing = bNextRing;
    }
}
