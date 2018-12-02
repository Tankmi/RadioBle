package com.example.radio.radioble.activity.scan;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.example.radio.radioble.R;
import com.example.radio.radioble.activity.DeviceControlActivity.DeViceControlActivity;
import com.example.radio.radioble.base.BaseActivity;

/**
 * 扫描
 */
public class DeViceScanActivity extends DeViceScanBaseActivity{

    public DeViceScanActivity() {
        super(R.layout.activity_device_scan);
    }

    @Override
    protected void initHead() {
        super.initHead();
        setTittle("查找设备");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_title_view_right:
                  scanLeDevice(!mScanning);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        final BluetoothDevice device = (BluetoothDevice) xlist_device_scan.getItemAtPosition(position);
        if (device == null) return;
        LOG("选中的设备：" + device.getName() + ";设备地址：" + device.getAddress());
        final Intent intent = new Intent(this, DeViceControlActivity.class);
        intent.putExtra(DeViceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeViceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            scanLeDevice(false);
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(BLE_INIT);
    }

    @Override
    protected void initLogic() {
        super.initLogic();
    }

}
