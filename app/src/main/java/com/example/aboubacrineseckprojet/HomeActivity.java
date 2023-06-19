package com.example.aboubacrineseckprojet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.DetectedActivity;

import java.util.Date;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 127;
    private static final int ACTIVITY_RECOGNITION_CODE = 45;
    Profile profile;
    private ProfileDBHelper dbHelper;
    BroadcastReceiver broadcastReceiver;
    private final String CHANNEl_ID = "projet_channel_id";

    ActivityDBHelper activityDBHelper;

    private Button start;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if((profile = Utils.getSharedProfileOrElseLogin(this)) == null){
            return;
        }
        dbHelper = new ProfileDBHelper(this);
        activityDBHelper = new ActivityDBHelper(this);
        findViewById(R.id.deconnecter_home).setOnClickListener(this::deconnecter);
        findViewById(R.id.maps_home).setOnClickListener((v)->{
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                },LOCATION_PERMISSION_CODE);
                return;
            }
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.settings_home).setOnClickListener((v)->{
            Utils.startActivityAndShareProfile(this,profile,SettingActivity.class,false);
        });
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("activity_intent")) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        start = findViewById(R.id.btn_id);
        start.setOnClickListener((v)->{
            Intent intent = new Intent(this,MainActivity2.class);
            startActivity(intent);
        });

        findViewById(R.id.start_ar_home).setOnClickListener((v)->{
            if(checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},ACTIVITY_RECOGNITION_CODE);
                return;
            }
            startActivityTracking();
        });
        findViewById(R.id.all_ar_home).setOnClickListener((v)->{
            Utils.startActivityAndShareProfile(this,profile,ActivitiesActivity.class,false);
        });
        createNotificationChannel();
    }


    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.activity_unknown);
        int icon = -1;

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.activity_in_vehicle);
                icon = R.drawable.ic_driving;
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = getString(R.string.activity_on_bicycle);
                icon = R.drawable.ic_on_bicycle;
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = getString(R.string.activity_on_foot);
                icon = R.drawable.ic_walking;
                break;
            }
            case DetectedActivity.RUNNING: {
                label = getString(R.string.activity_running);
                icon = R.drawable.ic_running;
                break;
            }
            case DetectedActivity.STILL: {
                label = getString(R.string.activity_still);
                icon = R.drawable.ic_still;
                break;
            }
            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                icon = R.drawable.ic_walking;
                break;
            }
        }
        if (confidence > 60 && icon != -1) {
            Toast.makeText(this, "label:"+label+",icon"+icon, Toast.LENGTH_SHORT).show();
            notification(label,icon);
            activityDBHelper.saveActivity(profile.id,label,icon,new Date(System.currentTimeMillis()));
        }
    }

    private void notification(String label, int icon) {
        Notification n = new NotificationCompat.Builder(this,CHANNEl_ID)
                .setSmallIcon(icon)
                .setContentTitle("Activity detected")
                .setContentText(label)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        NotificationManagerCompat.from(this).notify(new Random().nextInt(99999999),n);
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = "nameeee";
            String description = "nameeeee";
            int imp = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel c = new NotificationChannel(CHANNEl_ID,name,imp);
            c.setDescription(description);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("activity_intent"));
    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
    @SuppressLint("MissingPermission")
    private void startActivityTracking() {
        startService(new Intent(this,BackgroundService.class));
    }
    @SuppressLint("MissingPermission")
    private void stopActivityTracking() {
        stopService(new Intent(this,BackgroundService.class));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Location permission denied" ,Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(this,MapsActivity.class);
                startActivity(intent);
            }
        }else if(requestCode == ACTIVITY_RECOGNITION_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"ACTIVITY_RECOGNITION permission denied" ,Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityTracking();
        }
    }
    private void deconnecter(View view) {
        Utils.saveLoginSharedPreferences(this,null,null);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        profile = dbHelper.getProfileFromDB(profile.id);
    }
}