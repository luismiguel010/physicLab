package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

public class ExperimentMURTimer extends AppCompatActivity {

    Button btnStart, btnPause, btnLap;
    TextView textState, time1, time2, time3, time4, time5;
    String time;
    String timeCatch = "";
    Handler customHandler = new Handler();
    LinearLayout container;
    long startTime=0L, timeInMilliseconds=0L, timeSwapBuff=0L, updateTime=0L;
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
    double threshold = 8;
    double sensitivity = 50;
    int counterClap = 0;

    Runnable updateTimetThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int)(updateTime / 1000);
            int milliseconds = (int) (updateTime%1000);
            time = secs + "," + String.format("%3d", milliseconds);
            customHandler.postDelayed(this,0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_m_u_r_timer);
        initComponents();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAudioDispatcher();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTime();
            }
        });

        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartTime();
            }
        });
    }

    public void startAudioDispatcher(){
            PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                    new OnsetHandler() {
                        @Override
                        public void handleOnset(double timeHandle, double salience) {
                            timeCatch = time;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    controlTime(counterClap, timeCatch);
                                }
                            });
                        }
                    }, sensitivity, threshold);

            dispatcher.addAudioProcessor(mPercussionDetector);
            new Thread(dispatcher, "Audio Dispatcher").start();
    }

    private void initComponents() {
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnLap = findViewById(R.id.btnLap);
        textState = findViewById(R.id.txtState);
        time1 = findViewById(R.id.time1);
        time2 = findViewById(R.id.time2);
        time3 = findViewById(R.id.time3);
        time4 = findViewById(R.id.time4);
        time5 = findViewById(R.id.time5);
    }

    public void controlTime(int counter, String timeCatchHandle){
        switch (counter){
            case 0:
                startTime = SystemClock.uptimeMillis();
                textState.setText("Started");
                customHandler.postDelayed(updateTimetThread, 0);
                counterClap = 1;
                break;
            case 1:
                time1.setText(timeCatchHandle);
                counterClap = 2;
                break;
            case 2:
                time2.setText(timeCatchHandle);
                counterClap = 3;
                break;
            case 3:
                time3.setText(timeCatchHandle);
                counterClap = 4;
                break;
            case 4:
                time4.setText(timeCatchHandle);
                counterClap = 5;
                break;
            case 5:
                time5.setText(timeCatchHandle);
                pauseTime();
                break;
        }
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
}