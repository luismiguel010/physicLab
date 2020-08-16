package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.physiclab.features.experiments.ExperimentMUAProx;
import com.example.physiclab.features.guides.MUAGuide;
import com.example.physiclab.features.sensors.position.Magnetometer;
import com.example.physiclab.features.theories.MurTheory;

import static android.Manifest.permission.RECORD_AUDIO;

public class MuaIntroduction extends AppCompatActivity {

    private ImageButton btnbook, btnlab;
    public static final int RequestPermissionCode = 1;
    private SensorManager sensorManager;
    private TextView disableProximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mua_introduction);
        btnlab = findViewById(R.id.imagebutton_lab);
        btnbook = findViewById(R.id.imagebutton_book);
        disableProximity = findViewById(R.id.disable_proximity_experimentMUA);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            btnlab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkPermission()) {
                        Intent intent = new Intent(getApplicationContext(), ExperimentMUAProx.class);
                        startActivity(intent);
                    }else {
                        requestPermission();
                        if(checkPermission()){
                            Intent intent = new Intent(getApplicationContext(), ExperimentMUAProx.class);
                            startActivity(intent);
                        }
                    }
                }
            });
        }else{
            disableProximity.setText(R.string.label_warning_sensor_proximity);
            btnlab.setClickable(false);
            btnlab.setEnabled(false);
        }



        btnbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MUAGuide.class);
                startActivity(intent);
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MuaIntroduction.this, new String[]{RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean RecordPermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (RecordPermission) {
                        Toast.makeText(MuaIntroduction.this, "Permisos aceptados, presione el botón nuevamente.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MuaIntroduction.this,"Permisos denegados.",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

}
