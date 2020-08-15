package com.example.physiclab.features.sensors.movement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.physiclab.R;

public class MenuMovementSensor extends AppCompatActivity {

    private ImageButton btnAccelerometer, btnAccelerometer2, btnGyro;
    private SensorManager sensorManager;
    private TextView dissableAccelerometer, disableAccelerometer2, disableGyro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_movement_sensor);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        btnAccelerometer = findViewById(R.id.btnAccelerometer);
        btnAccelerometer2 = findViewById(R.id.btnAccelerometer2);
        btnGyro = findViewById(R.id.btnGyro);
        dissableAccelerometer = findViewById(R.id.disable_accelerometer);
        disableAccelerometer2 = findViewById(R.id.disable_accelerometerwithoutG);
        disableGyro = findViewById(R.id.disable_gyro);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            btnAccelerometer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentAccelView = new Intent(getApplicationContext(), AccelerometerView.class);
                    startActivity(intentAccelView);
                }
            });
        } else {
            dissableAccelerometer.setText("*No disponible");
            btnAccelerometer.setClickable(false);
            btnAccelerometer.setEnabled(false);
        }


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            btnAccelerometer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentAccelWithouGView = new Intent(getApplicationContext(), AccelerometerWithoutG.class);
                    startActivity(intentAccelWithouGView);
                }
            });
        }else {
            disableAccelerometer2.setText("*No disponible");
            btnAccelerometer2.setClickable(false);
            btnAccelerometer2.setEnabled(false);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            btnGyro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentGyro = new Intent(getApplicationContext(), GyroscopeView.class);
                    startActivity(intentGyro);
                }
            });
        }else {
            disableGyro.setText("*No disponible");
            btnGyro.setClickable(false);
            btnGyro.setEnabled(false);
        }




    }


}