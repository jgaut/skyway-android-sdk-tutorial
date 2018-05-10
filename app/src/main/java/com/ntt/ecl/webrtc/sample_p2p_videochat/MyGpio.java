package com.ntt.ecl.webrtc.sample_p2p_videochat;

import android.app.UiAutomation;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jeremy on 18/01/2017.
 * https://developer.android.com/things/hardware/raspberrypi-io.html
 */

public class MyGpio {

    private boolean delay = true;
    private final String ConfGpioRing = "BCM24";
    private final String TAG = this.getClass().toString();
    private Gpio gpioRing;
    private Gpio gpioOpenDoor;
    private Gpio gpioTestRing;
    private final GpioCallback mGpioRingCallback;
    private long timeNextRing=0;
    private long timeNextRingDelay = 60*5;
    private int delayRing=5000;
    private int delayOpenDoor=1000;

    MyGpio() {

        //Listing des ports GPIO
        PeripheralManager manager = PeripheralManager.getInstance();
        List<String> portList = manager.getGpioList();
        if (portList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.");
        } else {
            Log.i(TAG, "List of available ports: " + portList);
        }

        //Gestion du callback
        mGpioRingCallback = new GpioCallback() {
            @Override
            public boolean onGpioEdge(Gpio gpio) {
                // Read the active low pin state
                try {
                    Log.i("GPIO-Callback", "BCM24="+gpio.getValue());
                    if (gpio.getValue() && delay) {
                        delay=false;
                        Log.i(TAG, "Setup Security delay=" + delay);
                        MyLog.logEvent(gpio.getName()+ "=" + gpio.getValue());
                        if(timeNextRing>=(System.currentTimeMillis() / 1000)){
                            openDoor();
                            timeNextRing=0;
                        }
                        MyDataActivity.getMyWebSocketServer().sendToAll("Ring !");
                        new Timer().schedule(new TimerTask(){
                            public void run(){
                                delay=true;
                                Log.i(TAG, "Reset Security delay=" + delay);
                                //initGpio24();
                            }
                        },delayRing);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Continue listening for more interrupts => true
                return true;
            }

            @Override
            public void onGpioError(Gpio gpio, int error) {
                Log.w(TAG, gpio + ": Error event " + error);
            }
        };

        //Initialisation des GPIO pour la détection de la sonnerie
        //BCM4 -> DIRECTION_IN + ACTIVE_LOW + EDGE_RISING
        try {
            // PASS THE COMMAND DIRECTLY, HERE FOR ROOT ACCESS
            /*Process su = Runtime.getRuntime().exec("/system/xbin/su");
            su = Runtime.getRuntime().exec("echo DOWN > /sys/class/pinctrl/"+ConfGpioRing+"/resistor");*/
            /*try {
                Process process = Runtime.getRuntime().exec("/system/xbin/su");
                InputStreamReader reader = new InputStreamReader(process.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(reader);
                int numRead;
                char[] buffer = new char[5000];
                StringBuffer commandOutput = new StringBuffer();
                while ((numRead = bufferedReader.read(buffer)) > 0) {
                    commandOutput.append(buffer, 0, numRead);
                }
                bufferedReader.close();
                process.waitFor();

                Log.i(TAG,":::::::::::::::::::" +commandOutput.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            /*
            try {
                // Get the root permission
                Log.i(TAG, "Begin of init : "+ConfGpioRing);




                Process p = Runtime.getRuntime().exec("ls");
                //UiAutomation uiAutomation = getInstrumentation().getUiAutomation();
                //ParcelFileDescriptor tmp = UiAutomation.executeShellCommand("");
                // Enable GPIO pull-up
                DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                dos.writeBytes("su\n");
                dos.writeBytes("echo DOWN > /sys/class/pinctrl/\"+ConfGpioRing+\"/resistor\n");
                dos.writeBytes("exit\n");
                dos.flush();
                dos.close();
                p.waitFor();
                Log.i(TAG, "End of init : "+ConfGpioRing);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }*/

            gpioRing = manager.openGpio(ConfGpioRing);
            gpioRing.registerGpioCallback(mGpioRingCallback);
            gpioRing.setDirection(Gpio.DIRECTION_IN);
            gpioRing.setActiveType(Gpio.ACTIVE_HIGH);
            gpioRing.setEdgeTriggerType(Gpio.EDGE_BOTH);
            Log.i(TAG, "First time "+gpioRing.getName()+"=" + gpioRing.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Initialisation des GPIO pour la détection de la sonnerie -> Simulation
        //BCM18 -> OUT
        try {
            gpioTestRing = manager.openGpio("BCM18");
            gpioTestRing.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            Log.i(TAG, "First time BCM18=" + gpioTestRing.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Initialisation des GPIO pour l'ouverture de la porte
        //BCM23 -> OUT to open the door
        try {
            gpioOpenDoor = manager.openGpio("BCM23");
            // Initialize the pin as an input
            gpioOpenDoor.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Log.i(TAG, "First time BCM23=" + gpioOpenDoor.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Gpio getGpioTestRing() {
        return gpioTestRing;
    }

    public GpioCallback mGpioRingCallback() {
        return mGpioRingCallback;
    }

    public void openDoor(){
        MyLog.logEvent("Open Door");
        try {
            gpioOpenDoor.setValue(true);
            Log.i(TAG, "BCM23:" + gpioOpenDoor.getValue());
            new Timer().schedule(new TimerTask(){
                public void run(){
                    try {
                        gpioOpenDoor.setValue(false);
                        Log.i(TAG, "BCM23:" + gpioOpenDoor.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },delayOpenDoor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTimeNextRing(long timeNextRing) {
        this.timeNextRing = timeNextRing*this.timeNextRingDelay;
    }
}
