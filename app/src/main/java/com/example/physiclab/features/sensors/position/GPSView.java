package com.example.physiclab.features.sensors.position;

import androidx.appcompat.app.AppCompatActivity;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.physiclab.R;

public class GPSView extends AppCompatActivity {

    TextView viewLon, viewLat, viewAlt;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_s_view);
        viewLon = findViewById(R.id.viewLongitud);
        viewLat = findViewById(R.id.viewLatitud);
        viewAlt = findViewById(R.id.viewAltitud);
    }
}