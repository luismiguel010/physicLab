package com.example.physiclab.features.sensors.movement;

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

public class AccelerometerView extends AppCompatActivity implements OnChartValueSelectedListener, SensorEventListener {

    private Toolbar toolbar;
    private Menu menu;
    private LineChart lineChartX, lineChartY, lineChartZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isSensorOn = false;
    long startTime=0L, timeInNanoSeconds =0L, timeSwapBuff=0L, updateTime=0L;
    private float secsWithMillis;
    Handler customHandler = new Handler();
    ArrayList<Float> vectorTime, vectorAxisX, vectorAxisY, vectorAxisZ;

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
        setContentView(R.layout.activity_accelerometer_view);
        initComponents();
    }

    public void initComponents(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Acelerómetro");
        toolbar.setTitleTextColor(Color.WHITE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lineChartX = findViewById(R.id.linear_chartX);
        lineChartX.setOnChartValueSelectedListener(this);
        lineChartX.setDrawGridBackground(false);
        lineChartX.getDescription().setEnabled(false);
        lineChartX.setNoDataText("Presione play para visualizar los datos.");
        lineChartX.invalidate();
        lineChartY = findViewById(R.id.linear_chartY);
        lineChartY.setOnChartValueSelectedListener(this);
        lineChartY.setDrawGridBackground(false);
        lineChartY.getDescription().setEnabled(false);
        lineChartY.setNoDataText("Presione play para visualizar los datos.");
        lineChartY.invalidate();
        lineChartZ = findViewById(R.id.linear_chartZ);
        lineChartZ.setOnChartValueSelectedListener(this);
        lineChartZ.setDrawGridBackground(false);
        lineChartZ.getDescription().setEnabled(false);
        lineChartZ.setNoDataText("Presione play para visualizar los datos.");
        lineChartZ.invalidate();
        vectorAxisX = new ArrayList<>();
        vectorAxisY = new ArrayList<>();
        vectorAxisZ = new ArrayList<>();
        vectorTime = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
                lineChartX.clear();
                lineChartY.clear();
                lineChartZ.clear();
                lineChartX.removeAllViews();
                lineChartY.removeAllViews();
                lineChartZ.removeAllViews();
                isSensorOn = true;
                restartTime();
                runTime();
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                Toast.makeText(AccelerometerView.this, "Acelerómetro activado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.stop:
                isSensorOn = false;
                pauseTime();
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
                Toast.makeText(AccelerometerView.this, "Acelerómetro desactivado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.exportData:
                export();
                Toast.makeText(AccelerometerView.this, "Exportar datos.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.actionClear: {
                restartTime();
                lineChartX.clear();
                lineChartY.clear();
                lineChartZ.clear();
                lineChartX.removeAllViews();
                lineChartY.removeAllViews();
                lineChartZ.removeAllViews();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEntryX(float secsWithMillis, float axisX){
        LineData data = lineChartX.getData();
        if (data == null) {
            data = new LineData();
            lineChartX.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSetX();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(secsWithMillis, axisX), 0);
        data.notifyDataChanged();
        data.setDrawValues(false);
        lineChartX.setDrawMarkers(false);
        lineChartX.notifyDataSetChanged();
        lineChartX.setVisibleXRangeMaximum(6);
        lineChartX.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSetX() {
        LineDataSet set = new LineDataSet(null, "Aceleración en X  m/s²");
        set.setDrawCircles(false);
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(0, 110, 71));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set;
    }

    private void addEntryY(float secsWithMillis, float axisY){
        LineData data = lineChartY.getData();
        if (data == null) {
            data = new LineData();
            lineChartY.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSetY();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(secsWithMillis, axisY), 0);
        data.notifyDataChanged();
        data.setDrawValues(false);
        lineChartY.notifyDataSetChanged();
        lineChartY.setVisibleXRangeMaximum(6);
        lineChartY.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSetY() {
        LineDataSet set = new LineDataSet(null, "Aceleración en Y  m/s²");
        set.setDrawCircles(false);
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(255, 192, 0));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set;
    }

    private void addEntryZ(float secsWithMillis, float axisZ){
        LineData data = lineChartZ.getData();
        if (data == null) {
            data = new LineData();
            lineChartZ.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSetZ();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(secsWithMillis, axisZ), 0);
        data.notifyDataChanged();
        data.setDrawValues(false);
        lineChartZ.notifyDataSetChanged();
        lineChartZ.setVisibleXRangeMaximum(6);
        lineChartZ.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSetZ() {
        LineDataSet set = new LineDataSet(null, "Aceleración en Z  m/s²");
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
            float axisX = sensorEvent.values[0];
            float axisY = sensorEvent.values[1];
            float axisZ = sensorEvent.values[2];
            addEntryX(secsWithMillis, axisX);
            addEntryY(secsWithMillis, axisY);
            addEntryZ(secsWithMillis, axisZ);
            saveData(secsWithMillis, axisX, axisY, axisZ);
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

    private void saveData(float secsWithMillis, float axisX, float axisY, float axisZ) {
        if(isSensorOn) {
            vectorTime.add(secsWithMillis);
            vectorAxisX.add(axisX);
            vectorAxisY.add(axisY);
            vectorAxisZ.add(axisZ);
        }
    }

    public void export(){
        String nameFile = "DataAccelerometer";
        StringBuilder data = new StringBuilder();
        data.append("Tiempo,EjeX,EjeY,EjeZ");
        for(int i = 0; i < vectorTime.size(); i++){
            data.append("\n" + vectorTime.get(i).toString() + "," + vectorAxisX.get(i).toString()
                    + "," + vectorAxisY.get(i).toString() + "," + vectorAxisZ.get(i).toString());
        }
        try{
            FileOutputStream out = openFileOutput("accelerometer.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "accelerometer.csv");
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