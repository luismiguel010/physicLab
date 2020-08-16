package com.example.physiclab.features.experiments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
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

public class ExperimentMURTimer extends AppCompatActivity {

    private Toolbar toolbar;
    private Menu menu;
    private TextView textState, time1, time2, time3, time4, time5;
    private String time;
    private String timeCatch = "";
    private Handler customHandler = new Handler();
    private long startTime=0L, timeInMilliseconds=0L, timeSwapBuff=0L, updateTime=0L;
    private AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
    private double threshold = 8;
    private double sensitivity = 50;
    private int counterClap = 0;
    private long startTimeCode;
    private boolean isFirstTime = true;
    private Thread threadListener;

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

    Runnable audioListener = new Runnable() {
        @Override
        public void run() {
            timeCatch = time;
            controlTime(counterClap, timeCatch);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_m_u_r_timer);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MUR");
        toolbar.setTitleTextColor(Color.WHITE);
        initComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customHandler.removeCallbacks(audioListener);
        customHandler.removeCallbacks(updateTimetThread);
        isFirstTime = true;
        counterClap = 0;
    }

    public void startAudioDispatcher(){
            PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                    new OnsetHandler() {
                        @Override
                        public void handleOnset(final double timeHandle, double salience) {
                            runOnUiThread(audioListener);
                        }
                    }, sensitivity, threshold);

        dispatcher.addAudioProcessor(mPercussionDetector);
        if(isFirstTime){
            startThreadAudio();
            isFirstTime = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_mur_experiment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.play) {
            restartTime();
            startAudioDispatcher();
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
            Toast.makeText(ExperimentMURTimer.this, "Escuchando...", Toast.LENGTH_LONG).show();
            return true;
        }else if(id == R.id.stop){
            pauseTime();
            restartTime();
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
            Toast.makeText(ExperimentMURTimer.this, "No escuchando...", Toast.LENGTH_LONG).show();
            return true;
        }else if(id == R.id.restart){
            pauseTime();
            restartTime();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void initComponents() {
        textState = findViewById(R.id.txtState);
        time1 = findViewById(R.id.time1);
        time2 = findViewById(R.id.time2);
        time3 = findViewById(R.id.time3);
        time4 = findViewById(R.id.time4);
        time5 = findViewById(R.id.time5);
        counterClap = 0;
    }

    public void controlTime(int counter, String timeCatchHandle){
        switch (counter){
            case 0:
                runTime();
                textState.setText("Inició");
                time1.setText("_ _ , _ _");
                time2.setText("_ _ , _ _");
                time3.setText("_ _ , _ _");
                time4.setText("_ _ , _ _");
                time5.setText("_ _ , _ _");
                counterClap = 1;
                break;
            case 1:
                time1.setText(timeCatchHandle);
                startTime = 0L;
                timeInMilliseconds=0L;
                timeSwapBuff=0L;
                updateTime=0L;
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimetThread, 0);
                counterClap = 2;
                break;
            case 2:
                time2.setText(timeCatchHandle);
                startTime = 0L;
                timeInMilliseconds=0L;
                timeSwapBuff=0L;
                updateTime=0L;
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimetThread, 0);
                counterClap = 3;
                break;
            case 3:
                time3.setText(timeCatchHandle);
                startTime = 0L;
                timeInMilliseconds=0L;
                timeSwapBuff=0L;
                updateTime=0L;
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimetThread, 0);
                counterClap = 4;
                break;
            case 4:
                time4.setText(timeCatchHandle);
                startTime = 0L;
                timeInMilliseconds=0L;
                timeSwapBuff=0L;
                updateTime=0L;
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimetThread, 0);
                counterClap = 5;
                break;
            case 5:
                time5.setText(timeCatchHandle);
                startTime = 0L;
                timeInMilliseconds=0L;
                timeSwapBuff=0L;
                updateTime=0L;
                pauseTime();
                break;
        }
    }

    public void runTime(){
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimetThread, 0);
    }

    public void pauseTime(){
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimetThread);
        textState.setText("Paused");
    }

    public void restartTime(){
        counterClap = 0;
        time1.setText("");
        time2.setText("");
        time3.setText("");
        time4.setText("");
        time5.setText("");
        startTime = 0L;
        timeInMilliseconds=0L;
        timeSwapBuff=0L;
        updateTime=0L;
        textState.setText("");
    }

    public void startThreadAudio() {
        threadListener = new Thread(dispatcher, "Audio dispatcher");
        threadListener.start();
    }

    public void stopThreadAudio() {
        customHandler.removeCallbacks(audioListener);
    }

}