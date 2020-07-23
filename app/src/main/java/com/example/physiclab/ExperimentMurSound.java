package com.example.physiclab;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.physiclab.services.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;


public class ExperimentMurSound extends AppCompatActivity implements SensorEventListener {

    private EditText timeSoundOther;
    private EditText distanceMurSound;
    private TextView timeSoundThis;
    private TextView temperatureViewer;
    private float time;
    private long otherTime;
    private float velocity;
    private float distance;
    private long timeStampThis;
    private static final String TAG = "Clapper";
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
    double threshold = 8;
    double sensitivity = 50;
    public static final int RequestPermissionCode = 1;
    private int counterCatchTime = 0;
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    boolean isPresentTemperatureSensor;
    double longitud;
    double latitude;
    double temperature = 15;
    double kelvinDifference = 273.15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_mur_sound);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(isPresentTemperatureSensor) {
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            temperatureViewer.setText("Temperatura ambiente = " + temperatureSensor.toString() + " °C");
        }else{
            getLocationByGPS();
        }

        initComponent();
            PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                    new OnsetHandler() {
                        @Override
                        public void handleOnset(double time, double salience) {
                            final long timeStampCatch = System.currentTimeMillis();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    counterCatchTime = counterCatchTime + 1;
                                    catchTimeClap(timeStampCatch);
                                }
                            });
                        }
                    }, sensitivity, threshold);

            dispatcher.addAudioProcessor(mPercussionDetector);
            new Thread(dispatcher, "Audio Dispatcher").start();

            Button buttonCalculate = (Button) findViewById(R.id.buttonMurSound);
            buttonCalculate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    otherTime = catchOtherTime();
                    distance = catchDistance();
                    String resultVelocitySound = calculateSound();
                    AlertDialog alertDialog = new AlertDialog.Builder(ExperimentMurSound.this).create(); //Read Update
                    alertDialog.setTitle("Velocidad del sonido");
                    alertDialog.setMessage(resultVelocitySound + " m/s");
                    alertDialog.show();
                }
            });

            Button buttonEraseTime = (Button) findViewById(R.id.eraseTime);
            buttonEraseTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    counterCatchTime = 0;
                    timeStampThis = 0;
                    timeSoundThis.setText("");

                }
            });
    }


    public void catchTimeClap(long timeStampCatch){
        if(counterCatchTime == 1) {
            final long timeStampTemp = timeStampCatch;
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ExperimentMurSound.this);
            builder1.setMessage("¿Desea guardar este tiempo para este dispositivo: " + String.valueOf(timeStampTemp)+"?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Sí",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            timeStampThis = timeStampTemp;
                            String timeThis = String.valueOf(timeStampThis);
                            timeSoundThis.setText(timeThis);
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            counterCatchTime = 0;
                            timeSoundThis.setText("");
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public float catchDistance(){
        String valueString = distanceMurSound.getText().toString();
        if(valueString.equals("") || valueString.equals("0")){
            Toast.makeText(ExperimentMurSound.this,
                    "Ingrese la distancia entre los dispositivos en metros.", Toast.LENGTH_SHORT)
                    .show();
        }else{
            distance = Float.parseFloat(distanceMurSound.getText().toString());
        }
        return distance;
    }

    public long catchOtherTime(){
        String valueString = timeSoundOther.getText().toString();
        if(valueString.equals("") || valueString.equals("0")){
            Toast.makeText(ExperimentMurSound.this,
                    "Ingrese el tiempo que registró el otro dispositivo.", Toast.LENGTH_SHORT)
                    .show();
        }else{
            otherTime = Long.parseLong(timeSoundOther.getText().toString());
        }
        return otherTime;
    }

    public String calculateSound() {
        float theoreticalTime = (float) (distance/(331.3+0.606 * temperature));
        float timeMilliSeconds = (timeStampThis - otherTime);
        float timeSeconds = timeMilliSeconds * 1000;
        float constRegulatorTime = (theoreticalTime - timeSeconds)/distance;
        float timeWithCRT = distance * constRegulatorTime + timeSeconds;
        time = (float) Math.abs((timeMilliSeconds)/100000.0);
        velocity = (float) ((distance/timeWithCRT));
        String resultVelocitySound = String.valueOf(velocity);
        return resultVelocitySound;
    }

    private void initComponent() {
            timeSoundOther = findViewById(R.id.timeSoundOther);
            timeSoundThis = findViewById(R.id.timeSoundThis);
            distanceMurSound = findViewById(R.id.distanceMurSound);
            temperatureViewer = findViewById(R.id.textViewTemperature);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("¿Está seguro que desea salir?")
                .setMessage("Se borrarán los datos.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        counterCatchTime = 0;
                        ExperimentMurSound.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float millibarsOfTemperature = sensorEvent.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        isPresentTemperatureSensor = sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ExperimentMurSound.this, new String[]{ACCESS_FINE_LOCATION, INTERNET}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int i = 0; i == permissions.length; i++) {
            switch (requestCode) {
                case RequestPermissionCode:
                    if (grantResults.length > 0) {
                        boolean RecordPermission = grantResults[i] ==
                                PackageManager.PERMISSION_GRANTED;
                        if (RecordPermission) {
                            Toast.makeText(ExperimentMurSound.this, "Permission Granted",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ExperimentMurSound.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    }

    public boolean checkPermissionLocation() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionInternet(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void getLocationByGPS(){
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
                longitud = bestLocation.getLongitude();
                latitude = bestLocation.getLatitude();
            }
        }
        if (bestLocation == null) {
            System.out.println("Not found location");
        }
        requestTemperatureApi(Double.toString(longitud), Double.toString(latitude));
        //temperatureViewer.setText("Temperatura ambiente = " + Double.toString(temperature) + " °C");
    }

    public void requestTemperatureApi(final String lon, final String lat){
        if(checkPermissionInternet()){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    HttpRequest httpRequest = new HttpRequest();
                    try{
                        String response = httpRequest.run("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=9de243494c0b295cca9337e1e96b00e2");
                        JSONObject jsonObject = new JSONObject(response);
                        String main = jsonObject.getString("main");
                        JSONObject jsonObject1 = new JSONObject(main);
                        temperature = jsonObject1.getDouble("temp") - kelvinDifference;
                        System.out.println(temperature);
                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        } else {
            temperatureViewer.setText("Temperatura ambiente por defecto =  15 °C");
        }
    }

}