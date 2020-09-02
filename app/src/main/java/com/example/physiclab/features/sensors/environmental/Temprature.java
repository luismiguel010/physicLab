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

public class Temprature extends AppCompatActivity implements OnChartValueSelectedListener, SensorEventListener {

    private Toolbar toolbar;
    private Menu menu;
    private LineChart lineChartC, lineChartK, lineChartF;
    private SensorManager sensorManager;
    private Sensor temperature;
    private boolean isSensorOn = false;
    long startTime=0L, timeInNanoSeconds =0L, timeSwapBuff=0L, updateTime=0L;
    private float secsWithMillis;
    Handler customHandler = new Handler();
    ArrayList<Float> vectorTime, vectorC, vectorK, vectorF;
    private TextView viewC, viewK, viewF;

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
        setContentView(R.layout.activity_temprature);
        initComponents();
    }

    public void initComponents(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Temperatura ambiente");
        toolbar.setTitleTextColor(Color.WHITE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        lineChartC = findViewById(R.id.linear_chartC);
        lineChartC.setOnChartValueSelectedListener(this);
        lineChartC.setDrawGridBackground(false);
        lineChartC.getDescription().setEnabled(false);
        lineChartC.setNoDataText("Presione play para visualizar los datos.");
        lineChartC.invalidate();
        lineChartK = findViewById(R.id.linear_chartK);
        lineChartK.setOnChartValueSelectedListener(this);
        lineChartK.setDrawGridBackground(false);
        lineChartK.getDescription().setEnabled(false);
        lineChartK.setNoDataText("Presione play para visualizar los datos.");
        lineChartK.invalidate();
        lineChartF = findViewById(R.id.linear_chartF);
        lineChartF.setOnChartValueSelectedListener(this);
        lineChartF.setDrawGridBackground(false);
        lineChartF.getDescription().setEnabled(false);
        lineChartF.setNoDataText("Presione play para visualizar los datos.");
        lineChartF.invalidate();
        vectorC = new ArrayList<>();
        vectorK = new ArrayList<>();
        vectorF = new ArrayList<>();
        vectorTime = new ArrayList<>();
        viewC = findViewById(R.id.viewTempC);
        viewK = findViewById(R.id.viewTempK);
        viewF = findViewById(R.id.viewTempF);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
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
                lineChartC.clear();
                lineChartK.clear();
                lineChartF.clear();
                lineChartC.removeAllViews();
                lineChartK.removeAllViews();
                lineChartF.removeAllViews();
                isSensorOn = true;
                restartTime();
                runTime();
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                Toast.makeText(Temprature.this, "Sensor de temperatura activado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.stop:
                isSensorOn = false;
                pauseTime();
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
                Toast.makeText(Temprature.this, "Sensor de temperatura desactivado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.exportData:
                export();
                Toast.makeText(Temprature.this, "Exportar datos.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.actionClear: {
                restartTime();
                lineChartC.clear();
                lineChartK.clear();
                lineChartF.clear();
                lineChartC.removeAllViews();
                lineChartK.removeAllViews();
                lineChartF.removeAllViews();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEntryC(float secsWithMillis, float axisX){
        LineData data = lineChartC.getData();
        if (data == null) {
            data = new LineData();
            lineChartC.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSetC();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(secsWithMillis, axisX), 0);
        data.notifyDataChanged();
        data.setDrawValues(false);
        lineChartC.setDrawMarkers(false);
        lineChartC.notifyDataSetChanged();
        lineChartC.setVisibleXRangeMaximum(6);
        lineChartC.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSetC() {
        LineDataSet set = new LineDataSet(null, "Temperatura en °C");
        set.setDrawCircles(false);
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(0, 110, 71));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set;
    }

    private void addEntryK(float secsWithMillis, float axisY){
        LineData data = lineChartK.getData();
        if (data == null) {
            data = new LineData();
            lineChartK.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSetK();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(secsWithMillis, axisY), 0);
        data.notifyDataChanged();
        data.setDrawValues(false);
        lineChartK.notifyDataSetChanged();
        lineChartK.setVisibleXRangeMaximum(6);
        lineChartK.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSetK() {
        LineDataSet set = new LineDataSet(null, "Temperatura en K");
        set.setDrawCircles(false);
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(255, 192, 0));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set;
    }

    private void addEntryF(float secsWithMillis, float axisZ){
        LineData data = lineChartF.getData();
        if (data == null) {
            data = new LineData();
            lineChartF.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSetF();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(secsWithMillis, axisZ), 0);
        data.notifyDataChanged();
        data.setDrawValues(false);
        lineChartF.notifyDataSetChanged();
        lineChartF.setVisibleXRangeMaximum(6);
        lineChartF.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSetF() {
        LineDataSet set = new LineDataSet(null, "Temperatura en °F");
        set.setDrawCircles(false);
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(33, 150, 243));
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
            float axisK = (float) (axisC + 273.15);
            float axisF = (float) ((axisC * 1.8) + 32);
            viewC.setText(axisC + " °C");
            viewK.setText(axisK + " K");
            viewF.setText(axisF + " °F");
            addEntryC(secsWithMillis, axisC);
            addEntryK(secsWithMillis, axisK);
            addEntryF(secsWithMillis, axisF);
            saveData(secsWithMillis, axisC, axisK, axisF);
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

    private void saveData(float secsWithMillis, float axisC, float axisK, float axisF) {
        if(isSensorOn) {
            vectorTime.add(secsWithMillis);
            vectorC.add(axisC);
            vectorK.add(axisK);
            vectorF.add(axisF);
        }
    }

    public void export(){
        String nameFile = "DataTemperature";
        StringBuilder data = new StringBuilder();
        data.append("Tiempo,Celcius,Kelvin,Fahrenheit");
        for(int i = 0; i < vectorTime.size(); i++){
            data.append("\n" + vectorTime.get(i).toString() + "," + vectorC.get(i).toString()
                    + "," + vectorK.get(i).toString() + "," + vectorF.get(i).toString());
        }
        try{
            FileOutputStream out = openFileOutput("temperature.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "temperature.csv");
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