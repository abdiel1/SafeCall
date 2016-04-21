package btcomm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.abdielrosado.safecall.MainActivity;
import com.example.abdielrosado.safecall.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Kenneth on 3/25/2016.
 */
public class SendAndGetDataActivity extends AppCompatActivity {
    private static final String TAG = "SENDANDGETDATAACTIVITY";

    TextView label;
    EditText entryTextBox;

    BluetoothAdapter BTAdapter;
    BluetoothSocket BTSocket;
    BluetoothDevice BTDevice;
    OutputStream output;
    InputStream input;
    Thread listeningThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter = 0;
    volatile boolean stopListeningThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_send_and_get_data);

        //Connect to the Bluetooth device chosen
        final String id = this.getIntent().getStringExtra("device_name");
        findBluetoothDevices(id);
        openConnectionToBT();

    }

    void findBluetoothDevices(String id) {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if(BTAdapter == null) {
            label.setText(R.string.no_adapter);
        }

        if(!BTAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(id)) {
                    BTDevice = device;
                    break;
                }
            }
        }
        label.setText("Bluetooth Device Found: " + BTDevice.getName().toString());
    }

    void openConnectionToBT() {
        try{
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            BTSocket = BTDevice.createRfcommSocketToServiceRecord(uuid);
            BTSocket.connect();
            output = BTSocket.getOutputStream();
            input = BTSocket.getInputStream();

            beginListenForData();

            label.setText(R.string.BTopened + BTDevice.getName().toString());
        }
        catch (IOException e){
            Log.e(TAG, e.toString());
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
        final byte letterE = 69; //This is the ASCII code for the E character

        stopListeningThread = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        listeningThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopListeningThread) {
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

                                    handler.post(new Runnable() {
                                        public void run() {
                                            label.setText(data);
                                        }
                                    });
                                }
                                else if (b == letterE){
                                    counter = counter + 1;
                                    Log.d(TAG, "The letter E was received. Time: " + counter);
                                    handler.post(new Runnable() {
                                        public void run() {
                                            label.setText("The letter E was received." + counter);
                                        }
                                    });

                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException e) {
                        stopListeningThread = true;
                        Log.e(TAG, e.toString());

                    }
                }
            }
        });
        listeningThread.start();
    }

    void sendDataToBT() {
        try {
            String message = entryTextBox.getText().toString();
            message += "\n";
            output.write(message.getBytes());
            label.setText(R.string.sent);
        }catch (IOException e){
            Log.e(TAG, e.toString());
        }
    }

    void sendDataToBT(String msg) {
        try {
            String message = msg ;
            message += "\n";
            output.write(message.getBytes());
            label.setText(R.string.sent);
        }catch (IOException e){
            Log.e(TAG, e.toString());
        }
    }

    void closeConnectionFromBT() {
        try {
            stopListeningThread = true;
            output.close();
            input.close();
            BTSocket.close();
            label.setText(R.string.closed);
        }
        catch (IOException e){
            Log.e(TAG, e.toString());
        }
    }

}
