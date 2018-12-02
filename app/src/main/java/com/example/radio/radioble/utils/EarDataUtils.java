package com.example.radio.radioble.utils;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 耳穴数据处理工具
 * @author ZhuTao
 * 
 */
public class EarDataUtils {// 发送数据

	private static List<Integer> mEars;
	private static int mPressureRange;

	/** 重新检测，初始化集合 */
	public static void releasetData(){
		if(mEars != null && mEars.size()>=20){
			mEars.clear();
			mEars = null;
		}
	}

	/**
	 *  集合中插入数据,返回：-1，继续监测，否则表示成功
	 */
	public static int insertEarNumber(byte[] bytes) {
//		if(isTrue(bytes) && isOk(getEarNumber(bytes))){	//产生结果值了
//		for(int i=0; i<bytes.length; i++){
//			LOG("遍历数据：" + i + "  :  " + Integer.parseInt(Integer.toString(0XFF & bytes[i], 10)));
//		}
		for(int i=0; i<bytes.length; i+=5){
			byte[] newbyte = new byte[5];
			for(int j = i,k=0;j<i+5;j++,k++){
				newbyte[k] = bytes[j];
			}

			if(isTrue(newbyte)){	//产生结果值了
				int num = getEarNumber(newbyte);
//				if(mUpEarDataList != null)mUpEarDataList.updataEarData(subData(num,4));
				if(mUpEarDataList != null)mUpEarDataList.updataEarData(num);
				if(isOk(num)){
					num = mEars.get(mEars.size()-1);
					return num;
				}
			}else{
//				LOG("无效数");
			}
		}
		return -1;
	}

	//获取数据
	public static Integer getEarNumber(byte[] bytes) {
		int h = Integer.parseInt((Integer.toString(bytes[2] << 8, 10)));
		int l = Integer.parseInt(Integer.toString(0XFF & bytes[3], 10));
		int num = h + l;
		if(num == 0) return 9999;
		return (int)((4096f - num)/num * 680f);
	}

	/**
	 *  值有效范围：大于4096 - 当前值结果大于100，小于3000就是有效值？，连续20个值，差值小于30。
	 */
	private static boolean isOk(int num){
		if(num>100 && num<3000){
		LOG("值：" + num);
//		if(num>100){
			if(mEars == null ) mEars = new ArrayList<Integer>();
			int lastNum = mEars.size()>0?mEars.get(mEars.size()-1):-1;	//获取集合最新的一个值，为空时值为-1
			if(lastNum == -1){	//集合是空的
				mEars.add(num);
				LOG("集合值为空：" + mEars.size() + ":  " + num);
			}
			else if(Math.abs(num - lastNum) <= 20){
				mEars.add(num);
				LOG("临近值有效：" + mEars.size() + ":  " + num);
			}else{
				mEars.clear();
				mEars.add(num);
				LOG("新增值：" + mEars.size() + ":  " + num);
			}
			if(mEars.size() >= 30) return true;
			else return false;
		}
		else {
			return false;
		}
	}

	/** 判断校验和 */
	public static boolean isTrue(byte[] bytes) {
		int num1 = 0,num2 = 0;
		if(bytes.length == 5){
			if((0xff & bytes[1]) != mPressureRange){
				LOG("无效数据：" + (0xff & bytes[1]));
				return false;
			}
			num1 = Integer.parseInt(Integer.toString((0xff & bytes[0]) + (0xff & bytes[1]) + (0xff & bytes[2]) + (0xff & bytes[3]), 10));
			num1 = 0xff & num1;
			num2 = Integer.parseInt(Integer.toString((0xff & bytes[4]), 10));
			LOG("校验和" + num1 + "::" + num2  + ";;bytes:" + (0xff & bytes[0]) + ","+ (0xff & bytes[1]) + ","+ (0xff & bytes[2]) + ","+ (0xff & bytes[3]) + ","+ (0xff & bytes[4]) + ",");
			if((num1) == num2) {
				return true;
			}

		}
		return false;
	}

	/** 获取集合大小 */
	public  static int getEarsSize(){
		return mEars!=null?mEars.size():0;
	}

	/** 获取校准值 */
	public  static int getEarsData(){
		return mEars!=null?mEars.size()>0?mEars.get(mEars.size() - 1):-999:-999;
	}

	public static void LOG(String data){
		Log.i("ear_log", "数据运算工具类：" + data + "");
	}

	/** 长数据转化 */
	public static int subData(int data,int length){
		String strData = data + "";
		if(strData.length()>length){
			strData = strData.substring(strData.length() - length);
		}
		return Integer.parseInt(strData);
	}

	/** 更新压力级 0 1 2 3 */
	public void setPressureRange(int mPressureRange){
		this.mPressureRange = mPressureRange;
	}

	public void setEarDataListener(UpdataEardataListener mupEarDataList){
		this.mUpEarDataList = mupEarDataList;
	}

	protected static UpdataEardataListener mUpEarDataList;

	public interface UpdataEardataListener{
		void updataEarData(int data);
	}
}