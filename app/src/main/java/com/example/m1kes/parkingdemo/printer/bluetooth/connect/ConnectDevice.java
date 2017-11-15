package com.example.m1kes.parkingdemo.printer.bluetooth.connect;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.printer.bluetooth.DeviceItem;
import com.example.m1kes.parkingdemo.printer.bluetooth.callbacks.OnDeviceConnected;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;


public class ConnectDevice extends AsyncTask<DeviceItem,Void,Void> {

    private Context context;
    private Handler mHandler;
    private BluetoothAdapter BTAdapter;
    private UUID UUID;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private ProgressDialog dialog;
    private OnDeviceConnected response;
    private boolean hasConnected = false;

    public ConnectDevice(Context context, BluetoothAdapter BTAdapter,UUID UUID,OnDeviceConnected response){
        this.context = context;
        this.BTAdapter = BTAdapter;
        this.UUID = UUID;
        this.response = response;
        mHandler = new Handler();
    }



    @Override
    protected Void doInBackground(DeviceItem... params) {

        final DeviceItem deviceItem = params[0];


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.setMessage("Connecting to Printer....");
                dialog.show();
            }
        });

        BluetoothSocket mmSocket= null;
        BluetoothDevice mmDevice = null;
        hasConnected = ConnectDevice(BTAdapter,mmSocket,mmDevice,deviceItem.getAddress(),UUID);
        if (hasConnected)
        {
            DisConnect(mmSocket);
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });

        if(hasConnected){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("Connected to Printer");
                    Toast.makeText(context,"Connected!",Toast.LENGTH_SHORT).show();
                }
            });
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    response.onConnectSuccess(deviceItem);
                }
            });
        }else{

            mHandler.post(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage("Failed to Connect");
                Toast.makeText(context, "Failed to Connect!", Toast.LENGTH_SHORT).show();
            }
        });

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    response.onConnectFail();
                }
            });
        }

        return null;
    }


    private boolean ConnectDevice(BluetoothAdapter BTAdapter,BluetoothSocket mmSocket, BluetoothDevice mmDevice,
                                  String toothAddress,UUID MY_UUID)
    {
        boolean bRet=false;

        BTAdapter.cancelDiscovery();
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        try
        {

            mmDevice = BTAdapter.getRemoteDevice(toothAddress);
            if(Build.VERSION.SDK_INT < 15)
            {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }
            else
            {
                mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            }
            BTAdapter.cancelDiscovery();
            Thread.sleep(500);
            if(BTAdapter.isDiscovering())
            {
                int iCnt=0;
                while (iCnt<5)
                {
                    Thread.sleep(500);
                    iCnt++;
                    if(BTAdapter.cancelDiscovery())
                    {
                        break;
                    }
                }
            }
            mmSocket.connect();
        }
        catch (Exception e)
        {
            try
            {
                Method m;
                m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                mmSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
                if(BTAdapter.isDiscovering())
                {
                    int iCnt=0;
                    while (iCnt<5)
                    {
                        Thread.sleep(500);
                        iCnt++;
                        if(BTAdapter.cancelDiscovery())
                        {
                            break;
                        }
                    }
                }
                mmSocket.connect();
            }
            catch (Exception e1)
            {
                Log.d("PRTLIB", (new StringBuilder("BTO_ConnectDevice --> create ")).append(e1.getMessage()).toString());
                return false;
            }
        }

        try
        {
            bRet=GetIOInterface(mmSocket);
            return bRet;
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }


    private boolean GetIOInterface(BluetoothSocket mmSocket)
    {
        Log.d("PRTLIB", "BTO_GetIOInterface...");
        try
        {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        }
        catch(IOException e)
        {
            Log.d("PRTLIB", (new StringBuilder("BTO_GetIOInterface ")).append(e.getMessage()).toString());
            return false;
        }
        return true;
    }
    private boolean DisConnect(BluetoothSocket mmSocket)
    {
        boolean bRet = true;
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        try
        {
            if(mmInStream!=null)
            {
                mmInStream.close();
                mmInStream=null;
            }
            if(mmOutStream!=null)
            {
                mmOutStream.close();
                mmOutStream=null;
            }
            if(mmSocket!=null)
            {
                mmSocket.close();
                mmSocket=null;
            }
        }
        catch (IOException e)
        {
            System.out.println((new StringBuilder(
                    "BTO_ConnectDevice close ")).append(e.getMessage())
                    .toString());
            bRet = false;
        }
        return bRet;
    }


}
