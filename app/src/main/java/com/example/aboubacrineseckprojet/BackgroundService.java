package com.example.aboubacrineseckprojet;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.Task;

public class BackgroundService extends Service {
    public BackgroundService() {
    }
    Intent mIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private PendingIntent mPendingIntent;

    class LocalBinder extends Binder {
        BackgroundService serverInstance = BackgroundService.this;
    }
    LocalBinder binder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntent = new Intent(this,ActivityDetectionService.class);
        mPendingIntent = PendingIntent.getService(
                this,
                1,
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesHandler();
    }

    @SuppressLint("MissingPermission")
    private void requestActivityUpdatesHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(1000, mPendingIntent);
        task.addOnSuccessListener((v)->{
            Toast.makeText(getApplicationContext(), "Requested Activity Updates", Toast.LENGTH_SHORT).show();
        });
        task.addOnFailureListener((v)->{
            System.out.println("**********************");
            System.out.println(v);
            System.out.println("**********************");
            Toast.makeText(getApplicationContext(), "Failed to request Activity Updates", Toast.LENGTH_SHORT).show();
        });
    }
    @SuppressLint("MissingPermission")
    private void stopActivityUpdatesHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(mPendingIntent);
        task.addOnSuccessListener((v)->{
            Toast.makeText(getApplicationContext(), "Stopped Activity Updates", Toast.LENGTH_SHORT).show();
        });
        task.addOnFailureListener((v)->{
            Toast.makeText(getApplicationContext(), "Failed to stop Activity Updates", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopActivityUpdatesHandler();
    }
}