package com.example.physiclab;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import static android.Manifest.permission.INTERNET;

public class ExperimentMUAProx extends AppCompatActivity {

    private Toolbar toolbar;
    private Menu menu;
    private Switch switchButton;
    private TextView textSensor;
    boolean isOnSensor = false;
    int counterCatchTime = 0;
    boolean isOrigin = false;
    boolean isCatchTime = false;
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
    double threshold = 8;
    double sensitivity = 50;
    SensorManager sensorManager;
    Sensor proximitySensor;
    public static final int RequestPermissionCode = 1;
    String timeStampCatchRequest;
    private RequestQueue queue;
    String urlServer = "http://192.168.1.9:8080/physicslab/getCurrentTime";
    public static  final String TAG = "TAG_SERVER";
    private boolean isEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_m_u_a_prox);
        initComponents();


        if(proximitySensor == null){
            Toast.makeText(ExperimentMUAProx.this, "Sensor de proximidad no disponible", Toast.LENGTH_LONG).show();
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    isOrigin = true;
                }else {
                    isOrigin = false;
                }
            }
        });
    }

    private void initComponents() {
        toolbar = findViewById(R.id.tool_bar);
        switchButton = findViewById(R.id.switch1);
        textSensor = findViewById(R.id.textSensor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        queue = Volley.newRequestQueue(this);
    }

    private void getCurrentTimeServer(){
        final long startTime = System.currentTimeMillis();
        if(checkPermissionInternet()) {
            if(isEvent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlServer, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        long totalRequestTime = Long.valueOf(response) - (System.currentTimeMillis() - startTime);
                        timeStampCatchRequest = Long.toString(totalRequestTime);
                        catchTimeClap(timeStampCatchRequest);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        timeStampCatchRequest = "Error 404";
                    }
                });
                stringRequest.setTag(TAG);
                queue.add(stringRequest);
            }
        }else{
            Toast.makeText(ExperimentMUAProx.this, "Es necesario permisos de internet",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(queue != null){
            queue.cancelAll(TAG);
        }
    }

    private void startProximityCatcher() {
        final SensorEventListener proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[0] < proximitySensor.getMaximumRange()){
                    textSensor.setText("Ahí está el sensor.");
                    isEvent = true;
                }else{
                    textSensor.setText("");
                    if(isOnSensor && isEvent){
                        getCurrentTimeServer();
                    }
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!isOrigin && isOnSensor) {
                                    isEvent = true;
                                    getCurrentTimeServer();
                                }
                            }
                        });
                    }
                }, sensitivity, threshold);

        dispatcher.addAudioProcessor(mPercussionDetector);
        new Thread(dispatcher, "Audio Dispatcher").start();
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
            startAudioCatcher();
            startProximityCatcher();
            isOnSensor = true;
            isCatchTime = false;
            counterCatchTime = 0;
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            Toast.makeText(ExperimentMUAProx.this, "Sensor activado...", Toast.LENGTH_LONG).show();
            requestPermission();
            return true;
        }else if(id == R.id.stop){
            isOnSensor = false;
            isCatchTime = false;
            counterCatchTime = 0;
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            Toast.makeText(ExperimentMUAProx.this, "Sensor desactivado...", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void catchTimeClap(String timeStampCatch){
        if(!isCatchTime && timeStampCatchRequest != null) {
            isCatchTime = true;
            isEvent = false;
            counterCatchTime = 0;
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ExperimentMUAProx.this);
            builder1.setMessage("Tiempo: " + timeStampCatchRequest);
            builder1.setNegativeButton(
                    "Reintentar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            isCatchTime = false;
                            counterCatchTime = 0;
                            queue.cancelAll(TAG);
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ExperimentMUAProx.this, new String[]{INTERNET}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case RequestPermissionCode:
                    if (grantResults.length > 0) {
                        boolean RecordPermission = grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED;
                        if (RecordPermission) {
                            Toast.makeText(ExperimentMUAProx.this, "Permission Granted",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ExperimentMUAProx.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
    }

    public boolean checkPermissionInternet(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        return result == PackageManager.PERMISSION_GRANTED;
    }





/*    public void requestCurrentTime(){
        //final long startTime = System.currentTimeMillis();
        if(checkPermissionInternet()){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    HttpRequest httpRequest = new HttpRequest();
                    try{
                            String timeReponse = httpRequest.run("http://192.168.1.9:8080/physicslab/getCurrentTime");
                            isOkResponse = true;
                            //long elapsedTime = System.currentTimeMillis() - startTime;
                            //long currentTimeReal = Long.valueOf(timeResponse).longValue() - elapsedTime;
                            //timeStampCatchRequest = Long.toString(currentTimeReal);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(ExperimentMUAProx.this, "Error http",
                    Toast.LENGTH_LONG).show();
        }
    }*/

}