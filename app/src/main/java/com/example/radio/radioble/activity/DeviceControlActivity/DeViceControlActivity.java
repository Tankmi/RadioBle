package com.example.radio.radioble.activity.DeviceControlActivity;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;

import com.example.radio.radioble.R;
import com.example.radio.radioble.utils.EarDataUtils;
import com.example.radio.radioble.utils.ToastUtils;

/**
 * 控制
 */
public class DeViceControlActivity extends DeViceControlBaseActivity {

    public DeViceControlActivity() {
        super(R.layout.activity_device_control);
    }

    @Override
    protected void initHead() {
        super.initHead();
        setTittle("连接设备");

        mEarDataUtils = new EarDataUtils();
        mEarDataUtils.setEarDataListener(new EarDataUtils.UpdataEardataListener() {
            @Override
            public void updataEarData(int data) {
                if(tv_device_con_data != null) tv_device_con_data.setText(data + "");

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_device_con_conn:
                if(mBluetoothLeService != null && mBluetoothLeService.mConnectionState == mBluetoothLeService.STATE_DISCONNECTED){
                    final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                    updataHint(2,"");
                }else{
                    LOG("设备正在建立连接，驳回重复连接操作");
                }
                break;
            case R.id.btn_device_con_disconn:
                if(mBluetoothLeService != null && mBluetoothLeService.mConnectionState == mBluetoothLeService.STATE_CONNECTED){
                    mConnected = false; //手动设置断开连接
                    mBluetoothLeService.disconnect();
                    updataHint(0,"");
                }else{
                    LOG("设备没有建立连接，驳回断开连接操作");
                }
                break;
            case R.id.btn_device_detection: //继续监测
                detectionState(false);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        mHandler.sendEmptyMessage(BLE_INIT);
    }

    @Override
    protected void initLogic() {
        super.initLogic();
    }

    @Override
    protected void destroyClose() {
        super.destroyClose();
        if(mBluetoothLeService != null && mBluetoothLeService.mConnectionState == mBluetoothLeService.STATE_CONNECTED){
            mBluetoothLeService.disconnect();
        }
    }
}
