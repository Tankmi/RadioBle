package com.example.radio.radioble.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.radio.radioble.R;
import com.example.radio.radioble.context.ApplicationData;


public class NetUtils{

	private static final int CMNET = 3;
	private static final int CMWAP = 2;
	private static final int WIFI = 1;

	/**
	 * 获取当前的网络状态-1：没有网络；1：WIFI网络；2：wap网络；3：net网络
	 * 
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			 Resources resource = (Resources) ApplicationData.context.getResources();
//			ToastUtils.showToast("" + resource.getString(R.string.netstate_error));
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
//			Log.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is " + networkInfo.getExtraInfo());
			if (networkInfo.getExtraInfo().equalsIgnoreCase("cmnet")) {
				netType = CMNET;
			} else {
				netType = CMWAP;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = WIFI;
		}
		return netType;
	}
	
	/**
	 * 检查是否有网络
	 * 
	 * @param context
	 * @return 
	 */
	public static boolean isAPNType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			Resources resource = (Resources) ApplicationData.context.getResources(); 
			ToastUtils.showToast("" + resource.getString(R.string.netstate_error));
			return false;
		}
		return true;
	}
}
