package com.example.tennisballlauncher;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    // Intent request codes
    private Set pairedDevices;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    String address = null;
    SeekBar speedSeekBar;
    SeekBar delaySeekBar;
    TextView currentSpeed;
    TextView currentDelay;
    int progressChangedValue = 0;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        setContentView(R.layout.activity_main);
        new ConnectBT().execute(); //Call the class to connect
        currentSpeed = findViewById(R.id.CurrentSpeed);
        currentDelay = findViewById(R.id.CurrentDelay);
        speedSeekBar = findViewById(R.id.SpeedSlider);
        delaySeekBar = findViewById(R.id.DelaySlider);
        // perform seek bar change listener event used for getting the progress value
        speedSeekBar.setOnSeekBarChangeListener(this);
        delaySeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch(seekBar.getId())
        {
            case R.id.SpeedSlider:
                progressChangedValue = progress;
                break;
            case R.id.DelaySlider:
                progressChangedValue = progress;
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.SpeedSlider:
                String newSpeed = Integer.toString(progressChangedValue);
                currentSpeed.setText(newSpeed);
                if (btSocket != null) {
                    try {
                        //btSocket.getOutputStream().write(progressChangedValue);
                        char speed = (char) progressChangedValue;
                        char BtTxCmd = 's';
                        //String BtTxData = ('s' + (char)progressChangedValue);
                        btSocket.getOutputStream().write(BtTxCmd);
                        btSocket.getOutputStream().write(speed);
                    } catch (IOException e) {

                    }
                }
                break;
            case R.id.DelaySlider:
                String newDelay = Integer.toString(progressChangedValue);
                currentDelay.setText(newDelay);
                if (btSocket != null) {
                    try {
                        //btSocket.getOutputStream().write(progressChangedValue);
                        char delay = (char) progressChangedValue;
                        char BtTxCmd = 't';
                        //String BtTxData = ('s' + (char)progressChangedValue);
                        btSocket.getOutputStream().write(BtTxCmd);
                        btSocket.getOutputStream().write(delay);
                    } catch (IOException e) {
                    }
                }
                break;

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
               // msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
              //  msg("Connected.");
                isBtConnected = true;
            }
           // progress.dismiss();
        }
    }
}
