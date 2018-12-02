package com.example.radio.radioble.activity.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.radio.radioble.R;
import com.example.radio.radioble.base.BaseActivity;
import com.example.radio.radioble.base.MBaseAdapter;
import com.example.radio.radioble.utils.ToastUtils;
import com.example.radio.radioble.view.xlist.XListView;

import java.util.ArrayList;

/**
 * 扫描
 */
public class DeViceScanBaseActivity extends BaseActivity implements AdapterView.OnItemClickListener,View.OnClickListener{

    public boolean mScanning;
    /** 扫描周期10S */
    private static final long SCAN_PERIOD = 10000;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    public final int BLE_INIT = 100;
    public final int BLE_INITBLE = 101;

    private LinearLayout lin_device_scan_main;
    private LinearLayout lin_device_scan_title;
    private ProgressBar pb_device_scan;
    private TextView tv_device_scan;
    public ListView xlist_device_scan;

    public DeViceScanBaseActivity(int layoutId) {
        super(layoutId);
    }

    @Override
    protected void initHead() {
        mBtnLeft.setVisibility(View.GONE);
    }

    @Override
    protected void initLogic() {
        tv_device_scan.setText("查找设备中");
        mBtnRight.setOnClickListener(this);
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            updataHint(1);
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning){
                        updataHint(2);
                    }
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            updataHint(3);
            mScanning = false;
            if(mBluetoothAdapter != null){
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }
    /** 更新提示信息
     * @param state -1，启动蓝牙失败；0,打开蓝牙；1，搜索设备，2，搜索结束；3，停止搜索
     * */
    private void updataHint(int state){
        if(state == 0){
            pb_device_scan.setVisibility(View.GONE);
            tv_device_scan.setText("正在启动蓝牙");
        }else if(state == -1){
            pb_device_scan.setVisibility(View.GONE);
            tv_device_scan.setText("蓝牙启动失败！");
        }else if(state == 1){
            pb_device_scan.setVisibility(View.VISIBLE);
            tv_device_scan.setText("正在搜索");

            mBtnRight.setText("停止搜索");
            mBtnRight.setVisibility(View.VISIBLE);
        }else if(state == 2){
            pb_device_scan.setVisibility(View.GONE);
            tv_device_scan.setText("搜索结束");

            mBtnRight.setText("重新搜索");
            mBtnRight.setVisibility(View.VISIBLE);
        }else if(state == 3){
            pb_device_scan.setVisibility(View.GONE);
            tv_device_scan.setText("停止搜索");

            mBtnRight.setText("重新搜索");
            mBtnRight.setVisibility(View.VISIBLE);
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case BLE_INIT:	//初始化蓝牙
                    //注册广播，开启服务，扫描设备
                    if(initBluetoothAdapters()){
                         scanLeDevice(true);
                    }else{
                        updataHint(0);
                        mHandler.sendEmptyMessageDelayed(BLE_INITBLE,2000);
                    }
                    break;
                case BLE_INITBLE:	//二次启动蓝牙
                    if(initBluetoothAdapters()){
                        scanLeDevice(true);
                    }else{
                        updataHint(-1);
                    }
                    break;
                default:
                    break;
            }
        };
    };



    @Override
    protected void initContent() {
        lin_device_scan_main = findViewByIds(R.id.lin_device_scan_main);
        lin_device_scan_title = findViewByIds(R.id.lin_device_scan_title);
        pb_device_scan = findViewByIds(R.id.pb_device_scan);
        tv_device_scan = findViewByIds(R.id.tv_device_scan);
        xlist_device_scan = findViewByIds(R.id.xlist_device_scan);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        xlist_device_scan.setOnItemClickListener(this);
        xlist_device_scan.setAdapter(mLeDeviceListAdapter);

    }

    @Override
    protected void initLocation() {
        mLayoutUtil.drawViewRBLinearLayout(lin_device_scan_title, 0, 0, 0, 0, 20, 20);
        mLayoutUtil.drawViewRBLinearLayout(pb_device_scan, 50, 50, 0, 0, 0, 0);
    }

    /** 退出操作 */
    private void closes(){
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    public void onClick(View v) {

    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeViceScanBaseActivity.this.getLayoutInflater();
        }


        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText("未知设备");
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }

    }

    /**
     * 扫描到结果后便会执行此回调
     */
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            LOG("扫描到的设备Name: " + device.getName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };


    /**
     * 初始化蓝牙适配器对象
     */
    public boolean initBluetoothAdapters(){

        if(mBluetoothAdapter == null){
            // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上版本)
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            LOG("initBluetoothAdapters 正在启动蓝牙");
            //弹窗打开蓝牙
            //				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothAdapter.enable();
            return false;
        }
        return true;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    @Override
    protected void pauseClose() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    protected void destroyClose() {
        closes();
    }
}
