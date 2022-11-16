package com.example.mobileapp_infinityrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class Map_Runner extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    Polyline currentPolyline;
    static List<LatLng> latLngList = new ArrayList<>();
    List<MarkerOptions> placeList = new ArrayList<>();
    LatLng currentLocation;
    Marker markerAtCurrrentLocation;

    Chronometer chronometer;
    long pauseOffset;
    boolean timeIsRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_runner);
        chronometer = findViewById(R.id.timer);

        TextView username = findViewById(R.id.username);
        username.setText(getIntent().getStringExtra("username"));

        Bundle bundle = getIntent().getExtras();
        placeList = (List<MarkerOptions>) bundle.getSerializable("placeList");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MaterialButton logoutbtn = (MaterialButton) findViewById(R.id.logoutbutton);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Map_Runner.this, LogInActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (placeList.isEmpty()) return;

        for (MarkerOptions m : placeList) {
            latLngList.add(m.getPosition());
        }
        latLngList.add(placeList.get(0).getPosition());

        TextView speed = findViewById(R.id.speed);

        // Get current location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);  // Enable button to zoom to current location
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        // Get current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (markerAtCurrrentLocation != null) markerAtCurrrentLocation.remove();
                //markerAtCurrrentLocation = map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                //Get speed
                speed.setText(String.valueOf(location.getSpeed()) + " km/h");
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // start running the route latLngList


        currentPolyline = map.addPolyline(new PolylineOptions().addAll(latLngList).width(5).color(R.color.black));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeList.get(0).getPosition(), 15));
    }

    public void startTimer(View v){
        if(!timeIsRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            timeIsRunning = true;
        }
    }

    public void stopTimer(View v){
        if (timeIsRunning) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        }
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId){
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // Connect to bluetooth device
    public void connectBluetooth(){
        // TODO: connect to bluetooth device
        // TODO: get data from bluetooth device
        // connect to bluetooth device
        // get data from bluetooth device

    }

}