package com.example.warroomapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesMachine {
    private static final String PREF_NAME = "MachinePrefs";
    private static final String KEY_PK = "pk";
    private static final String KEY_PLANT = "plant";
    private static final String KEY_LINE = "line";
    private static final String KEY_MACHINE = "machine";
    private static final String KEY_EQUIP_CODE = "equipmentCode";
    private static final String KEY_EQUIP_TYPE = "equipmentType";
    private static final String KEY_CAMERA_IP1 = "cameraIp1";
    private static final String KEY_CAMERA_IP2 = "cameraIp2";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TYPEOF = "typeOf";
    private static final String KEY_ISSUED_DATE = "issuedDate";
    private static final String KEY_ENDED_DATE = "endedDate";

    private SharedPreferences preferences;

    public SharedPreferencesMachine(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public void saveMachineData(Integer pk, String plant, String line, String machine, String equipCode,
                                String equipType, String cameraIp1, String cameraIp2, String name, String description, String typeOf, String issuedDate, String endedDate){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_PK, pk);
        editor.putString(KEY_PLANT, plant);
        editor.putString(KEY_LINE, line);
        editor.putString(KEY_MACHINE, machine);
        editor.putString(KEY_EQUIP_CODE, equipCode);
        editor.putString(KEY_EQUIP_TYPE, equipType);
        editor.putString(KEY_CAMERA_IP1, cameraIp1);
        editor.putString(KEY_CAMERA_IP2, cameraIp2);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_DESCRIPTION, description);
        editor.putString(KEY_TYPEOF, typeOf);
        editor.putString(KEY_ISSUED_DATE, issuedDate);
        editor.putString(KEY_ENDED_DATE, endedDate);
        editor.apply();
    }

    public int getPk() {
        return preferences.getInt(KEY_PK, 0);
    }
    public String getPlant() {
        return preferences.getString(KEY_PLANT, "");
    }
    public String getLine() {
        return preferences.getString(KEY_LINE, "");
    }
    public String getMachine() {
        return preferences.getString(KEY_MACHINE, "");
    }
    public String getEquipCode() {
        return preferences.getString(KEY_EQUIP_CODE, "");
    }
    public String getEquipType() {
        return preferences.getString(KEY_EQUIP_TYPE, "");
    }
    public String getCameraIp1() {
        return preferences.getString(KEY_CAMERA_IP1, "");
    }
    public String getCameraIp2() {
        return preferences.getString(KEY_CAMERA_IP2, "");
    }
    public String getName() {
        return preferences.getString(KEY_NAME, "");
    }
    public String getDescription() {
        return preferences.getString(KEY_DESCRIPTION, "");
    }
    public String getType() {
        return preferences.getString(KEY_TYPEOF, "");
    }
    public String getIssuedDate() {
        return preferences.getString(KEY_ISSUED_DATE, "");
    }
    public String getEndedDate() {
        return preferences.getString(KEY_ENDED_DATE, "");
    }
}
