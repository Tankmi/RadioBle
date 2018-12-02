package com.example.radio.radioble.activity.DeviceControlActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.radio.radioble.R;
import com.example.radio.radioble.base.BaseActivity;
import com.example.radio.radioble.service.BluetoothLeService;
import com.example.radio.radioble.utils.EarDataUtils;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 扫描
 */
public class DeViceControlBaseActivity extends BaseActivity implements View.OnClickListener{

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    public String mDeviceAddress;
    /** 标记连接状态  */
    public boolean mConnected = false;
    public BluetoothLeService mBluetoothLeService;
    public boolean mScanning;
    /** 扫描周期10S */
    private static final long SCAN_PERIOD = 10000;
    /** 蓝牙发送值 */
    public BluetoothGattCharacteristic writeBluetoothGattCharacteristic;
    public BluetoothGattCharacteristic readBluetoothGattCharacteristic;

//    private BluetoothAdapter mBluetoothAdapter;
//    public final int BLE_INIT = 100;
//    public final int BLE_INITBLE = 101;

    private LinearLayout lin_device_control_main;
    private LinearLayout lin_device_con_title;
    private ProgressBar pb_device_con;
    private TextView tv_device_con;
    public LinearLayout lin_device_con_con;
    private Button btn_device_con_conn;
    private Button btn_device_con_disconn;
    /** 继续监测 */
    private Button btn_device_detection;
    protected TextView tv_device_con_data;
    private TextView tv_device_con_info1;
    private TextView tv_device_con_info;

    public DeViceControlBaseActivity(int layoutId) {
        super(layoutId);
    }

