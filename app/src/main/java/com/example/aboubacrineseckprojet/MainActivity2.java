package com.example.aboubacrineseckprojet;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView textView;

    private static final int ACTIVITY_THRESHOLD = 10; // Seuil d'activité

    private boolean isWalking = false;
    private boolean isRunning = false;
    private boolean isDriving = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = findViewById(R.id.textView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double magnitude = Math.sqrt(x * x + y * y + z * z);

        if (magnitude > ACTIVITY_THRESHOLD) {
            if (!isWalking) {
                isWalking = true;
                textView.setText("Marche détectée");
            } else if (!isRunning) {
                isRunning = true;
                textView.setText("Course détectée");
            } else if (!isDriving) {
                isDriving = true;
                textView.setText("Conduite détectée");
            }
        } else {
            isWalking = false;
            isRunning = false;
            isDriving = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ne fait rien pour l'instant
    }
}
