package com.example.physiclab;

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
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.physiclab.features.experiments.ExperimentMUAProx;
import com.example.physiclab.features.experiments.ExperimentMUCGyro;
import com.example.physiclab.features.guides.MUCGuide;
import com.example.physiclab.features.theories.MurTheory;

public class MucIntroduction extends AppCompatActivity {

    private ImageButton btnbook, btnlab;
    private SensorManager sensorManager;
    private TextView disableGyro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_muc_introduction);
        btnlab = findViewById(R.id.imagebutton_lab);
        btnbook = findViewById(R.id.imagebutton_book);
        disableGyro = findViewById(R.id.disable_gyro_experimentMUC);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            btnlab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ExperimentMUCGyro.class);
                    startActivity(intent);
                }
            });
        }else{
            disableGyro.setText(R.string.label_warning_sensor_gyro);
            btnlab.setClickable(false);
            btnlab.setEnabled(false);
        }

        btnbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MUCGuide.class);
                startActivity(intent);
            }
        });
    }
}
