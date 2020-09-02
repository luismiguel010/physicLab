package com.example.physiclab.features.sensors.movement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.physiclab.R;

public class MenuMovementSensor extends AppCompatActivity {

    private ImageButton btnAccelerometer, btnAccelerometer2, btnGyro;
    private SensorManager sensorManager;
    private TextView dissableAccelerometer, disableAccelerometer2, disableGyro, textinfoAccelG,
            textinfoAccel, textInfoGyro;
    private Toolbar toolbar;
    private Menu menu;

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
        textinfoAccelG = findViewById(R.id.textinfoAccelG);
        textinfoAccel = findViewById(R.id.textinfoAccel);
        textInfoGyro = findViewById(R.id.textInfoGyro);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sensores de movimiento");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            textinfoAccelG.setClickable(true);
            btnAccelerometer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentAccelView = new Intent(getApplicationContext(), AccelerometerView.class);
                    startActivity(intentAccelView);
                }
            });
            textinfoAccelG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
            textinfoAccel.setClickable(true);
            btnAccelerometer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentAccelWithouGView = new Intent(getApplicationContext(), AccelerometerWithoutG.class);
                    startActivity(intentAccelWithouGView);
                }
            });
            textinfoAccel.setOnClickListener(new View.OnClickListener() {
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
            textInfoGyro.setClickable(true);
            btnGyro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentGyro = new Intent(getApplicationContext(), GyroscopeView.class);
                    startActivity(intentGyro);
                }
            });
            textInfoGyro.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}