package com.example.physiclab;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import static android.Manifest.permission.RECORD_AUDIO;

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
    private static final String TAG = "Clapper";
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
    double threshold = 8;
    double sensitivity = 50;
    public static final int RequestPermissionCode = 1;
    private int counterCatchTime = 0;

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
                                    counterCatchTime = counterCatchTime + 1;
                                    catchTimeClap();
                                }
                            });
                        }
                    }, sensitivity, threshold);

            dispatcher.addAudioProcessor(mPercussionDetector);
            new Thread(dispatcher, "Audio Dispatcher").start();

            Button buttonCalculate = (Button) findViewById(R.id.buttonMurSound);
            buttonCalculate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    otherTime = catchOtherTime();
                    distance = catchDistance();
                    calculateSound();
                }
            });

            Button buttonEraseTime = (Button) findViewById(R.id.eraseTime);
            buttonEraseTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    counterCatchTime = 0;
                    timeStampThis = 0;
                    timeSoundThis.setText("");

                }
            });
    }

    public void catchTimeClap(){
        if(counterCatchTime == 1) {
            final long timeStampTemp = System.currentTimeMillis();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ExperimentMurSound.this);
            builder1.setMessage("Desea guardar este tiempo para este dispositivo: " + String.valueOf(timeStampTemp));
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Sí",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            timeStampThis = timeStampTemp;
                            String timeThis = String.valueOf(timeStampThis);
                            timeSoundThis.setText(timeThis);
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            counterCatchTime = 0;
                            timeSoundThis.setText("");
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
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
        float timeMilliSeconds = (timeStampThis - otherTime);
        time = (float) Math.abs((timeMilliSeconds)/100000.0);
        velocity = (float) ((distance/time));
        String resultVelocitySound = String.valueOf(velocity);
        resultVelocity.setText("Velocidad del sonido = "+resultVelocitySound+" m/s");
    }

    private void initComponent() {
            timeSoundOther = findViewById(R.id.timeSoundOther);
            timeSoundThis = findViewById(R.id.timeSoundThis);
            distanceMurSound = findViewById(R.id.distanceMurSound);
            resultVelocity = findViewById(R.id.resultVelocitySound);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        counterCatchTime = 0;
                        ExperimentMurSound.super.onBackPressed();
                    }
                }).create().show();
    }
}