package com.example.physiclab.features.sensors.position;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.physiclab.R;

public class MenuPositionSensor extends AppCompatActivity {

    private ImageButton btnMagnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_position_sensor);
        btnMagnetometer = findViewById(R.id.btnMagnetometer);

        btnMagnetometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMagne = new Intent(getApplicationContext(), Magnetometer.class);
                startActivity(intentMagne);
            }
        });
    }
}