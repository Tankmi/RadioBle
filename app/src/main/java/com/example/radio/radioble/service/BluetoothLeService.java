package com.example.radio.radioble.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.radio.radioble.context.SampleGattAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Created by zt on 2017/6/17.
 */

public class BluetoothLeService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    /**
     * 缓存的已连接的设备地址
     */
    private String mBluetoothDeviceAddress;
    /**
     * 设备连接状态标记
     */
    public int mConnectionState = STATE_DISCONNECTED;
    public static final int STATE_DISCONNECTED = 0;
    /**
     * 正在连接
     */
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private BluetoothGatt mBluetoothGatt;
    /**
     * 广播 连接成功
     */
    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    /**
     * 广播 断开连接
     */
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    /**
     * 广播 成功获取到服务
     */
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    /**
     * 广播 成功获取到值
     */
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    /**
     * 广播 传值Intent Value 数据解析成功
     */
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    /**
     * 心率监测的UUID对象
     */
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID EAR_GETDATA_UUID = UUID.fromString("0000aaf2-0000-1000-8000-00805f9b34fb");

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    /**
     * 获取BLE的服务级 Services
     * BLE分为三部分Service、Characteristic、Descriptor，
     * 这三部分都由UUID作为唯一标示符。
     * 一个蓝牙4.0的终端可以包含多个Service，一个Service可以包含多个Characteristic（特征），
     * 一个Characteristic包含一个Value和多个Descriptor（描述符），
     * 一个Descriptor包含一个Value。
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }

    /**
     * 发送值
     */
    public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LOG("发送值，适配器或者设备对象为空");
            return;
        }
        LOG("发送值，发送中");
        mBluetoothGatt.writeCharacteristic(characteristic);

    }

    /**
     * 收到值
     *
     * @param characteristic
     * @param enabled
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LOG("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (descriptor != null) {
            LOG("write descriptor 允许读写数据");
            mBluetoothGatt.writeDescriptor(descriptor);
        }else{
            LOG("write descriptor 允许读写数据授权失败！");
        }
    }

    /**
     * 设备操作回调
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                LOG("回调 ：连接成功。设备服务信息：" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                LOG("回调 ：断开连接。");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LOG("回调 ：获取服务成功。");
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                LOG("回调 ：获取服务失败，状态码:" + status);
            }
        }

        /**  读取某个characteristic值 */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LOG("回调 ：收到设备返回的数据");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        /** onCharacteristicRead（）方法 读取成功后的回调*/
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getValue() != null) {
//                System.out.println("string10--->"   + BytesToStr(characteristic.getValue()));
//                System.out.println("string16--->" + IntsToStr(characteristic.getValue()));
//                System.out.println("string2--->" + IntsToStr2(characteristic.getValue()));
            }
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

    };

    /**
     * 发送带参数的广播
     */
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                LOG("解析到的数据是16进制");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                LOG("解析到的数据是8进制");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            LOG(String.format("收到的心率: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            byte[] arrayOfByte = characteristic.getValue();
            if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
                intent.putExtra(EXTRA_DATA, arrayOfByte);
            }
//            final byte[] data = characteristic.getValue();
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for(byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
//            }
        }
        sendBroadcast(intent);
    }

    /**
     * 发送不带参数的广播
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 连接指定设备地址的设备
     *
     * @param address 设备地址
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null) {
            LOG("适配器获取失败");
            return false;
        }
        if (address == null) {
            LOG("设备地址为空");
            return false;
        }

        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            LOG("指定的设备地址与缓存的设备地址一致");
            if (mBluetoothGatt.connect()) { //返回true 表示正在连接
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);  //获取指定地址的设备对象
        if (device == null) {
            LOG("指定地址的设备对象为空");
            return false;
        }
        // 设置自动连接参数为false，以便于马上连接上
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        LOG("设备信息正常，正在与设备建立连接，等待系统回调连接结果...");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LOG("断开连接，非法操作，没有初始化适配器或者没有连接到蓝牙设备，无须断开连接");
            return;
        }
        LOG("设备信息正常，正在与设备断开连接，等待系统回调连接结果...");
        mBluetoothGatt.disconnect();
    }

    /**
     * 初始化蓝牙控制器以及适配器
     */
    public boolean initialize() {
        // 4.0以上才可使用
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                LOG("蓝牙控制器获取失败");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            LOG("蓝牙适配器获取失败");
            return false;
        }

        return true;
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    /**
     * 关闭
     */
    private void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    private void LOG(String str) {
        Log.i("spoort_list","蓝牙服务模块: " + str);
    }
}
