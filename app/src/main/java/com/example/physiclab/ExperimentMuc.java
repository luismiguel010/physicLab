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
import android.widget.TextView;
import android.widget.Toast;

public class ExperimentMuc extends Activity {

    private EditText editNumberLaps;
    private EditText editRadio;
    private int numberLaps;
    private float radio;
    Chronometer chronometer;
    private long pauseOffset;
    private boolean flagStartMovement;
    private float time;
    static final float pi = 3.1415f;
    private float angularVelocity;
    private float frecuency;
    private float tanVelocity;
    private TextView resultAngularVelocity;
    private TextView resultTanVelocity;
    private TextView resultFrecuency;

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
                Toast.makeText(ExperimentMuc.this,
                        "¡Gire!", Toast.LENGTH_LONG)
                        .show();
                starChronometer();
            }
        });

        Button buttonFinish = (Button) findViewById(R.id.buttonFinish);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartChronometer();
                calculate();
            }
        });
    }

    private float catchRadio() {
        int value = Integer.parseInt(editRadio.getText().toString());
        if("".equals(value)){
            Toast.makeText(ExperimentMuc.this,
                    "Ingrese número de vueltas", Toast.LENGTH_SHORT)
                    .show();
        }else{
            radio = Float.valueOf(value);
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
        resultAngularVelocity = findViewById(R.id.resultAngularVelocity);
        resultTanVelocity = findViewById(R.id.resultTanVelocity);
        resultFrecuency = findViewById(R.id.resultFrecuency);
    }

    public void calculate() {
        time = pauseChronometer();
        restartChronometer();
        angularVelocity = (float) ((numberLaps*2*pi)/time);
        frecuency = (float) (1 / (time));
        tanVelocity = (float) ((2*pi*radio)/(time));
        String resultAngular = String.valueOf(angularVelocity);
        String resultTan = String.valueOf(tanVelocity);
        String resultFrec = String.valueOf(frecuency);
        resultAngularVelocity.setText("Velocidad angular = "+resultAngular + " rad/s");
        resultTanVelocity.setText("Velocidad tangencial = "+resultTan + " m/s");
        resultFrecuency.setText("Frecuencia = " + resultFrec + " Hz");
    }

    public void starChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
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

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public int getNumberLaps() {
        return numberLaps;
    }

    public void setNumberLaps(int numberLaps) {
        this.numberLaps = numberLaps;
    }
}
