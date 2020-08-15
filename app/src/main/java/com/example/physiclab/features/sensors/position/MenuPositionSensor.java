package com.example.physiclab.features.sensors.position;

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

public class MenuPositionSensor extends AppCompatActivity {

    private ImageButton btnMagnetometer, btnOrientation, btnProximity;
    private TextView disableMagne, disableOrientation, disableProximity;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_position_sensor);
        btnMagnetometer = findViewById(R.id.btnMagnetometer);
        btnOrientation = findViewById(R.id.btnOrientation);
        btnProximity = findViewById(R.id.btnProx);
        disableMagne = findViewById(R.id.disable_magne);
        disableOrientation = findViewById(R.id.disable_orientation);
        disableProximity = findViewById(R.id.disable_proximity);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            btnMagnetometer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentMagne = new Intent(getApplicationContext(), Magnetometer.class);
                    startActivity(intentMagne);
                }
            });
        }else{
            disableMagne.setText("*No disponible");
            btnMagnetometer.setClickable(false);
            btnMagnetometer.setEnabled(false);
        }

        if ((sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) &&
                (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)) != null){
            btnOrientation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentOrient = new Intent(getApplicationContext(), Orientation.class);
                    startActivity(intentOrient);
                }
            });
        }else{
            disableOrientation.setText("*No disponible");
            btnOrientation.setClickable(false);
            btnOrientation.setEnabled(false);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            btnProximity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentProx = new Intent(getApplicationContext(), Proximity.class);
                    startActivity(intentProx);
                }
            });
        }else{
            disableProximity.setText("*No disponible");
            btnProximity.setClickable(false);
            btnProximity.setEnabled(false);
        }
    }
}