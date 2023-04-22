package com.example.deviceinfoappjava;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Size;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.math.MathKt;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View textView = this.findViewById(R.id.displayInfo);
        try {
            ((TextView) textView).setText(this.getSystemDetails());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint({"HardwareIds"})
    private  String getSystemDetails() throws CameraAccessException {
        return "Manufacture: " + Build.MANUFACTURER + " \n" +
                "Model: " + Build.MODEL + " \n" +
                "Brand: " + Build.BRAND + " \n" +
                this.getRAM() +
                this.getBatteryInfo() +
                "Version Code: " + Build.VERSION.RELEASE + '\n' +
                "Incremental: " + Build.VERSION.INCREMENTAL + " \n" +
                this.getCamerasMegaPixel() +
                "SDK: " + Build.VERSION.SDK_INT + " \n" +
                "\nDeviceID: " + Settings.Secure.getString(this.getContentResolver(), "android_id") + " \n" +
                "ID: " + Build.ID + " \n" +
                "User: " + Build.USER + " \n" +
                "Type: " + Build.TYPE + " \n" +
                "Board: " + Build.BOARD + " \n" +
                "Host: " + Build.HOST + " \n" +
                "FingerPrint: " + Build.FINGERPRINT + " \n";
    }

    private String getIMEI() {
        Object service = this.getSystemService(Context.TELEPHONY_SERVICE);
        if (service == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.telephony.TelephonyManager");
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) service;
            return Build.VERSION.SDK_INT >= 26 ? "IMEI number : " + telephonyManager.getImei() + " \n" : "";
        }
    }

    private  String getCamerasMegaPixel() throws CameraAccessException {
        String output;
        Object service = this.getSystemService(Context.CAMERA_SERVICE);
        if (service == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.hardware.camera2.CameraManager");
        } else {
            CameraManager manager = (CameraManager) service;
            String[] list = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(list[0]);

            StringBuilder str = (new StringBuilder()).append("Back camera MP: ");
            Size size = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
            output = str.append(this.calculateMegaPixel((float) size.getWidth(), (float) size.getHeight())).append("\n").toString();

            characteristics = manager.getCameraCharacteristics(list[1]);
            str = (new StringBuilder()).append(output).append("Front camera MP: ");
            size = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
            output = str.append(this.calculateMegaPixel((float) size.getWidth(), (float) size.getHeight())).append("\n").toString();
            return output;
        }
    }

    private  int calculateMegaPixel(float width, float height) {
        return MathKt.roundToInt(width * height / (float) 1024000);
    }

    private  String getRAM() {
        Object service = this.getSystemService(Context.ACTIVITY_SERVICE);
        if (service == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.app.ActivityManager");
        } else {
            ActivityManager actManager = (ActivityManager) service;
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            double totalMemory = (double) memInfo.availMem / (double) 1073741824;
            double availMemory = Math.rint(totalMemory);
            totalMemory = Math.rint((double) memInfo.totalMem / (double) 1073741824);
            return "Available RAM: " + availMemory + "\nTotal RAM: " + totalMemory + " \n";
        }
    }

    private  String getBatteryInfo() {
        Object service = this.getSystemService(Context.BATTERY_SERVICE);
        if (service == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.os.BatteryManager");
        } else {
            BatteryManager batLevel = (BatteryManager) service;
            return "Battery: " + batLevel.getIntProperty(4) + " \n";
        }
    }

}