package com.example.deviceinfoappjava;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Size;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import kotlin.math.MathKt;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    SensorManager sm = null;
    List list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        textView = this.findViewById(R.id.displayInfo);

        list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (list.size() > 0) {
            sm.registerListener(sel, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(getBaseContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }

        try {
            textView.setText(this.getSystemDetails());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    SensorEventListener sel = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @SuppressLint("SetTextI18n")
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            String sensor = "x: " + values[0] + "\ny: " + values[1] + "\nz: " + values[2];
            try {
                textView.setText(getSystemDetails() + sensor);
            } catch (CameraAccessException e) {
                Toast.makeText(MainActivity.this, "Camera Access Exception Occurred", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressLint({"HardwareIds"})
    private String getSystemDetails() throws CameraAccessException {
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

    private String getCamerasMegaPixel() throws CameraAccessException {
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

    private int calculateMegaPixel(float width, float height) {
        return MathKt.roundToInt(width * height / (float) 1024000);
    }

    private String getRAM() {
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

    private String getBatteryInfo() {
        Object service = this.getSystemService(Context.BATTERY_SERVICE);
        if (service == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.os.BatteryManager");
        } else {
            BatteryManager batLevel = (BatteryManager) service;
            return "Battery: " + batLevel.getIntProperty(4) + " \n";
        }
    }

}