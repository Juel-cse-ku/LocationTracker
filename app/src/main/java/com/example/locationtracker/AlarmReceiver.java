package com.example.locationtracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {
//    public static final String API_URL = "http://control.jahajibd.com/api_req/Ship/GpsTracking";
    public static final String API_URL = "http://192.168.0.122/shipTracking/post.php";
    private String latitude;
    private String longitude;
    private String heading;



    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());


//        ActivityCompat.requestPermissions(get, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        GPSTracker gpsTracker = new GPSTracker(context);
        Location location = gpsTracker.getLocation();

        if (location != null) {


           latitude = String.valueOf(location.getLatitude());
           longitude = String.valueOf(location.getLongitude());
        }else {
            Toast.makeText(context, "Location Not Received", Toast.LENGTH_LONG).show();
        }

        Heading h = new Heading(context);
        heading = h.getHeading();


        postToServer(API_URL, context, latitude, longitude);

    }

    private void postToServer(String api_url, final Context context, final String latitude, final String longitude) {
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID); //"a39676ad6a299538"

//                String m_androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);


                params.put("serial", androidId);
                params.put("lat", latitude);
                params.put("lng", longitude);
                params.put("heading", heading);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
