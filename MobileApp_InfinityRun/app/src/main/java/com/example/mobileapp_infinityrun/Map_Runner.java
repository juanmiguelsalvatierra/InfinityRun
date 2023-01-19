package com.example.mobileapp_infinityrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import java.util.UUID;

public class Map_Runner extends AppCompatActivity implements OnMapReadyCallback {

    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    int permissionRequestCode;

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private static final String TAG = "MAP_RUNNER";

    private static final UUID hr_service = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"); // HR
    private static final UUID hr_characteristic = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"); // HR
    private static final UUID hr_control_characteristic = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb"); // HR
    private static final UUID client_characteristic = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


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

        // Get the username from the previous activity
        TextView username = findViewById(R.id.username);
        username.setText(getIntent().getStringExtra("username"));

        // Get the place list from the previous activity
        Bundle bundle = getIntent().getExtras();
        placeList = (List<MarkerOptions>) bundle.getSerializable("placeList");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Button to Logout
        MaterialButton logoutbtn = (MaterialButton) findViewById(R.id.logoutbutton);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Map_Runner.this, LogInActivity.class);
                startActivity(intent);
            }
        });

        Log.d(TAG, "onCreate: ");
        if (!hasPermission(Map_Runner.this, permissions)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, permissionRequestCode);
            }
        }
        scanLeDevice();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Set the map and the type of map
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Check if place list is empty
        if (placeList.isEmpty()) return;

        // Add the markers to the map
        for (MarkerOptions m : placeList) {
            latLngList.add(m.getPosition());
        }
        // Add the first marker to the map to make a loop
        latLngList.add(placeList.get(0).getPosition());

        TextView speed = findViewById(R.id.speed);
        TextView distance = findViewById(R.id.heartrate);

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
                // Update current location and get the speed
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (markerAtCurrrentLocation != null) markerAtCurrrentLocation.remove();
                //markerAtCurrrentLocation = map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                // Zoom to current location
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                //Get speed
                speed.setText(String.valueOf(location.getSpeed() * 3.6f) + " km/h");
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        currentPolyline = map.addPolyline(new PolylineOptions().addAll(latLngList).width(5).color(R.color.black));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeList.get(0).getPosition(), 15));
    }

    public void startTimer(View v) {
        if (!timeIsRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            timeIsRunning = true;
        }
    }

    public void stopTimer(View v) {
        if (timeIsRunning) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        }
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanLeDevice();
        }
    }

    // Nach Geräten suchen
    private void scanLeDevice() {
        Log.d(TAG, "onCreate: ");
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = btAdapter.getBluetoothLeScanner();

        if (!scanning) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (!scanning) {
                if (ActivityCompat.checkSelfPermission(Map_Runner.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Log.i(TAG, "Scan Result: " + result.getDevice().getName() + " : " + result.getDevice().getAddress());

                if (result.getDevice().getAddress().equals("C1:2C:2A:24:FE:80")) {
                    scanning = true;
                    if (ActivityCompat.checkSelfPermission(Map_Runner.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    bluetoothLeScanner.stopScan(leScanCallback);
                    if (ActivityCompat.checkSelfPermission(Map_Runner.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    result.getDevice().connectGatt(Map_Runner.this, false, mGattCallback);
                    Log.i(TAG, "Connecting to GATT");
                }
            }
        }

        private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.i(TAG, "onConnectionStateChange: " + newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if (ActivityCompat.checkSelfPermission(Map_Runner.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Log.i(TAG, "CONNECTED");
                    gatt.discoverServices();
                } else {
                    if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        scanning = false;
                        Log.i(TAG, "DISCONNECTED");
                    }
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                for (BluetoothGattService service : gatt.getServices()) {
                    Log.i(TAG, "Service discovered: " + service.getUuid());
                }
                for (BluetoothGattCharacteristic character : gatt.getService(hr_service).getCharacteristics()) {
                    Log.i(TAG, "Characteristic for Heart Rate discovered: " + character.getUuid());
                }

                BluetoothGattCharacteristic characteristic = gatt.getService(hr_service).getCharacteristic(hr_characteristic);
                if (ActivityCompat.checkSelfPermission(Map_Runner.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gatt.setCharacteristicNotification(characteristic, true);


                final BluetoothGattDescriptor DESCRIPTOR = characteristic.getDescriptor(client_characteristic);

                if (!DESCRIPTOR.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                    Log.e(TAG, "    Cannot create descriptor for HR in: ");
                }

                if (!gatt.writeDescriptor(DESCRIPTOR)) {
                    Log.e(TAG, "    Cannot enable notifications for HR in: ");
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.i(TAG, "onDescriptorWrite: ");
                BluetoothGattCharacteristic characteristic =
                        gatt.getService(hr_service)
                                .getCharacteristic(hr_control_characteristic);
                characteristic.setValue(new byte[]{1, 1});
                if (ActivityCompat.checkSelfPermission(Map_Runner.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gatt.writeCharacteristic(characteristic);
            }



            @Override
            public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);

                // HR anzeigen
                TextView hrValue = findViewById(R.id.heartrate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hrValue.setText(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1) + "");
                    }
                });

                Log.i(TAG, "Heart Rate: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1));
            }
        };

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private boolean hasPermission(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}