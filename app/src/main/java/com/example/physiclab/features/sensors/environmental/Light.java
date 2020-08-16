package com.example.physiclab.features.sensors.environmental;

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

public class Light extends AppCompatActivity implements OnChartValueSelectedListener, SensorEventListener {

    private Toolbar toolbar;
    private Menu menu;
    private LineChart lineChart;
    private SensorManager sensorManager;
    private Sensor light;
    private boolean isSensorOn = false;
    long startTime=0L, timeInNanoSeconds =0L, timeSwapBuff=0L, updateTime=0L;
    private float secsWithMillis;
    Handler customHandler = new Handler();
    ArrayList<Float> vectorTime, vectorLight;
    private TextView viewLight;

    Runnable updateTimetThread = new Runnable() {
        @Override
        public void run() {
            timeInNanoSeconds = System.currentTimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInNanoSeconds;
            int secs = (int)(updateTime / 1000);
            int milliseconds = (int) (updateTime%1000);
            secsWithMillis = Float.parseFloat(secs + "." + String.format("%03d", milliseconds));
            customHandler.postDelayed(this,0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        initComponents();
    }

    public void initComponents(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Intensidad de luz");
        toolbar.setTitleTextColor(Color.WHITE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lineChart = findViewById(R.id.linear_chartLight);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("Presione play para visualizar los datos.");
        lineChart.invalidate();
        vectorLight = new ArrayList<>();
        vectorTime = new ArrayList<>();
        viewLight = findViewById(R.id.viewLux);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
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
                lineChart.clear();
                lineChart.removeAllViews();
                isSensorOn = true;
                restartTime();
                runTime();
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                Toast.makeText(Light.this, "Sensor de luz activado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.stop:
                isSensorOn = false;
                pauseTime();
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
                Toast.makeText(Light.this, "Sensor de luz desactivado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.exportData:
                export();
                Toast.makeText(Light.this, "Exportar datos.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.actionClear: {
                restartTime();
                lineChart.clear();
                lineChart.removeAllViews();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEntryC(float secsWithMillis, float axisX){
        LineData data = lineChart.getData();
        if (data == null) {
            data = new LineData();
            lineChart.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSetC();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(secsWithMillis, axisX), 0);
        data.notifyDataChanged();
        data.setDrawValues(false);
        lineChart.setDrawMarkers(false);
        lineChart.notifyDataSetChanged();
        lineChart.setVisibleXRangeMaximum(6);
        lineChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSetC() {
        LineDataSet set = new LineDataSet(null, "Iluminación en lx");
        set.setDrawCircles(false);
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(0, 110, 71));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set;
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
            float axisC = sensorEvent.values[0];
            viewLight.setText(axisC + " lx");
            addEntryC(secsWithMillis, axisC);
            saveData(secsWithMillis, axisC);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    public void runTime(){
        if(isSensorOn) {
            startTime = System.currentTimeMillis();
            customHandler.postDelayed(updateTimetThread, 0);
        }
    }

    public void pauseTime(){
        timeSwapBuff += timeInNanoSeconds;
        customHandler.removeCallbacks(updateTimetThread);
    }

    public void restartTime(){
        startTime = 0L;
        timeInNanoSeconds =0L;
        timeSwapBuff=0L;
        updateTime=0L;
    }

    private void saveData(float secsWithMillis, float axisC) {
        if(isSensorOn) {
            vectorTime.add(secsWithMillis);
            vectorLight.add(axisC);
        }
    }

    public void export(){
        String nameFile = "DataLight";
        StringBuilder data = new StringBuilder();
        data.append("Tiempo,Lx");
        for(int i = 0; i < vectorTime.size(); i++){
            data.append("\n" + vectorTime.get(i).toString() + "," + vectorLight.get(i).toString());
        }
        try{
            FileOutputStream out = openFileOutput("light.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "light.csv");
            Uri path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() +".fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, nameFile);
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}