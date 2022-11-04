package com.example.mobileapp_infinityrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
        username.setText(getIntent().getStringExtra("username"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MaterialButton startbtn = (MaterialButton) findViewById(R.id.startbutton);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!placeList.isEmpty()) {
                    Toast.makeText(Map.this, "Run successfully started", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Map.this, Map_Runner.class);
                    intent.putExtra("username", username.getText().toString());
                    intent.putExtra("placeList", (Serializable) placeList);
                    startActivity(intent);
                } else
                    Toast.makeText(Map.this, "No route available", Toast.LENGTH_SHORT).show();
            }
        });

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
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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

        if(placeList.isEmpty()) return;

        for (MarkerOptions m:placeList) {
            latLngList.add(m.getPosition());
        }
        latLngList.add(placeList.get(0).getPosition());

        for (MarkerOptions m:placeList) {
            markerList.add(map.addMarker(m));
        }

        currentPolyline = map.addPolyline(new PolylineOptions().addAll(latLngList).width(5).color(R.color.black));

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

}