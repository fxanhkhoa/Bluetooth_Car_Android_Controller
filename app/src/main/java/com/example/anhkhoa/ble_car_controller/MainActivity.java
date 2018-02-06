package com.example.anhkhoa.ble_car_controller;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Button btn_findBLE;
    private Button btn_Controller;
    private TextView tBLE_Name;
    private TextView tBLE_Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_findBLE = (Button) findViewById(R.id.BTN_FindBLE);
        btn_Controller = (Button) findViewById(R.id.BTN_Controller);

        btn_findBLE.setOnClickListener(btn_findBLEOnclick);
        btn_Controller.setOnClickListener(btn_ControllerOnClick);

        tBLE_Name = (TextView) findViewById(R.id.BLE_Name);
        tBLE_Address = (TextView) findViewById(R.id.BLE_Address);

        // Grant Permission from User allowing location use
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        btn_Controller.setEnabled(false);
        btn_Controller.setTextColor(Color.WHITE);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onResume() {
        super.onResume();
//        SharedPreferences pre = getSharedPreferences("BLE_DATA", MODE_PRIVATE);
//        tBLE_Name.setText(pre.getString("BLE_NAME", "NAME"));
//        tBLE_Address.setText(pre.getString("BLE_ADDRESS", "ADDRESS"));
        Global g = Global.getInstance();
        tBLE_Name.setText(g.getBLE_Name());
        tBLE_Address.setText(g.getBLE_Address());
        if (g.getBLE_Address() != null){
            btn_Controller.setEnabled(true);
            btn_Controller.setTextColor(R.color.BTN_COLOR);
        }
    }

    View.OnClickListener btn_findBLEOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), BLEfind.class);
            startActivity(intent);
        }
    };

    View.OnClickListener btn_ControllerOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            MainActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
                Intent intent = new Intent(getApplicationContext(), joystick_Controller.class);
                startActivity(intent);
            }
    };
}
