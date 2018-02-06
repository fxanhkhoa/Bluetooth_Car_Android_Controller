package com.example.anhkhoa.ble_car_controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class joystick_Controller extends AppCompatActivity implements JoystickView.JoystickListener {

    private TextView tDeviceName;
    private TextView tDeviceAddress;
    private TextView tStatus;
    private TextView tdata_read;
    private TextView xtextview;
    private TextView ytextview;

    private boolean mConnected = false;

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic mcharacteristic;

    Global g = Global.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick__controller);

        tDeviceName = (TextView) findViewById(R.id.tdevice_name);
        tDeviceAddress = (TextView) findViewById(R.id.tdevice_address);
        tStatus = (TextView) findViewById(R.id.Status);
        tdata_read = (TextView) findViewById(R.id.data_read);
        xtextview = (TextView) findViewById(R.id.xfield);
        ytextview = (TextView) findViewById(R.id.yfield);

        tDeviceName.setText(g.getBLE_Name());
        tDeviceAddress.setText(g.getBLE_Address());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(g.getBLE_Address());
        mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);

        tStatus.setText("Connected");

        JoystickView joystickView = new JoystickView(this);
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //intentAction = ACTION_GATT_CONNECTED;
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //intentAction = ACTION_GATT_DISCONNECTED;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            displayGattServices(mBluetoothGatt.getServices());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            joystick_Controller.this.runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run() {
                    byte[] value = characteristic.getValue();
                    String data = new String(value);
                    tdata_read.setText(data);
                }
            });
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            final String uuid = gattService.getUuid().toString();
            joystick_Controller.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tdata_read.append("Service disovered: " + uuid + "\n");
                }
            });

            new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                final String charUuid = gattCharacteristic.getUuid().toString();
                charas.add(gattCharacteristic);
                joystick_Controller.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //tdata_read.append("Characteristic discovered for service: " + charUuid + "\n");
                    }
                });
            }
            mGattCharacteristics.add(charas);
        }
        mcharacteristic = mGattCharacteristics.get(2).get(0);
        final int charaPop = mcharacteristic.getProperties();
        if ((charaPop | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (mNotifyCharacteristic != null) {
                setCharacteristicNotification(mNotifyCharacteristic, false);
            }
        }
        if ((charaPop | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = mcharacteristic;
            setCharacteristicNotification(mcharacteristic, true);
        }
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            //Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        //if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
        //}
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        xtextview.setText("X:" + String.valueOf(xPercent));
        ytextview.setText("Y:" + String.valueOf(yPercent));
//        String sx = String.format("%.2f", xPercent);
//        String sy = String.format("%.2f", yPercent);
        xPercent *= 100;
        yPercent *= 100;
        mcharacteristic.setValue((int) xPercent + " " + (int) yPercent + "*");
        mBluetoothGatt.writeCharacteristic(mcharacteristic);
    }
}
