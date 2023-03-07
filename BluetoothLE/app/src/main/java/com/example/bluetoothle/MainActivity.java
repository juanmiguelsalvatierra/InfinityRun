package com.example.bluetoothle;

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
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    int permissionRequestCode;

    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int SCAN_PERIOD = 7000;
    private boolean scanning;
    private Handler handler = new Handler();
    private static final String TAG = "MainActivityA";

    private static final UUID hr_service = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"); // HR
    private static final UUID hr_characteristic = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"); // HR
    private static final UUID hr_control_characteristic = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb"); // HR
    private static final UUID client_characteristic = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    static String myDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        if (!hasPermission(MainActivity.this, permissions)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, permissionRequestCode);
            }
        }

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        //ensure bluetooth is enabled
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            scanLeDevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanLeDevice();
        }
    }

    private void scanLeDevice() {
        Log.d(TAG, "onCreate: ");
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = btAdapter.getBluetoothLeScanner();

        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(() -> {
                scanning = false;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                bluetoothLeScanner.stopScan(leScanCallback);
                Log.d(TAG, "Stopped scanning after 10 seconds!");
                MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Stopped scanning after 10 seconds!", Toast.LENGTH_SHORT).show());
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d(TAG, "Scanning... ");
            MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Scanning...", Toast.LENGTH_SHORT).show());
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d(TAG, "Stopped scanning!");
            MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Stopped scanning!", Toast.LENGTH_SHORT).show());
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (scanning) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Log.i(TAG, "Scan Result: " + result.getDevice().getName() + " : " + result.getDevice().getAddress());

                if (result.getDevice().getAddress().equals("C1:2C:2A:24:FE:80")) {
                    myDevice = result.getDevice().getName();
                    scanning = false;
                    Log.i(TAG, "Device found...");
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    bluetoothLeScanner.stopScan(leScanCallback);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    result.getDevice().connectGatt(MainActivity.this, false, mGattCallback);
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
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Log.i(TAG, "CONNECTED");
                    // Connected Status für den User anzeigen
                    MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected to " + myDevice, Toast.LENGTH_SHORT).show());
                    gatt.discoverServices();
                } else {
                    if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        scanning = false;
                        Log.i(TAG, "DISCONNECTED");

                        // Disconnected Status für den User anzeigen
                        MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Disconnected from " + myDevice, Toast.LENGTH_SHORT).show());
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
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gatt.setCharacteristicNotification(characteristic, true);

                final BluetoothGattDescriptor DESCRIPTOR = characteristic.getDescriptor(client_characteristic);
                if (!DESCRIPTOR.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                    Log.e(TAG, "Cannot create descriptor for HR in: ");
                }
                if (!gatt.writeDescriptor(DESCRIPTOR)) {
                    Log.e(TAG, "Cannot enable notifications for HR in: ");
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.i(TAG, "onDescriptorWrite: ");
                BluetoothGattCharacteristic characteristic =
                        gatt.getService(hr_service)
                                .getCharacteristic(hr_control_characteristic);
                characteristic.setValue(new byte[]{1, 1});
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gatt.writeCharacteristic(characteristic);
            }

            @Override
            public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
                super.onCharacteristicChanged(gatt, characteristic, value);

                // HR anzeigen
                TextView hrValue = findViewById(R.id.hrValue);
                runOnUiThread(() -> hrValue.setText(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1) + ""));

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