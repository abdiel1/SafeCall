package btcomm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdielrosado.safecall.Countdown;
import com.example.abdielrosado.safecall.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Kenneth on 3/25/2016.
 */
public class SendAndGetData {
    private static final String TAG = "SENDANDGETDATAACTIVITY";
    private static SendAndGetData instance;
    private static Context context;
    private final byte delimiter = 10; //This is the ASCII code for a newline character
    private final byte letterE = 69; //This is the ASCII code for the E character
    private final byte letterF = 70; //This is the ASCII code for the F character


    BluetoothAdapter BTAdapter;
    BluetoothSocket BTSocket;
    BluetoothDevice BTDevice;
    OutputStream output;
    InputStream input;
    Thread listeningThread;

    byte[] readBuffer;
    int readBufferPosition;
    private AtomicBoolean stopListeningThread;
    private AtomicBoolean connected;


    private SendAndGetData(Context c){
        context = c;
        stopListeningThread = new AtomicBoolean(false);
        connected = new AtomicBoolean(false);
    }


    public static SendAndGetData getInstance(Context context){
        if(instance == null){
            instance = new SendAndGetData(context);
        }
        return instance;
    }

    public void start(String id){
        if(!connected.get()){
            findBluetoothDevices(id);
            openConnectionToBT();
        }
    }


    public void findBluetoothDevices(String id) {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();


        if(!BTAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBluetooth);
        }

        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        //TODO add the pairing
//        BluetoothDevice deviceToConnectTo = BTAdapter.getRemoteDevice(id);
//
//        if(deviceToConnectTo.getBondState() == BluetoothDevice.BOND_NONE){
//            deviceToConnectTo.createBond();
//        }

        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(id)) {
                    BTDevice = device;
                    break;
                }
            }
        }
    }

    void openConnectionToBT() {
        try{
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            BTSocket = BTDevice.createRfcommSocketToServiceRecord(uuid);
            BTSocket.connect();
            output = BTSocket.getOutputStream();
            input = BTSocket.getInputStream();
            connected.set(true);

            Toast.makeText(context,"Connected to: " + BTDevice.getName(),Toast.LENGTH_LONG).show();

            beginListenForData();

        }
        catch (IOException e){
            Log.e(TAG, e.toString());
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();

        stopListeningThread.set(false);
        readBufferPosition = 0;
        readBuffer = new byte[32];
        listeningThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopListeningThread.get()) {
                    try {
                        int bytesAvailable = input.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            input.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                }
                                else if (b == letterE){
                                    if(!Countdown.runningTimer.get()){
                                        Intent intent = new Intent(context,Countdown.class);
                                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    }

                                }
                                else if(b == letterF){
                                    if(Countdown.runningTimer.get()){
                                        Countdown.stop();
                                    }
                                }
                                else {
                                    if(readBufferPosition == readBuffer.length - 1){
                                        readBufferPosition = 0;
                                    }
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException e) {
                        stopListeningThread.set(true);
                        Log.e(TAG, e.toString());

                    }
                }
            }
        });
        listeningThread.start();
    }

    public void sendDataToBT(String message) {
        try {
            if(connected.get()){
                message += "\n";
                output.write(message.getBytes());
            }

        }catch (IOException e){
            Log.e(TAG, e.toString());
        }
    }

    public void closeConnectionFromBT() {
        try {
            if(connected.get()){
                stopListeningThread.set(true);
                output.close();
                input.close();
                BTSocket.close();
                connected.set(false);
            }
        }
        catch (IOException e){
            Log.e(TAG, e.toString());
        }
    }

    public boolean isConnected(){
        return connected.get();
    }

}
