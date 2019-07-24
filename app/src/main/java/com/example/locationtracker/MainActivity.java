package com.example.locationtracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private static MainActivity instance;

    private String heading;
    private float azimuth;
    float[] mGravity;
    float[] mGeomagnetic;

    private SensorManager sensorManager;

    private TextView textView;
    int level;

    // getting current battery level
    private BroadcastReceiver chargeLevelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        // registering accelerometer and magnetometer sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Sensor sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(MainActivity.this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);

        // Getting Android ID
        @SuppressLint("HardwareIds")
        final String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        textView = findViewById(R.id.tv_location);
        textView.setText(new StringBuilder().append("AndroidID: ").append(androidId).toString());

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        this.registerReceiver(this.chargeLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        startAlarm();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    // method for sending data after certain interval
   private void startAlarm() {
       AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       Intent intent = new Intent(this, AlarmReceiver.class);
       PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
       Calendar calendar = Calendar.getInstance();
//       calendar.add(Calendar.SECOND, 15);
//       long time = System.currentTimeMillis();
       alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 15, pendingIntent);
   }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);

        Toast.makeText(this, "Tracking Stopped.", Toast.LENGTH_SHORT).show();
    }

    // getting Heading
    @Override
    public void   onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                float orientation[] = new  float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = orientation[0];
            }
        }

        heading = String.valueOf((float)(Math.toDegrees(azimuth)+ 360) % 360);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public String getBatteryLevel()
    {
        return String.valueOf(level);
    }
    public String getHeading() {return heading;}
}
