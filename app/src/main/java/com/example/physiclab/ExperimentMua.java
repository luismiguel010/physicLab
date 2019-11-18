package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.FloatMath;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;


public class ExperimentMua extends Activity implements SensorEventListener {

    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Chronometer chronometer;
    private long pauseOffset;
    private boolean flagStartMovement;
    private double distance;
    static final double gravityAceleration = 9.80665;
    private float time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_mua);
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        Button buttonStart = (Button)findViewById(R.id.btnStartMua);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if(mAccel > 3){
                starChronometer();
            }else if(flagStartMovement){
                time = pauseChronometer()/1000;
                restartChronometer();
                distance = 0.5*gravityAceleration*Math.pow(time,2);
                Toast.makeText(ExperimentMua.this,
                        "Distancia recorrida="+distance+" m", Toast.LENGTH_SHORT)
                        .show();
                flagStartMovement = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void starChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
        flagStartMovement = true;
    }

    public float pauseChronometer(){
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        return pauseOffset;
    }

    public void restartChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

}



