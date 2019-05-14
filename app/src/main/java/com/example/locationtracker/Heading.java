package com.example.locationtracker;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class Heading extends Activity implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    float[] mGravity;
    float[] mGeomagnetic;
    String heading = "0.0";

    public Heading(Context context) {
        this.context = context;
    }

    public Heading() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    protected void onResume() {
        super.onResume();
        heading = "OR";

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        Sensor sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Sensor sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
            heading = "BB";
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
            heading = "CC";
        }
        heading = "event";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    String getHeading() {
        double azimuth = 0.0;
        heading = "GH";



        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                azimuth = orientation[0];
            }
            heading = String.valueOf((float)(Math.toDegrees(azimuth)+ 360) % 360);

        }
        return heading;
    }
}
