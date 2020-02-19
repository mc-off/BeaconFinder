package ru.mc.off.beaconfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private TextView textView;

    private boolean mScanning = true;
    private Handler handler = new Handler();
    private ArrayList<BluetoothDevice> leDeviceListAdapter = new ArrayList<>();
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            textView.setText("Results\n");
            for (ScanResult result:
                 results) {
                textView.append("Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            textView.setText("Scan failed..." + errorCode);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //Toast.makeText(this,"Result is here", Toast.LENGTH_SHORT).show();
            textView.setText("Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");
        }
    };



    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.stop_button).setOnClickListener(this);
        setBluetoothAdapter();
      //  scanLeDevice(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.start_button): {
                startScanning();
                break;
            }
            case (R.id.stop_button):{
                stopScanning();
                break;
            }
        }
    }

    public void startScanning() {
        Toast.makeText(this, R.string.start_scan, Toast.LENGTH_LONG).show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        Toast.makeText(this, R.string.stop_scan, Toast.LENGTH_LONG).show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan(leScanCallback);
            }
        });
    }
    public void setBluetoothAdapter(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        } else{
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 0);
            }
        }
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    bluetoothLeScanner.startScan(leScanCallback);
                }
            });
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    bluetoothLeScanner.stopScan(leScanCallback);
//                }
//            }, SCAN_PERIOD);

            System.out.println(R.string.stop_scan);
            Toast.makeText(this, R.string.stop_scan, Toast.LENGTH_SHORT).show();

            mScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            System.out.println(R.string.start_scan);
            Toast.makeText(this, R.string.start_scan, Toast.LENGTH_SHORT).show();

        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }





    // Use this check to determine whether BLE is supported on the device. Then
// you can selectively disable BLE-related features.

}
