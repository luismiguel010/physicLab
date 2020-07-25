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

public class ExperimentMURTimer extends AppCompatActivity {

    Button btnStart, btnPause, btnLap;
    TextView textTimer1;
    Handler customHandler = new Handler();
    LinearLayout container;

    long startTime=0L, timeInMilliseconds=0L, timeSwapBuff=0L, updateTime=0L;

    Runnable updateTimetThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int)(updateTime / 1000);
            int mins = secs / 60;
            secs%=60;
            int milliseconds = (int) (updateTime%1000);
            textTimer1.setText("" + mins + ":" + String.format("%2d", secs) + ":"
            + String.format("%3d", milliseconds));
            customHandler.postDelayed(this,0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_m_u_r_timer);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnPause = (Button)findViewById(R.id.btnPause);
        btnLap = (Button)findViewById(R.id.btnLap);
        textTimer1 = (TextView) findViewById(R.id.timerValue1);
        container = (LinearLayout) findViewById(R.id.container);

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
            }
        });

        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View addView = inflater.inflate(R.layout.time_mur_sound,null);
                TextView txtValue = (TextView)addView.findViewById(R.id.txtContent);
                txtValue.setText(textTimer1.getText());
                container.addView(addView);
            }
        });

    }


}