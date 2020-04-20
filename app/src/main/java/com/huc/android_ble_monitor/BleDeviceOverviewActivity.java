package com.huc.android_ble_monitor;

import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.huc.android_ble_monitor.models.BleDevice;
import com.huc.android_ble_monitor.util.ActivityUtil;
import com.huc.android_ble_monitor.util.BLEPropertyToViewResolver;
import com.huc.android_ble_monitor.viewmodels.BleDeviceOverviewViewModel;

import java.util.ArrayList;

public class BleDeviceOverviewActivity extends AppCompatActivity {
    private static final String TAG = "BLEM_BleDeviceOverview";

    public static BleDevice  staticBleDevice;
    private BleDeviceOverviewViewModel mBleDeviceOverviewViewModel;
    private  BLEPropertyToViewResolver blePropertyToViewResolver;

    private TextView tvName;
    private TextView tvAddress;
    private TextView tvBonded;
    private TextView tvRssi;
    private TextView tvConnectability;
    private TextView tvCompanyIdentifier;
    private TextView tvServices;
    private ImageView ivBondstate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme); // Resets default theme after app was loaded
        setContentView(R.layout.activity_ble_device_overview);

        ActivityUtil.setToolbar(this, false);
        initializeViews();
        blePropertyToViewResolver = new BLEPropertyToViewResolver(this);

        mBleDeviceOverviewViewModel = ViewModelProviders.of(this).get(BleDeviceOverviewViewModel.class);
        mBleDeviceOverviewViewModel.init(staticBleDevice);

        mBleDeviceOverviewViewModel.getmBleDevice().observe(this, new Observer<BleDevice>() {
            @Override
            public void onChanged(BleDevice bleDevice) {
                Log.d(TAG, "onChanged: BleDevice value changed");
                mapBleObjectToView(bleDevice);
            }
        });
    }

    public void initializeViews() {
        tvName = findViewById(R.id.DeviceName_TextView);
        tvAddress = findViewById(R.id.DeviceUUID_TextView);
        tvBonded = findViewById(R.id.BondState_TextView);
        tvRssi = findViewById(R.id.RSSI_TextView);
        tvConnectability = findViewById(R.id.Connectability_TextView);
        tvCompanyIdentifier = findViewById(R.id.CompanyIdentifier_TextView);
        ivBondstate = findViewById(R.id.BondState_ImageView);
        tvServices = findViewById(R.id.Services_TextView);
    }

    public void mapBleObjectToView(BleDevice bleDevice) {
        ScanResult bleScanResult = bleDevice.mScanResult;
        tvBonded.setText(blePropertyToViewResolver.bondStateTextResolver(bleScanResult));
        ivBondstate.setImageResource(blePropertyToViewResolver.bondStateImageResolver(bleScanResult));
        tvName.setText(blePropertyToViewResolver.deviceNameResolver(bleScanResult));
        tvAddress.setText(blePropertyToViewResolver.deviceAddressResolver(bleScanResult));
        tvRssi.setText(blePropertyToViewResolver.deviceRssiResolver(bleScanResult));
        tvCompanyIdentifier.setText(blePropertyToViewResolver.deviceManufacturerResolver(bleScanResult));
        tvConnectability.setText(blePropertyToViewResolver.deviceConnectabilityResolver(bleScanResult));

        ArrayList<String> uuids = blePropertyToViewResolver.deviceServiceResolver(bleDevice, bleScanResult);
        tvServices.setText("Services (" + bleDevice.getServiceCount() + ")");
    }
}
