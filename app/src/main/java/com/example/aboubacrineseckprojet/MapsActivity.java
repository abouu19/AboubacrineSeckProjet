package com.example.aboubacrineseckprojet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.aboubacrineseckprojet.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;



import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , SensorEventListener,LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private List<LatLng> locations;
      private TextView text;
    private SensorManager sensorManager;
    LocationManager locationManager;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locations = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        text = findViewById(R.id.map_text);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, this);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    Marker startMarker = null;
    Marker lastMarker = null;
    Polyline line;
    @SuppressLint("MissingPermission")
    private void registerLocationListener(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                System.out.println("*******************************");
                System.out.println(location);
                System.out.println(mMap);

            }
        });
    }
    private String locationToText(double speed,double distance,double climbed){
        return "Speed: " + speed + " m/s\n"+
                "Climbed: " + climbed + "m\n"+
                "Distance: "+ distance + "m";
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        registerLocationListener();
        Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
    }
    double last = 0;
    double lastDist = 0;
    double firstDist = -1;
    double fZ = -1;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            double timeDelta = System.currentTimeMillis() - last;
            if(timeDelta > 1000)
                return;
            double ax,ay,az;
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
            double dist = Math.sqrt(ax*ax + ay*ay + az*az);
            if(firstDist == -1)
                firstDist = dist;
            if(fZ == -1)
                fZ = az;
            double speed = (dist - lastDist)/timeDelta;
            text.setText(locationToText(speed,firstDist,az-fZ));
            lastDist = dist;
            last = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }
    LatLng lastLatLng;
    double distance = 0;
    double lastAlt;
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (mMap != null && location != null) {

            Toast.makeText(MapsActivity.this, "Location known", Toast.LENGTH_SHORT).show();

            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            if (startMarker == null) {
                startMarker = mMap.addMarker(new MarkerOptions().position(current).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                lastLatLng = current;
                lastAlt = location.getAltitude();
            } else {
                if (lastMarker == null)
                    lastMarker = mMap.addMarker(new MarkerOptions().position(current).title("Current").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                lastMarker.setPosition(current);
            }
            locations.add(current);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,17));
            if (line == null) {
                line = mMap.addPolyline(new PolylineOptions()
                        .clickable(false)
                        .addAll(locations)
                );
            }else{
                line.setPoints(locations);
            }
            double speed = 0;
            if(location.hasSpeed()){
                speed = (double) location.getSpeed();
            }
            distance += calculationByDistance(lastLatLng,current);
            double climbed = location.getAltitude() - lastAlt;
            text.setText(locationToText(speed,distance,climbed));
            lastLatLng = current;
            lastAlt = location.getAltitude();
        }
    }
    public double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        /*
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        */
        return Radius * c;
    }
}