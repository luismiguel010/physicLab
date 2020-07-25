package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
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
    Handler customHandler = new Handler();
    LinearLayout container;
    long startTime=0L, timeInMilliseconds=0L, timeSwapBuff=0L, updateTime=0L;
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
    double threshold = 8;
    double sensitivity = 50;
    boolean isFirstTime = true;
    int counterClap = 0;

    Runnable updateTimetThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int)(updateTime / 1000);
            int mins = secs / 60;
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

        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {
                    @Override
                    public void handleOnset(double time, double salience) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                controlTime(counterClap);
                            }
                        });
                    }
                }, sensitivity, threshold);

        dispatcher.addAudioProcessor(mPercussionDetector);
        new Thread(dispatcher, "Audio Dispatcher").start();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimetThread, 0);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimetThread);
                textState.setText("Paused");
            }
        });

        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartTime();
            }
        });
    }

    private void initComponents() {
        btnStart = (Button)findViewById(R.id.btnStart);
        btnPause = (Button)findViewById(R.id.btnPause);
        btnLap = (Button)findViewById(R.id.btnLap);
        textState = (TextView) findViewById(R.id.txtState);
        time1 = (TextView)findViewById(R.id.time1);
        time2 = (TextView)findViewById(R.id.time2);
        time3 = (TextView)findViewById(R.id.time3);
        time4 = (TextView)findViewById(R.id.time4);
        time5 = (TextView)findViewById(R.id.time5);
    }

    public void controlTime(int counter){
        switch (counter){
            case 0:
                textState.setText("Started");
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimetThread, 0);
                counterClap = 1;
                break;
            case 1:
                time1.setText(time);
                counterClap = 2;
                break;
            case 2:
                time2.setText(time);
                counterClap = 3;
                break;
            case 3:
                time3.setText(time);
                counterClap = 4;
                break;
            case 4:
                time4.setText(time);
                counterClap = 5;
                break;
            case 5:
                time5.setText(time);
                break;
        }
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
    }


}