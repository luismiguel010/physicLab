package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

public class ExperimentMurSound extends AppCompatActivity {

    private EditText timeSoundOther;
    private EditText distanceMurSound;
    private TextView timeSoundThis;
    private TextView resultVelocity;
    private float time;
    private long otherTime;
    private float velocity;
    private float distance;
    private long timeStampThis;

    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
    private static final String TAG = "Clapper";

    double threshold = 8;
    double sensitivity = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_mur_sound);
        initComponent();

        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d(TAG, "Clap detected!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                catchTimeClap();
                            }
                        });
                    }
                }, sensitivity, threshold);

        dispatcher.addAudioProcessor(mPercussionDetector);
        new Thread(dispatcher,"Audio Dispatcher").start();


        Button buttonCalculate = (Button) findViewById(R.id.buttonMurSound);
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherTime = catchOtherTime();
                distance = catchDistance();
                calculateSound();
            }
        });
    }

    public void catchTimeClap(){
        timeStampThis = System.currentTimeMillis()/1000;
        String timeThis = String.valueOf(timeStampThis);
        timeSoundThis.setText(timeThis);
    }

    public float catchDistance(){
        String valueString = distanceMurSound.getText().toString();
        if(valueString.equals("") || valueString.equals("0")){
            Toast.makeText(ExperimentMurSound.this,
                    "Ingrese la distancia entre los dispositivos en metros", Toast.LENGTH_SHORT)
                    .show();
        }else{
            distance = Float.parseFloat(distanceMurSound.getText().toString());
        }
        return distance;
    }

    public long catchOtherTime(){
        String valueString = timeSoundOther.getText().toString();
        if(valueString.equals("") || valueString.equals("0")){
            Toast.makeText(ExperimentMurSound.this,
                    "Ingrese el tiempo que registró el otro dispositivo", Toast.LENGTH_SHORT)
                    .show();
        }else{
            otherTime = Long.parseLong(timeSoundOther.getText().toString());
        }
        return otherTime;
    }

    public void calculateSound() {
        float timeMilliSeconds = timeStampThis - otherTime;
        time = (float) Math.abs((timeMilliSeconds)/1000.0);
        velocity = (float) (distance/time);
        String resultVelocitySound = String.valueOf(velocity);
        resultVelocity.setText("Velocidad del sonido = "+resultVelocitySound+" m/s");
    }

    private void initComponent() {
        timeSoundOther = findViewById(R.id.timeSoundOther);
        timeSoundThis = findViewById(R.id.timeSoundThis);
        distanceMurSound = findViewById(R.id.distanceMurSound);
        resultVelocity = findViewById(R.id.resultVelocitySound);
    }


}