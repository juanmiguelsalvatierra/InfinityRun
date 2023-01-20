package com.example.mobileapp_infinityrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Map extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap map;
    Polyline currentPolyline;
    List<MarkerOptions> placeList = new ArrayList<>();
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        TextView username = findViewById(R.id.username);
        // Get username from previous activity and set it to the textview
        username.setText(getIntent().getStringExtra("username"));

        // Get the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Button to to start the game and go to the next activity
        MaterialButton startbtn = (MaterialButton) findViewById(R.id.startbutton);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if placeList is empty
                if (!placeList.isEmpty()) {
                    Toast.makeText(Map.this, "Run successfully started", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Map.this, Map_Runner.class);
                    intent.putExtra("username", username.getText().toString());
                    intent.putExtra("placeList", (Serializable) placeList);
                    startActivity(intent);
                } else // If placeList is empty, show a toast message
                    Toast.makeText(Map.this, "No route available", Toast.LENGTH_SHORT).show();
            }
        });

        // Button to logout and go to the login activity
        MaterialButton logoutbtn = (MaterialButton) findViewById(R.id.logoutbutton);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Map.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Get the map and set the map type
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Check if the GPS is enabled
        turnOnLocation();

        // Connect places
        placeList.add(new MarkerOptions().position(new LatLng(48.22153, 16.44409)).title("Place 1")
                .icon(bitmapDescriptor(getApplicationContext(), R.drawable.ic_baseline_circle_24)));
        placeList.add(new MarkerOptions().position(new LatLng(48.22108, 16.44649)).title("Place 2")
                .icon(bitmapDescriptor(getApplicationContext(), R.drawable.ic_baseline_circle_24)));
        placeList.add(new MarkerOptions().position(new LatLng(48.22002, 16.44617)).title("Place 3")
                .icon(bitmapDescriptor(getApplicationContext(), R.drawable.ic_baseline_circle_24)));
        placeList.add(new MarkerOptions().position(new LatLng(48.22048, 16.44386)).title("Place 4")
                .icon(bitmapDescriptor(getApplicationContext(), R.drawable.ic_baseline_circle_24)));

        // Check if the placeList is empty
        if(placeList.isEmpty()) return;

        // Add markers to the map
        for (MarkerOptions m:placeList) {
            latLngList.add(m.getPosition());
        }
        // Add the first marker to the map to make a loop
        latLngList.add(placeList.get(0).getPosition());

        // Add the markers to the map
        for (MarkerOptions m:placeList) {
            markerList.add(map.addMarker(m));
        }

        // Set the polyline
        currentPolyline = map.addPolyline(new PolylineOptions().addAll(latLngList).width(5).color(R.color.black));

        // Move the camera to the first marker
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeList.get(0).getPosition(), 15));
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId){
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void turnOnLocation() {
        // Ask if Location is on or off
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Build an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Turn on Location");
            builder.setMessage("Your Location Settings is set to 'Off'.\nPlease Enable Location to use this app");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void turnOnBluetooth() {
        // Ask if Bluetooth is on or off
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                // Build an alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Turn on Bluetooth");
                builder.setMessage("Your Bluetooth Settings is set to 'Off'.\nPlease Enable Bluetooth to use this app");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Show bluetooth settings when the user acknowledges the alert dialog
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 0);
        }
    }

}