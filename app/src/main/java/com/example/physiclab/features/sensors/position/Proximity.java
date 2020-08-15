package com.example.physiclab.features.sensors.position;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.physiclab.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Proximity extends AppCompatActivity implements OnChartValueSelectedListener, SensorEventListener {

    private Toolbar toolbar;
    private Menu menu;
    private SensorManager sensorManager;
    private Sensor proximity;
    private boolean isSensorOn = false;
    private TextView textProx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);
        initComponents();
    }

    public void initComponents(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Proximidad");
        toolbar.setTitleTextColor(Color.WHITE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        textProx = findViewById(R.id.textProximity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.play:
                isSensorOn = true;
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                Toast.makeText(Proximity.this, "Sensor de proximidad activado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.stop:
                isSensorOn = false;
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
                Toast.makeText(Proximity.this, "Sensor de proximidad desactivado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.exportData:
                Toast.makeText(Proximity.this, "Exportar datos.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.actionClear: {
                textProx.setText("");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(isSensorOn) {
            if(sensorEvent.values[0] != proximity.getMaximumRange()){
                float prox = sensorEvent.values[0];
                textProx.setText(prox + " cm.");
            }else{
                textProx.setText("");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

}