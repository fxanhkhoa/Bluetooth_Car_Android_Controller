package com.example.anhkhoa.ble_car_controller;

/**
 * Created by Anh Khoa on 2/5/2018.
 */

public class Global {
    private static Global instance;

    // Global variable
    private static String BLE_Name;
    private static String BLE_Address;

    // Restrict the constructor from being instantiated
    private Global(){}

    public void setBLE_Name(String s){
        this.BLE_Name = s;
    }

    public void setBLE_Address(String s){
        this.BLE_Address = s;
    }

    public String getBLE_Name() {
        return BLE_Name;
    }

    public String getBLE_Address() {
        return BLE_Address;
    }

    public static synchronized Global getInstance(){
        if (instance == null){
            instance = new Global();
        }
        return instance;
    }
}
