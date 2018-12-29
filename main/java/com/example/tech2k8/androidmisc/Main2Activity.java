package com.example.tech2k8.androidmisc;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity {

    Button server,client,send;
    EditText btName,msg;
    TextView receivedMsg;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mainSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ActivityCompat.requestPermissions(Main2Activity.this,new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_COARSE_LOCATION},1021);

        server=findViewById(R.id.server);
        client=findViewById(R.id.client);
        send=findViewById(R.id.send);
        btName=findViewById(R.id.bt_name);
        msg=findViewById(R.id.bt_msg);
        receivedMsg=findViewById(R.id.received_msg);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeData();
            }
        });

        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkForAdapter())
                {
                    if (mBluetoothAdapter.isEnabled())
                    {

                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                        try {
                            BluetoothServerSocket serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("MyApp",uuid);
                            new ConnectBt(serverSocket).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("Main2Activity","unable to create channel");
                        }
                    }
                    else
                    {
                        Log.i("Main2Activity","bluetooth not enabled");
                    }
                }
            }
        });


        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForAdapter())
                {
                    if (mBluetoothAdapter.isEnabled())
                    {

                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                        Set<BluetoothDevice> devices =mBluetoothAdapter.getBondedDevices();
                        for (BluetoothDevice device:devices)
                        {

                            Log.i("Main2Activity","device name ;  "+device.getName());
                            if (device.getName().equals(btName.getText().toString()))
                            {
                                Log.i("Main2Activity","pair name ;  "+btName.getText().toString());
                                try {

                                    BluetoothSocket socket=device.createRfcommSocketToServiceRecord(uuid);
                                    socket.connect();
                                    Log.i("Main2Activity","Socket created;  ");
                                    new ReadBt(socket).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.i("Main2Activity","unable to connect to server");
                                }
                            }
                        }

//                        try {
//                            BluetoothServerSocket serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("MyApp",uuid);
//                            new ConnectBt(serverSocket).execute();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Log.i("Main2Activity","unable to create channel");
//                        }
                    }
                    else
                    {
                        Log.i("Main2Activity","bluetooth not enabled");
                    }
                }
            }
        });
    }

    private boolean checkForAdapter()
    {
         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
         if (mBluetoothAdapter != null)
         {
             return true;
         }

         return false;
    }

    class ConnectBt extends AsyncTask
    {

        BluetoothServerSocket lServerSocket;

        public ConnectBt(BluetoothServerSocket lServerSocket) {
            this.lServerSocket = lServerSocket;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("Main2Activity","on pre execute in server");

        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            Log.i("Main2Activity","on post execute in server");
        }

        @Override
        protected Object doInBackground(Object[] objects) {


            try {

                Log.i("Main2Activity","before socket creation");
               mainSocket=lServerSocket.accept();
                Log.i("Main2Activity","socket accepted");
                    writeData();

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Main2Activity","unable to accept conn"+e.getMessage());
            }
            return null;
        }
    }


    class ReadBt extends AsyncTask
    {

        BluetoothSocket socket;

        public ReadBt(BluetoothSocket socket) {
            this.socket = socket;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            InputStream stream= null;
            try {
                stream = socket.getInputStream();
                Log.i("Main2Activity", "Soccect connected");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Main2Activity", "Input stream not available" + e.getMessage());
            }
            while (true) {

                byte data[] = new byte[1024];
                try {
                    if (stream ==null)
                    {
                        Log.i("Main2Activity", "Stream is null ");
                    }
                    else
                    {
                        int length =stream.read(data);
                        
                        Log.i("Main2Activity", "Data " + Arrays.toString(data));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("Main2Activity", "unable to read data" + e.getMessage());
                }
            }
            //return null;
        }
    }

    private void writeData()
    {
        OutputStream stream= null;
        try {
            stream = mainSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Main2Activity","output stream obtained");
        try {
            stream.write(msg.getText().toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
