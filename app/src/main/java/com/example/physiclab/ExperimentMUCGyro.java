package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    String time, timeCatch;
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
            secsWithMillis = updateTime/1000;
            time = secs + "." + String.format("%03d", milliseconds);
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
        getSupportActionBar().setTitle("");

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
    }

    private void addEntry(){
        LineData data = lineChart.getData();
        if (data == null) {
            data = new LineData();
            lineChart.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well
        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }
        // choose a random dataSet
        //int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
        //ILineDataSet randomSet = data.getDataSetByIndex(randomDataSetIndex);
        //float value = (float) (Math.random() * 50) + 50f * (randomDataSetIndex + 1);
        data.addEntry(new Entry(secsWithMillis, omegaMagnitude), 0);
        data.notifyDataChanged();
        // let the chart know it's data has changed
        lineChart.notifyDataSetChanged();
        lineChart.setVisibleXRangeMaximum(6);
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        lineChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private void removeLastEntry() {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set != null) {

                Entry e = set.getEntryForXValue(set.getEntryCount() - 1, Float.NaN);

                data.removeEntry(e, 0);
                // or remove by index
                // mData.removeEntryByXValue(xIndex, dataSetIndex);
                data.notifyDataChanged();
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        }
    }

    private void addDataSet() {

        LineData data = lineChart.getData();

        if (data == null) {
            lineChart.setData(new LineData());
        } else {
            int count = (data.getDataSetCount() + 1);
            int amount = data.getDataSetByIndex(0).getEntryCount();

            ArrayList<Entry> values = new ArrayList<>();

            for (int i = 0; i < amount; i++) {
                values.add(new Entry(i, (float) (Math.random() * 50f) + 50f * count));
            }

            LineDataSet set = new LineDataSet(values, "DataSet " + count);
            set.setLineWidth(2.5f);
            set.setCircleRadius(4.5f);

            int color = vectorColors[count % vectorColors.length];

            set.setColor(color);
            set.setCircleColor(color);
            set.setHighLightColor(color);
            set.setValueTextSize(10f);
            set.setValueTextColor(color);

            data.addDataSet(set);
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    private void removeDataSet() {

        LineData data = lineChart.getData();

        if (data != null) {

            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));

            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }


    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
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
                    runTime();
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(true);
                    Toast.makeText(ExperimentMUCGyro.this, "Sensor activado.", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.stop:
                isSensorOn = false;
                startGyroscope();
                restartTime();
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
                Toast.makeText(ExperimentMUCGyro.this, "Exportar datos.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.actionAddEntry: {
                addEntry();
                Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionRemoveEntry: {
                removeLastEntry();
                Toast.makeText(this, "Entry removed!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionAddDataSet: {
                addDataSet();
                Toast.makeText(this, "DataSet added!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionRemoveDataSet: {
                removeDataSet();
                Toast.makeText(this, "DataSet removed!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionClear: {
                lineChart.clear();
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
                float axisX = sensorEvent.values[0];
                float axisY = sensorEvent.values[1];
                float axisZ = sensorEvent.values[2];
                omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
                vectorVelocity.add(omegaMagnitude*radio);
                vectorTime.add(secsWithMillis);
                addEntry();
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

    public void runTime(){
        startTime = System.currentTimeMillis();
        customHandler.postDelayed(updateTimetThread, 0);
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
}