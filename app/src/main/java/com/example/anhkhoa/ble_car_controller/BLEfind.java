package com.example.anhkhoa.ble_car_controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class BLEfind extends AppCompatActivity {

    //private android.support.v7.widget.Toolbar mToolbar;

    private Button BTN_Scan;
    private ListView mDevicesListView;

    private LocationManager _LocationManager;

    private BluetoothAdapter mBTAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private Handler mHandler; // Our main handler that will receive callback notifications

    private boolean mScanning;

    private static final long SCAN_PERIOD = 10000;
    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    private final static int REQUEST_ENABLE_LOCATION = 4;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blefind);

        BTN_Scan = (Button) findViewById(R.id.Find_Device_BTN);
        mHandler = new Handler();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        //Get Adapter
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTAdapter = bluetoothManager.getAdapter();

        mDevicesListView = (ListView) findViewById(R.id._List);
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mDevicesListView.setAdapter(mLeDeviceListAdapter);

        _LocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // Get service of Location
        // Request turn bluetooth on
        if (!mBTAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            if (mBTAdapter.isEnabled())
                Toast.makeText(getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }

        // Turn on GPS
        if (!_LocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent enableLCIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(enableLCIntent, REQUEST_ENABLE_LOCATION);
        }

        BTN_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run() {
                    mScanning = false;
                    mBTAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBTAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBTAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // device scan call back
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(bluetoothDevice);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final BluetoothDevice device = mLeDeviceListAdapter.getDevice(i);
            if (device == null) return;
            //Intent intent = new Intent(getApplicationContext(), BLECommunication.class);
            //intent.putExtra(BLECommunication.EXTRAS_DEVICE_NAME, device.getName());
            //intent.putExtra(BLECommunication.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//            SharedPreferences pre = getSharedPreferences("BLE_DATA", MODE_PRIVATE);
//            SharedPreferences.Editor edit = pre.edit();
//            edit.putString("BLE_NAME", device.getName());
//            edit.putString("BLE_ADDRESS", device.getAddress());
//            edit.commit();
            Global g = Global.getInstance();
            g.setBLE_Name(device.getName());
            g.setBLE_Address(device.getAddress());
            if (mScanning) {
                mBTAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            //startActivity(intent);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), device.getName() + " Selected", Toast.LENGTH_SHORT).show();
                }
            });
            BLEfind.super.onBackPressed();

        }
    };

    private class LeDeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = BLEfind.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