    @Override
    protected void initHead() {
        mDeviceName = getIntent().getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);
        LOG("mDeviceAddress:" + mDeviceAddress);
    }

    @Override
    protected void initLogic() {
//        mBtnRight.setOnClickListener(this);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

//        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            tv_device_con.setText("正在连接设备，状态位：" + result);
//            LOG(result ? "正在连接设备" : "连接设备有误");
//        }else{
//            LOG("mBluetoothLeService == null");
//        }
    }

    /**
     * 更新提示信息
     *
     * @param state -1，断开连接，重新连接;
     *              0，正在断开连接；-10,断开连接成功。
     *              1，连接成功;
     *              2,正在建立连接
     */
    public void updataHint(int state,String str) {
        if (state == -1) {
            pb_device_con.setVisibility(View.VISIBLE);
            btn_device_detection.setVisibility(View.GONE);
            tv_device_con.setText("断开连接，正在重连，" + str);
        } else if (state == 0) {
            pb_device_con.setVisibility(View.GONE);
            btn_device_detection.setVisibility(View.GONE);
            tv_device_con.setText("正在断开连接" + str);

        } else if (state == -10) {
            pb_device_con.setVisibility(View.GONE);
            btn_device_detection.setVisibility(View.GONE);
            tv_device_con.setText("断开连接成功" + str);

        } else if (state == 1) {
            pb_device_con.setVisibility(View.GONE);
            tv_device_con.setText("连接成功" + str);
        } else if (state == 2) {
            pb_device_con.setVisibility(View.VISIBLE);
            btn_device_detection.setVisibility(View.GONE);
            tv_device_con.setText("正在连接" + str);
        }
    }


    // 绑定service实现的管理对象
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            LOG("onServiceConnected ");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                LOG("蓝牙服务初始化失败！");
//                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            tv_device_con.setText("正在连接设备，状态位：" + result);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /**
     * 广播接收器
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updataHint(1,"");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if(!mConnected){    //手动断开连接，无须重连
                    updataHint(-10,"");
                }else{
                    mConnected = false;
                    final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                    updataHint(-1,"状态位：" + result);
                }
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                LOG("收到服务");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] byteValues = intent .getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                // addByte(byteValues);
//                displayData(byteValues);
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                spDisplayData(BLESendAndReceiveData.receiveValues(byteValues));
                spDisplayData(byteValues);
            }
        }
    };

    protected EarDataUtils mEarDataUtils;
    /**
     * 运动数据回显
     * @param bytes 接收到的数据串
     */
    public void spDisplayData(byte[] bytes){
        if(!isDetection) return;    //是否进行监测
        //***测试，收到数据后跳转
//        ScanDevice(false);
        if(!(bytes !=null && bytes.length>=15)){
//            LOG("byte数据异常" + (bytes ==null?"null":bytes.length));
            return;
        }
        LOG("校验位（1，有效，0，未接触，2，二级按压力度，3，三级按压力度）："  + (0xff & bytes[1]) + ";集合长度：" + bytes.length);
        if ((0xff & bytes[1]) == pressureRange) {    //按压力度合适
            if ((int) tv_device_con_info1.getTag() != 1) {
                tv_device_con_info1.setTag(1);
                tv_device_con_info1.setTextColor(mContext.getResources().getColor(R.color.green));
                tv_device_con_info1.setText("压力正常");
            }
        } else if ((0xff & bytes[1]) == 0x00) {
            if ((int) tv_device_con_info1.getTag() != 0) {
                tv_device_con_info1.setTag(0);
                tv_device_con_info1.setTextColor(mContext.getResources().getColor(R.color.gray));
                tv_device_con_info1.setText("压力太小");
            }
//        } else if((0xff & bytes[1]) == 0x02 || (0xff & bytes[1]) == 0x03){
        } else {
            if ((int) tv_device_con_info1.getTag() != 2) {
                tv_device_con_info1.setTag(2);
                tv_device_con_info1.setTextColor(mContext.getResources().getColor(R.color.red));
                tv_device_con_info1.setText("压力过重");
            }
        }
//            LOG("bytes:" + (0xff & bytes[0]) + ","+ (0xff & bytes[1]) + ","+ (0xff & bytes[2]) + ","+ (0xff & bytes[3]) + ","+ (0xff & bytes[4]));

//            LOG( "当前有效数值" + BLESendAndReceiveData.getEarNumber(bytes) + "    " + BLESendAndReceiveData.isTrue(bytes));
//            LOG( "当前有效数值,插入数值结果：" + BLESendAndReceiveData.insertEarNumber(bytes));
            int data = mEarDataUtils.insertEarNumber(bytes);
//            LOG( "数值:" + Math.abs(data) + "      ；数据集大小(满20有效)： " + EarDataUtils.getEarsSize()+ "  ,有效值： " +EarDataUtils.getEarsData());

            if(mEarDataUtils.getEarsData()>-999){
                sendPhoneType(3);   //找到穴位

                updateInfo(1 , "数据集大小： " + mEarDataUtils.getEarsSize() + "    \n" +
                        "集合校准值(!=-999表示找到穴位)：" + mEarDataUtils.getEarsData());
            }else{
                updateInfo(0, "");
            }
            if(data > 0){
                sendPhoneType(1);
            }

    }
    /**
     *
     * @param type 1,有效值，0，无效值
     */
    private void updateInfo(int type,String data){
        if(!isDetection) return;
        if(type == 1){
            tv_device_con_info.setText("穴位校准成功:  " + data);
        }
        else {
            tv_device_con_info.setText("穴位校准中" + data);
        }
        tv_device_con_info.setTag(type);
    }

    public static final UUID EAR_SENDDATA_UUID = UUID.fromString("0000aaf1-0000-1000-8000-00805f9b34fb");
    public static final UUID EAR_GETDATA_UUID = UUID.fromString("0000aaf2-0000-1000-8000-00805f9b34fb");

    public void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            LOG("获取到的服务集为Null");
            return;
        }
        for (BluetoothGattService gattService : gattServices) { //便利服务集获取Characteristic集
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            Iterator<BluetoothGattCharacteristic> iterator = gattCharacteristics.iterator();
            if (!iterator.hasNext()) {
                LOG("获取到的Characteristic集为Null");
                return;
            }
            LOG("获取到的UUID :" + gattCharacteristics.size() +":   " + gattService.getUuid());
            try {
                for (BluetoothGattCharacteristic characteristic : gattCharacteristics) {
                    LOG("遍历UUID:  " + characteristic.getUuid().toString());
                    if(characteristic.getUuid().toString().equals(EAR_GETDATA_UUID.toString())) {
                        LOG("耳穴监测接收值: " + "aaf2");
                        readBluetoothGattCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(  readBluetoothGattCharacteristic, true);
                    }else if(characteristic.getUuid().toString().equals(EAR_SENDDATA_UUID.toString())) {
                        writeBluetoothGattCharacteristic = characteristic;
                        LOG("设备给耳穴监测发送值: " + "aaf1");
//                        sendPhoneType(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendPhoneType(0);
                            }
                        }, 300);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                sendPhoneType(4);
//                            }
//                        }, 500);
                    }
                }
            } catch (Exception e) {
                LOG("解析Characteristic集出现异常");
                return;
            }
        }
    }

    /** 找到穴位，只提示一遍 */
    private Boolean isFind = false;
    /** 按压区间 0123 */
    protected int pressureRange = 0x01;
    /**
     *
     * @param type 0,激活发送数据，持续发送
     *             1，测量成功
     *             2，只传有效数据
     *             3，找到穴位
     *             4,按压区间更新为2
     */
    public void sendPhoneType(int type){

        if(writeBluetoothGattCharacteristic != null){
            try{
                if(type == 0){	//激活发送数据，持续发送
                    LOG( "激活发送数据，持续发送" + type);
                    writeBluetoothGattCharacteristic.setValue(new byte[] { (byte) 0xAA, (byte)0x03,(byte)0x00, (byte)0xAD});
                    mBluetoothLeService.wirteCharacteristic(writeBluetoothGattCharacteristic);
                } else if(type == 1){    //测量成功 长响 三声
                    LOG( "测量成功" + type);
                    detectionState(true);
                    writeBluetoothGattCharacteristic.setValue(new byte[] { (byte) 0xAA, (byte)0x02, (byte)0x03, (byte)0xAF});
                    mBluetoothLeService.wirteCharacteristic(writeBluetoothGattCharacteristic);
                } else if(type == 3){    //找到穴位 短响 三声
                    if(isFind) return;
                    isFind = true;
                    LOG( "找到穴位" + type);
                    writeBluetoothGattCharacteristic.setValue(new byte[] { (byte) 0xAA, (byte)0x01, (byte)0x03, (byte)0xAE});
                    mBluetoothLeService.wirteCharacteristic(writeBluetoothGattCharacteristic);
                } else if(type == 4){    //按压区间更新为2
                    pressureRange = 0x02;
                    mEarDataUtils.setPressureRange(pressureRange);
                    LOG( "按压区间更新为2  " + type);
                    writeBluetoothGattCharacteristic.setValue(new byte[] { (byte) 0xAA, (byte)0x04, (byte)0x02, (byte)0xB0});
                    mBluetoothLeService.wirteCharacteristic(writeBluetoothGattCharacteristic);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 前端展示更新的信息
     */
    public void updateConnectionState(String str) {

    }

    /** 是否正在监测 */
    protected boolean isDetection = true;
    /** 更新监测状态 */
    protected void detectionState(boolean state){
       if(state){
           isFind = false;
           isDetection = false;
           btn_device_detection.setVisibility(View.VISIBLE);  //测量成功，显示继续监测
       }
        else{
           isDetection = true;
           btn_device_detection.setVisibility(View.GONE);  //继续监测，隐藏按钮
           mEarDataUtils.releasetData();
       }
    }

    /**
     * 添加广播接收的类型
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void initContent() {
        lin_device_control_main = findViewByIds(R.id.lin_device_control_main);
        lin_device_con_title = findViewByIds(R.id.lin_device_con_title);
        pb_device_con = findViewByIds(R.id.pb_device_con);
        tv_device_con = findViewByIds(R.id.tv_device_con);
        lin_device_con_con = findViewByIds(R.id.lin_device_con_con);
        btn_device_con_conn = findViewByIds(R.id.btn_device_con_conn);
        btn_device_con_disconn = findViewByIds(R.id.btn_device_con_disconn);
        btn_device_detection = findViewByIds(R.id.btn_device_detection);
        tv_device_con_info1 = findViewByIds(R.id.tv_device_con_info1);
        tv_device_con_data = findViewByIds(R.id.tv_device_con_data);
        tv_device_con_info = findViewByIds(R.id.tv_device_con_info);

        btn_device_con_conn.setOnClickListener(this);
        btn_device_con_disconn.setOnClickListener(this);
        btn_device_detection.setOnClickListener(this);
        btn_device_detection.setVisibility(View.GONE);
        tv_device_con_info1.setTag(-1);
        tv_device_con_info.setTag(-1);
    }

    @Override
    protected void initLocation() {
        mLayoutUtil.drawViewRBLinearLayout(lin_device_con_title, 0, 0, 0, 0, 20, 20);
        mLayoutUtil.drawViewRBLinearLayout(pb_device_con, 50, 50, 0, 0, 0, 0);
    }


    /**
     * 退出操作
     */
    private void close() {
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void pauseClose() {

    }

    @Override
    protected void destroyClose() {
        close();
    }

}
