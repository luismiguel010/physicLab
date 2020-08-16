package com.example.physiclab.features.sensors.environmental;

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
import com.example.physiclab.features.sensors.movement.AccelerometerView;
import com.example.physiclab.features.sensors.movement.AccelerometerWithoutG;
import com.example.physiclab.features.sensors.movement.GyroscopeView;

public class MenuEnvironmental extends AppCompatActivity {

    private ImageButton btnTemp, btnLight, btnPressure;
    private SensorManager sensorManager;
    private TextView dissableTemp, disableLigh, disablePress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_environmental);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        btnTemp = findViewById(R.id.btnTemp);
        btnLight = findViewById(R.id.btnLight);
        btnPressure = findViewById(R.id.btnPressure);
        dissableTemp = findViewById(R.id.disable_temp);
        disableLigh = findViewById(R.id.disable_light);
        disablePress = findViewById(R.id.disable_pressure);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            btnTemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentTemp= new Intent(getApplicationContext(), Temprature.class);
                    startActivity(intentTemp);
                }
            });
        } else {
            dissableTemp.setText("*No disponible");
            btnTemp.setClickable(false);
            btnTemp.setEnabled(false);
        }
    }
}