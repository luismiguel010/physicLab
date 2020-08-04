package com.example.physiclab.features.experiments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.physiclab.R;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

public class ExperimentMUAProx extends AppCompatActivity {

    private TextView timeTextView;
    long startTime=0L, timeInMilliseconds =0L, timeSwapBuff=0L, updateTime=0L;
    String time, timeCatch;
    Handler customHandler = new Handler();
    int counterSound = 0;
    private Toolbar toolbar;
    private Menu menu;
    private TextView textSensor;
    boolean isOnSensor = false;
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
    double threshold = 8;
    double sensitivity = 50;
    SensorManager sensorManager;
    Sensor proximitySensor;
    long startTimeCode;

    Runnable updateTimetThread = new Runnable() {
        @Override
        public void run() {
                startTimeCode = System.nanoTime();
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMilliseconds - (System.nanoTime() - startTimeCode) / 1000000;
                int secs = (int) (updateTime / 1000);
                int milliseconds = (int) (updateTime % 1000);
                time = secs + "." + String.format("%03d", milliseconds);
                customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_m_u_a_prox);
        initComponents();
        if(proximitySensor == null){
            Toast.makeText(ExperimentMUAProx.this, "Sensor de proximidad no disponible", Toast.LENGTH_LONG).show();
        }
    }

    private void initComponents() {
        timeTextView = findViewById(R.id.textTime);
        toolbar = findViewById(R.id.tool_bar);
        textSensor = findViewById(R.id.textSensor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startProximityCatcher();
    }

    private void startProximityCatcher() {
        final SensorEventListener proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[0] < proximitySensor.getMaximumRange()){
                    restartTime();
                    counterSound = 0;
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }else{
                    runTime();
                    startAudioCatcher();
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(proximitySensorListener, proximitySensor, 1);
    }

    private void startAudioCatcher() {
        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {
                    @Override
                    public void handleOnset(final double timeHandle, double salience) {
                        timeCatch = time;
                        counterSound = 1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(isOnSensor && counterSound == 1) {
                                    finishTime(timeCatch);
                                }
                            }
                        });
                    }
                }, sensitivity, threshold);

        dispatcher.addAudioProcessor(mPercussionDetector);
        new Thread(dispatcher, "Audio Dispatcher").start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_mua_experiment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.play) {
            isOnSensor = true;
            counterSound = 0;
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            Toast.makeText(ExperimentMUAProx.this, "Sensor activado...", Toast.LENGTH_LONG).show();
            return true;
        }else if(id == R.id.stop){
            restartTime();
            isOnSensor = false;
            counterSound = 0;
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            Toast.makeText(ExperimentMUAProx.this, "Sensor desactivado...", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void runTime(){
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimetThread, 0);
    }

    public void finishTime(String timeCatchSound){
        counterSound = 0;
        timeTextView.setText(timeCatchSound + " s");
        pauseTime();
    }

    public void pauseTime(){
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimetThread);
    }

    public void restartTime(){
        startTime = 0L;
        timeInMilliseconds =0L;
        timeSwapBuff=0L;
        updateTime=0L;
        timeTextView.setText("");
    }
}