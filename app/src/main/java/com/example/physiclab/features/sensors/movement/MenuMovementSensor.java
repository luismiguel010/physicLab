package com.example.physiclab.features.sensors.movement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.physiclab.R;

public class MenuMovementSensor extends AppCompatActivity {

    private ImageButton btnAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_movement_sensor);
        btnAccelerometer = findViewById(R.id.btnAccelerometer);

        btnAccelerometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccelView = new Intent(getApplicationContext(), AccelerometerView.class);
                startActivity(intentAccelView);
            }
        });
    }


}