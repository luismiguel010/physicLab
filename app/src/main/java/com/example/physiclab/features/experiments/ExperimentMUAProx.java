package com.example.physiclab.features.experiments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.physiclab.R;
import com.example.physiclab.threads.ControlAudioThread;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

public class ExperimentMUAProx extends AppCompatActivity implements OnChartValueSelectedListener {

    ControlAudioThread controlAudioThread;

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
    double sensitivity = 60;
    SensorManager sensorManager;
    Sensor proximitySensor;
    long startTimeCode;
    private final int[] vectorColors = ColorTemplate.VORDIPLOM_COLORS;
    private LineChart lineChart;
    private float secsWithMillis;
    private boolean isActivatedSensor = false;
    private Thread threadListener;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private int interval;

    Runnable updateTimetThread = new Runnable() {
        @Override
        public void run() {
                startTimeCode = System.nanoTime();
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMilliseconds - (System.nanoTime() - startTimeCode) / 1000000;
                int secs = (int) (updateTime / 1000);
                int milliseconds = (int) (updateTime % 1000);
                time = secs + "." + String.format("%03d", milliseconds);
                secsWithMillis = Float.parseFloat(time);
                addEntry();
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MUA");
        toolbar.setTitleTextColor(Color.WHITE);
        lineChart = findViewById(R.id.linear_chart);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("Presione play para visualizar los datos.");
        lineChart.invalidate();
        isActivatedSensor = false;
        //threadListener = new Thread(dispatcher, "Audio Dispatcher");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void addEntry(){
        if(isActivatedSensor && secsWithMillis != 0.000) {
            LineData data = lineChart.getData();
            if (data == null) {
                data = new LineData();
                lineChart.setData(data);
            }
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(secsWithMillis, (float) (0.5 * 9.8 * Math.pow(secsWithMillis, 2))), 0);
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(6);
            lineChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Distancia recorrida m");
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(0, 110, 71));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        //set.setValueTextSize(10f);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {}

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
                    pauseTime();
                    restartTime();
                    lineChart.clear();
                    isActivatedSensor = true;
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }else{
                    if(isActivatedSensor) {
                        runTime();
                        startAudioCatcher();
                    }
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(isOnSensor && isActivatedSensor) {
                                    finishTime(timeCatch);
                                }
                            }
                        });
                    }
                }, sensitivity, threshold);

        dispatcher.addAudioProcessor(mPercussionDetector);
        new Thread(dispatcher, "Audio dispatcher").start();
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
            pauseTime();
            restartTime();
            isOnSensor = false;
            isActivatedSensor = false;
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
        isActivatedSensor = false;
        pauseTime();
        restartTime();
        isOnSensor = false;
        isActivatedSensor = false;
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(false);
        Toast.makeText(ExperimentMUAProx.this, "Sensor desactivado...", Toast.LENGTH_LONG).show();
        timeTextView.setText(timeCatchSound + " s");
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