package com.example.m1kes.parkingdemo.printer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import HPRTAndroidSDK.HPRTPrinterHelper;

public class Activity_DeviceList extends AppCompatActivity {

	public static final String TAG = "DeviceListActivity";
    public static final boolean D = true;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public BluetoothAdapter mBtAdapter;
    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
	private OutputStream mmOutStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    public List<String> pairedDeviceList=null;
    public List<String> newDeviceList=null;
    public ArrayAdapter<String> mPairedDevicesArrayAdapter;
    public ArrayAdapter<String> mNewDevicesArrayAdapter;
    public static String toothAddress=null;
    public static String toothName=null;
    private Context thisCon=null;
    private String strAddressList="";
	private ProgressBar scan_progress;
	private Button scanButton;

	public static final int ZEBRA_CONNECT_RESULT = 1456;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
		setContentView(R.layout.activity_devicelist);

		scan_progress = (ProgressBar)findViewById(R.id.scan_progress);
		scan_progress.setVisibility(View.GONE);
		scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
        	   strAddressList="";
                doDiscovery();
                v.setVisibility(View.GONE);
           }
        });
        
        thisCon=this.getApplicationContext();



        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getPairedData());
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);


        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(ACTION_PAIRING_REQUEST);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, intent); 
        try
        {
	        pairedListView.setOnItemClickListener(mDeviceClickListener);
	        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        }catch(Exception excpt)
        {
  	       Toast.makeText(this, thisCon.getString(R.string.activity_devicelist_get_device_err)+excpt, Toast.LENGTH_LONG).show();
        }
    }

    public List<String> getPairedData()
    {
        List<String> data = new ArrayList<String>();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		if (pairedDevices.size() > 0) 
		{
		    findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
		    for (BluetoothDevice device : pairedDevices) //����
		    {
		       data.add(device.getName() + "\n" + device.getAddress());
		    }
		} 
		else
		  {
		      String noDevices = getResources().getText(R.string.activity_devicelist_none_paired).toString();
		      data.add(noDevices);       
		  }
        return data;
    }
    
    @Override
	protected void onDestroy() 
    {
        super.onDestroy();
        if (mBtAdapter != null)        
           mBtAdapter.cancelDiscovery();
		unregisterReceiver(mReceiver);
	}

    /**
     BluetoothAdapter
	*/
    public void doDiscovery() 
    {
        if (D) Log.d(TAG, "doDiscovery()");
        setSupportProgressBarIndeterminateVisibility(true);
        setTitle(R.string.activity_devicelist_scanning);
		scan_progress.setVisibility(View.VISIBLE);
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        int intStartCount=0;
        while (!mBtAdapter.startDiscovery() && intStartCount<5)
        {
            Log.e("BlueTooth", "ɨ�賢��ʧ��");
            intStartCount++;
            try 
            {
                Thread.sleep(100);
            } 
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public OnItemClickListener mDeviceClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
        	boolean hasConnected=false;
        	try 
        	{ 
	        	if(mBtAdapter.isDiscovering())
	        	{
	        		mBtAdapter.cancelDiscovery();    
	        	}
	        	

	            String info = ((TextView) v).getText().toString();
	            toothAddress = info.substring(info.length() - 17);
				System.out.println("info: "+info);
	            if(!toothAddress.contains(":"))
	            { 
	            	return;
	            }


				if(info.contains("MZ220")){
					hasConnected = true;
				}else{
					hasConnected= ConnectDevice();
					if (hasConnected)
					{
						DisConnect();
					}
				}
	            

	            System.out.println("Is getting to end of Activty");
	            Intent intent = new Intent();
	            intent.putExtra("is_connected", (hasConnected)?"OK":"NO"); 
	            intent.putExtra("BTAddress", toothAddress);

				//if model is of zebra then set activity result to zebra
				if(info.contains("MZ220")){
					System.out.println("Is getting Inside the result changing");
					System.out.println("Returning Model : "+info.substring(0,6) +" To Other Activity");
					intent.putExtra("PrinterModel",info.substring(0,5));
					setResult(Activity_DeviceList.ZEBRA_CONNECT_RESULT, intent);
					finish();
					System.out.println("Should have Finished by now");
				}else{
					intent.putExtra("PrinterModel",info.substring(0,6));
					System.out.println("Returning Model : "+info.substring(0,6) +" To Other Activity MPT PRinters");
					setResult(HPRTPrinterHelper.ACTIVITY_CONNECT_BT, intent);
					finish();
				}



            }
            catch (Exception e)
            {  
            	 e.printStackTrace();
            } 
        	finally
        	{ 
        		finish();
        	}          	
        }
    };

	private void finishThis(){
		this.finish();
	}

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
            String action = intent.getAction();
            BluetoothDevice device = null;

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_NONE)
                { 
                	if(device.getBluetoothClass().getMajorDeviceClass()==1536)
                	{
                		if(!strAddressList.contains(device.getAddress()))
                		{
	                		strAddressList+=device.getAddress()+",";
	                		mNewDevicesArrayAdapter.add(device.getName()+ "\n" + device.getAddress());
							System.out.println("Printer Debugging: "+device.getName() + " "+device.getAddress());
                		}
                	}
                } 
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) { 
                case BluetoothDevice.BOND_BONDING:
                    Log.d("BlueToothTestActivity", "�������......");
                    break; 
                case BluetoothDevice.BOND_BONDED:
                    Log.d("BlueToothTestActivity", "������");
                    break; 
                case BluetoothDevice.BOND_NONE:
                    Log.d("BlueToothTestActivity", "ȡ�����");
                default: 
                    break; 
                } 
            } 
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
				setSupportProgressBarIndeterminateVisibility(false);
				scan_progress.setVisibility(View.GONE);
				scanButton.setVisibility(View.VISIBLE);
	            setTitle(R.string.activity_devicelist_select_device);
				// ListView listview = (ListView) findViewById(R.id.new_devices);
				//listview.setAdapter(mNewDevicesArrayAdapter);
	            if (mNewDevicesArrayAdapter.getCount() == 0) { }
            }           
        }
    };

    private boolean ConnectDevice()
	{
    	boolean bRet=false;
		
    	mBtAdapter.cancelDiscovery();
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

			mmDevice = mBtAdapter.getRemoteDevice(toothAddress);			
			if(Build.VERSION.SDK_INT < 15)
			{
				mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
			}
			else
			{
				mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			}									
			mBtAdapter.cancelDiscovery();
			Thread.sleep(500);
			if(mBtAdapter.isDiscovering())
        	{
				int iCnt=0;
				while (iCnt<5)
	    		{	  
					Thread.sleep(500);
					iCnt++;
					if(mBtAdapter.cancelDiscovery())
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
				if(mBtAdapter.isDiscovering())
	        	{
					int iCnt=0;
					while (iCnt<5)
		    		{	  
						Thread.sleep(500);
						iCnt++;
						if(mBtAdapter.cancelDiscovery())
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
			bRet=GetIOInterface();	    	
	    	return bRet;
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}    
    
    private boolean DisConnect()
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
    
    private boolean GetIOInterface()
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
}


