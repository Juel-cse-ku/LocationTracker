package com.example.locationtracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

//        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
//        Location location = gpsTracker.getLocation();
//
//        if (location != null) {
//            double lat = location.getLatitude();
//            double lng = location.getLongitude();
//
//            textView.setText("Lat: "+lat+"\nLng: "+lng);
//            Toast.makeText(getApplicationContext(), "Lat: "+lat+"Lng: "+lng, Toast.LENGTH_LONG).show();
//        }else {
//            Toast.makeText(getApplicationContext(), "Location Not Received", Toast.LENGTH_LONG).show();
//        }
        

        startAlarm();
    }

   private void startAlarm() {
       AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       Intent intent = new Intent(this, AlarmReceiver.class);
       PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
       Calendar calendar = Calendar.getInstance();
//       calendar.add(Calendar.SECOND, 15);
//       long time = System.currentTimeMillis();
       alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30000, pendingIntent);
//       alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1500, pendingIntent);
   }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);

        Toast.makeText(this, "Tracking Stopped.", Toast.LENGTH_SHORT).show();
    }
}
