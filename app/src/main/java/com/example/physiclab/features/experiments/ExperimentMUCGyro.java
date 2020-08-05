package com.example.physiclab.features.experiments;

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
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ExperimentMUCGyro extends AppCompatActivity implements OnChartValueSelectedListener {

    private SensorManager sensorManager;
    private Sensor sensorGyroscope;
    private Toolbar toolbar;
    private Menu menu;
    private TextView textViewRadio;
    private EditText editRadio;
    private float radio;
    private int samplingPeriodUsGyro = 1000000;
    boolean isSensorOn = false;
    private float mAccel, mAccelCurrent, mAccelLast;
    long startTime=0L, timeInNanoSeconds =0L, timeSwapBuff=0L, updateTime=0L;
    String time;
    Handler customHandler = new Handler();
    private float omegaMagnitude;
    private final int[] vectorColors = ColorTemplate.VORDIPLOM_COLORS;
    private LineChart lineChart;
    private float secsWithMillis;
    ArrayList<Float> vectorVelocity, vectorTime;

    Runnable updateTimetThread = new Runnable() {
        @Override
        public void run() {
            timeInNanoSeconds = System.currentTimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInNanoSeconds;
            int secs = (int)(updateTime / 1000);
            int milliseconds = (int) (updateTime%1000);
            time = secs + "." + String.format("%03d", milliseconds);
            secsWithMillis = Float.parseFloat(time);
            customHandler.postDelayed(this,0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_m_u_c_gyro);
        initComponent();
    }

    private void initComponent() {
        toolbar = findViewById(R.id.tool_bar);
        editRadio = findViewById(R.id.edit_radio);
        textViewRadio = findViewById(R.id.textViewRadio);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MUC");
        toolbar.setTitleTextColor(Color.WHITE);
        lineChart = findViewById(R.id.linear_chart);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("Presione play para visualizar los datos.");
        lineChart.invalidate();
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensorGyroscope == null){
            Toast.makeText(ExperimentMUCGyro.this, "Giroscopio no disponible", Toast.LENGTH_LONG).show();
        }
        vectorVelocity = new ArrayList<>();
        vectorTime = new ArrayList<>();
    }

    private void addEntry(){
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
        data.addEntry(new Entry(secsWithMillis, omegaMagnitude*radio), 0);
        data.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.setVisibleXRangeMaximum(6);
        lineChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Velocidad m/s");
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(0, 110, 71));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {}


    @Override
    protected void onPause() {
        super.onPause();
        isSensorOn = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_muc_experiment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.play:
                catchRadio();
                if(radio != 0) {
                    isSensorOn = true;
                    startGyroscope();
                    restartTime();
                    runTime();
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(true);
                    Toast.makeText(ExperimentMUCGyro.this, "Sensor activado.", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.stop:
                isSensorOn = false;
                startGyroscope();
                pauseTime();
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
                Toast.makeText(ExperimentMUCGyro.this, "Sensor desactivado.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.enableRemote:
                CheckBox checkBox= (CheckBox) item.getActionView();
                item.setChecked(!item.isChecked());
                if(item.isChecked()){
                    Toast.makeText(ExperimentMUCGyro.this, "Habilitado remoto.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ExperimentMUCGyro.this, "Deshabilitado remoto.", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.exportData:
                export();
                Toast.makeText(ExperimentMUCGyro.this, "Exportar datos.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.actionClear: {
                restartTime();
                lineChart.clear();
                runTime();
                Toast.makeText(this, "Chart cleared!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void startGyroscope(){
        final SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(isSensorOn) {
                    float axisX = sensorEvent.values[0];
                    float axisY = sensorEvent.values[1];
                    float axisZ = sensorEvent.values[2];
                    omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
                    saveData();
                    addEntry();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) { }
        };
        if(isSensorOn) {
            sensorManager.registerListener(sensorEventListener, sensorGyroscope, samplingPeriodUsGyro);
        }else{
            sensorManager.unregisterListener(sensorEventListener, sensorGyroscope);
        }
    }

    private void saveData() {
        if(isSensorOn) {
            vectorVelocity.add(omegaMagnitude * radio);
            vectorTime.add(secsWithMillis);
        }
    }

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

    public void catchRadio(){
        if(!editRadio.getText().toString().isEmpty()) {
            radio = Float.parseFloat(editRadio.getText().toString());
        }else{
            Toast.makeText(ExperimentMUCGyro.this, "Ingrese el radio.", Toast.LENGTH_LONG).show();
        }
    }

    public void export(){
        StringBuilder data = new StringBuilder();
        data.append("Tiempo,Velocidad");
        for(int i = 0; i < vectorVelocity.size(); i++){
            data.append("\n" + vectorTime.get(i).toString() + "," + vectorVelocity.get(i).toString());
        }
        try{
            FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.physiclab.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}