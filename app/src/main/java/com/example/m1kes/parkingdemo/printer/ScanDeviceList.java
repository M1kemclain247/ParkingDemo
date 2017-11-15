package com.example.m1kes.parkingdemo.printer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.adapters.listviews.DeviceItemAdapter;
import com.example.m1kes.parkingdemo.adapters.listviews.callbacks.OnDeviceItemClicked;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterConnected;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.printer.bluetooth.DeviceItem;
import com.example.m1kes.parkingdemo.printer.bluetooth.callbacks.OnDeviceConnected;
import com.example.m1kes.parkingdemo.printer.bluetooth.connect.ConnectDevice;
import com.example.m1kes.parkingdemo.printer.tasks.ConnectBluetoothPrinter;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import HPRTAndroidSDK.HPRTPrinterHelper;

public class ScanDeviceList extends AppCompatActivity {


    private static int COUNT = 0;
    private Context context;
    private List<DeviceItem> scannedDevices;
    private List<DeviceItem> pairedDevices;

    private ListView listScanned,listPaired;
    private ProgressBar scan_progressBluetooth;
    private Button btnEnableBluetooth,btnScanNewDevices;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private BluetoothAdapter BTAdapter;
    public static int REQUEST_BLUETOOTH = 1;
    private DeviceItemAdapter pairedAdapter;
    private DeviceItemAdapter newDevicesAdapter;
    private String failedName = null;
    public static String printerType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_scan_device_list);
        setResult(Activity.RESULT_CANCELED);
        this.setFinishOnTouchOutside(false);

        context = ScanDeviceList.this;
        getPrinterType();
        initGui();
        turnOnBluetooth();
        getPairedData();
        setUpOnClicks();

    }

    private boolean getPrinterType() {
        Intent i = getIntent();
        if (i == null) {
            return false;
        }
        Bundle b = i.getExtras();
        if (b == null) {
            return false;
        }
        try {
            printerType = b.getString("Printer-Type");
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    private void initGui(){
        scannedDevices = new ArrayList<>();
        pairedDevices = new ArrayList<>();


        btnScanNewDevices = (Button)findViewById(R.id.btnScanNewDevices);
        listScanned = (ListView)findViewById(R.id.new_ScannedDevices);
        listPaired = (ListView)findViewById(R.id.paired_devicesList);
        scan_progressBluetooth = (ProgressBar)findViewById(R.id.scan_progressBluetooth);

    }

    private void turnOnBluetooth(){

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(context)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        //if bluetooth isn't already enabled then enable it
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
            Toast.makeText(context,"Enabled Bluetooth",Toast.LENGTH_SHORT).show();
        }



    }

    private void getPairedData(){

        pairedDevices = new ArrayList<DeviceItem>();

        Set<BluetoothDevice> pairedData = BTAdapter.getBondedDevices();
        if (pairedData.size() > 0) {
            for (BluetoothDevice device : pairedData) {
                DeviceItem newDevice= new DeviceItem(device.getName(),device.getAddress(),"false");
                pairedDevices.add(newDevice);
            }
        }
        // If there are no devices, add an item that states so. It will be handled in the view.
        if(pairedData.size() == 0) {
            pairedDevices.add(new DeviceItem("No Devices", "","false"));
        }
        Log.d("DEVICELIST", "DeviceList populated\n");
        pairedAdapter = new DeviceItemAdapter(context,R.layout.bluetooth_device_item, pairedDevices,onDeviceItemClicked);
        listPaired.setAdapter(pairedAdapter);


    }


    OnDeviceItemClicked onDeviceItemClicked = new OnDeviceItemClicked() {
        @Override
        public void onClicked(DeviceItem deviceItem) {

            new ConnectDevice(context,BTAdapter,MY_UUID,onDeviceConnected).execute(deviceItem);

        }
    };



    OnDeviceConnected onDeviceConnected = new OnDeviceConnected() {
        @Override
        public void onConnectSuccess(DeviceItem deviceItem) {

            Intent intent = new Intent();
            intent.putExtra("is_connected", (true)?"OK":"NO");
            intent.putExtra("BTAddress", deviceItem.getAddress());

            if(deviceItem.getAddress()!=null) {
                if (deviceItem.getDeviceName() == null) {
                    deviceItem.setDeviceName(ScanDeviceList.printerType);
                }
                if (deviceItem.getDeviceName().contains("MZ220")||deviceItem.getDeviceName().contains("XXXX")) {
                    deviceItem.setDeviceName("MZ220");
                    Toast.makeText(context, "Connected To Printer", Toast.LENGTH_SHORT).show();
                    intent.putExtra("PrinterModel", deviceItem.getDeviceName());
                    setResult(Activity_DeviceList.ZEBRA_CONNECT_RESULT, intent);
                    finish();
                } else if (deviceItem.getDeviceName().contains("MPT-II")) {
                    deviceItem.setDeviceName("MPT-II");
                    Toast.makeText(context, "Connected To Printer", Toast.LENGTH_SHORT).show();
                    intent.putExtra("PrinterModel", deviceItem.getDeviceName());
                    setResult(HPRTPrinterHelper.ACTIVITY_CONNECT_BT, intent);
                    finish();
                } else {
                    Toast.makeText(context, "Unsupported Device!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context,"Reconnect to printer",Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onConnectFail() {
            Toast.makeText(context,"Failed to Connect",Toast.LENGTH_SHORT).show();
        }
    };



    private void setUpOnClicks(){

        btnScanNewDevices.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                if(BTAdapter!=null) {

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    registerReceiver(bReciever, filter);
                    scannedDevices.clear();
                    newDevicesAdapter = new DeviceItemAdapter(context, R.layout.bluetooth_device_item, scannedDevices, onDeviceItemClicked);
                    listScanned.setAdapter(newDevicesAdapter);
                    BTAdapter.startDiscovery();




                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        boolean bluetoothScanning = BTAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                            @Override
                            public void onLeScan(final BluetoothDevice device, final int rssi,
                                                 byte[] scanRecord) {
                                final BleAdvertisedData badata = BleUtil.parseAdertisedData(scanRecord);
                                String deviceName = device.getName();
                                if( deviceName == null ){
                                    deviceName = badata.getName();
                                }
                                failedName = deviceName;
                                System.out.println("Name From LeScan: "+deviceName);
                            }
                        }); // this is true as well
                    }


                }
            }
        });

    }



    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                Toast.makeText(context,"Scanning....",Toast.LENGTH_SHORT).show();
                setSupportProgressBarIndeterminateVisibility(true);
                setTitle(R.string.activity_devicelist_scanning);
                findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
                scan_progressBluetooth.setVisibility(View.VISIBLE);
                btnScanNewDevices.setVisibility(View.GONE);
                COUNT++;

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                scan_progressBluetooth.setVisibility(View.GONE);
                setSupportProgressBarIndeterminateVisibility(false);
                setTitle(R.string.activity_devicelist_select_device);
                Toast.makeText(context,"Finished Searching",Toast.LENGTH_SHORT).show();
                btnScanNewDevices.setVisibility(View.VISIBLE);

                if(COUNT==1){
                    BTAdapter.cancelDiscovery();
                    scannedDevices.clear();
                    newDevicesAdapter = new DeviceItemAdapter(context, R.layout.bluetooth_device_item, scannedDevices, onDeviceItemClicked);
                    listScanned.setAdapter(newDevicesAdapter);
                    BTAdapter.startDiscovery();
                }

            }else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                Log.d("DEVICELIST", "Bluetooth device found\n");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                DeviceItem newDevice = null;
                if(device.getName()==null){
                    newDevice= new DeviceItem(failedName, device.getAddress(), "false");
                }else{
                    newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                }
                // Add it to our adapter
                scannedDevices.add(newDevice);
                newDevicesAdapter.notifyDataSetChanged();

            }
        }
    };




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}


////////////////////// Helper Classes: BleUtil and BleAdvertisedData ///////////////
final class BleUtil {
    private final static String TAG=BleUtil.class.getSimpleName();
    public static BleAdvertisedData parseAdertisedData(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();
        String name = null;
        if( advertisedData == null ){
            return new BleAdvertisedData(uuids, name);
        }

        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0) break;

            byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2) {
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;
                case 0x09:
                    byte[] nameBytes = new byte[length-1];
                    buffer.get(nameBytes);
                    try {
                        name = new String(nameBytes, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }
        return new BleAdvertisedData(uuids, name);
    }
}


 class BleAdvertisedData {
    private List<UUID> mUuids;
    private String mName;
    public BleAdvertisedData(List<UUID> uuids, String name){
        mUuids = uuids;
        mName = name;
    }

    public List<UUID> getUuids(){
        return mUuids;
    }

    public String getName(){
        return mName;
    }
}