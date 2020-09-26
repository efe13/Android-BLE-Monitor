package com.huc.android_ble_monitor.adapters;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.huc.android_ble_monitor.R;
import com.huc.android_ble_monitor.util.PropertyResolver;
import com.huc.android_ble_monitor.util.IdentifierXmlResolver;

import java.util.List;

public class ServicesListAdapter extends ArrayAdapter<BluetoothGattService> {
    PropertyResolver propertyResolver;
    TextView tvServiceName;
    TextView tvServiceUuid;
    TextView tvServiceIdentifier;
    TextView tvServiceLink;

    public ServicesListAdapter(@NonNull Context context, List<BluetoothGattService> bluetoothGattServices) {
        super(context, 0, bluetoothGattServices);
        propertyResolver = new PropertyResolver(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothGattService bluetoothGattService = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_list_item, parent, false);
        }

        initializeViews(convertView);
        resolveViews(bluetoothGattService);

        return convertView;
    }

    private void initializeViews(View view) {
        tvServiceName = view.findViewById(R.id.tv_service_item_name);
        tvServiceUuid = view.findViewById(R.id.tv_service_item_uuid);
        tvServiceIdentifier = view.findViewById(R.id.tv_service_item_identifier);
        tvServiceLink = view.findViewById(R.id.tv_service_identifier_link);
    }

    private void resolveViews(BluetoothGattService bluetoothGattService) {
        tvServiceName.setText(this.propertyResolver.serviceNameResolver(bluetoothGattService));
        tvServiceUuid.setText(this.propertyResolver.serviceUuidResolver(bluetoothGattService));
        tvServiceIdentifier.setText(this.propertyResolver.serviceIdentifierResolver(bluetoothGattService));

        String serviceId = propertyResolver.serviceIdentifierResolver(bluetoothGattService);
        if(!serviceId.equals(PropertyResolver.SIG_UNKNOWN_SERVICE_IDENTIFIER)) {
            tvServiceLink.setText(IdentifierXmlResolver.getServiceXmlLink(serviceId));
        }else{
            tvServiceLink.setVisibility(View.GONE);
        }
    }
}
