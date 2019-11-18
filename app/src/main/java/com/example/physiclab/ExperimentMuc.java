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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

public class ExperimentMuc extends Activity {

    private EditText editNumberLaps;
    private EditText editRadio;
    private int numberLaps = 1;
    private int radio;
    Chronometer chronometer;
    private long pauseOffset;
    private boolean flagStartMovement;
    private float time;
    static final double pi = 3.1415;
    private float angularVelocity;
    private float frecuency;
    private float tanVelocity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_muc);
        initComponent();
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button buttonStart = (Button) findViewById(R.id.button);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberLaps = catchNumberLaps();
                radio = catchRadio();
                starChronometer();
            }
        });

        Button buttonFinish = (Button) findViewById(R.id.buttonFinish);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = pauseChronometer();
                restartChronometer();
                calculate(time);
            }
        });

    }



    private int catchRadio() {
        int value = Integer.parseInt(editRadio.getText().toString());
        if("".equals(value)){
            Toast.makeText(ExperimentMuc.this,
                    "Ingrese número de vueltas", Toast.LENGTH_SHORT)
                    .show();
        }else{
            radio = Integer.valueOf(value);
        }
        return radio;
    }

    private int catchNumberLaps() {
        int value = Integer.parseInt(editNumberLaps.getText().toString());
        if("".equals(value)){
            Toast.makeText(ExperimentMuc.this,
                    "Ingrese número de vueltas", Toast.LENGTH_SHORT)
                    .show();
        }else{
            numberLaps = Integer.valueOf(value);
        }
        return numberLaps;
    }

    private void initComponent(){
        editNumberLaps = findViewById(R.id.editNumberLaps);
        editRadio = findViewById(R.id.editRadio);
    }



    public void calculate(float time) {
        time = pauseChronometer()/1000;
        restartChronometer();
         angularVelocity = (float) ((numberLaps*2*pi)/time);
         frecuency = (float) (angularVelocity / (2*pi));
         tanVelocity = (float) ((2*pi*radio)/time);
         Toast.makeText(ExperimentMuc.this,
                        "La velocidad angular="+String.format("%.2f", angularVelocity)+" rad/s\nFrecuencia="
                        +String.format("%.2f", frecuency)+" rpm\nVelocidad tangencial="+String.format("%.2f", tanVelocity)+" m/s", Toast.LENGTH_SHORT)
                        .show();
         flagStartMovement = false;
    }



    public float getNumberLaps() {
        return numberLaps;
    }

    public void setNumberLaps(int numberLaps) {
        this.numberLaps = numberLaps;
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

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }
}
