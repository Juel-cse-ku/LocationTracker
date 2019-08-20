package com.example.locationtracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver implements LocationListener {
//    public static final String API_URL = "https://control.jahajibd.com/api_req/Ship/GpsTracking";
    public static final String API_URL = "http://192.168.0.9/shipTracking/post.php";
    private String latitude = "";
    private String longitude = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        // creating notification
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());

        // getting current location
//        GPSTracker gpsTracker = new GPSTracker(context);
//        Location location = gpsTracker.getLocation();
//
//        if (location != null) {
//           latitude = String.valueOf(location.getLatitude());
//           longitude = String.valueOf(location.getLongitude());
//        }else {
//            Toast.makeText(context, "Location Not Received", Toast.LENGTH_LONG).show();
//        }

//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "Location Permission Not Granted", Toast.LENGTH_SHORT).show();
//        }
//
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//        if (isGPSEnabled) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, this);
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            latitude = String.valueOf(location.getLatitude());
//            longitude = String.valueOf(location.getLongitude());
//        }else {
//            Toast.makeText(context,"Please Enable GPS.", Toast.LENGTH_SHORT).show();
//        }

//        String heading = MainActivity.getInstance().getHeading();

        // getting current battery level

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(context, "Please, Enable Location Permission.", Toast.LENGTH_LONG).show();
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, this);

                if (locationManager != null){
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
//                        Toast.makeText(context, "Lat"+latitude+"lon"+longitude,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Location is Empty.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "LocationManager is Empty.", Toast.LENGTH_LONG).show();
                }
            }

        } else {
            Toast.makeText(context, "Please, Enable GPS.", Toast.LENGTH_LONG).show();
        }
//

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        String crg = String.valueOf(level);
        String heading = "123";

        // sending data to server
        postToServer(API_URL, context, latitude, longitude, heading, crg);
    }

    private void postToServer(String api_url, final Context context, final String latitude, final String longitude, final String heading, final String crg) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error+"", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                @SuppressLint("HardwareIds")
                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                params.put("serial", androidId);
                params.put("lat", latitude);
                params.put("lng", longitude);
                params.put("heading", heading);
                params.put("crg", crg);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
