package com.example.radio.radioble.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据的收发
 * @author ZhuTao
 * 
 */
public class BLESendAndReceiveData {// 发送数据


//	public static byte[] sendValues(byte[] b) {
//		ArrayList<Byte> byteList = new ArrayList<Byte>();
//		byteList.add((byte) 0xAA);
//		byteList.add((byte) 0x01);
//
//		byteList.add((byte) 0xAB);
////		for (int i = 0; i < b.length; i++) {
////			byteList.add(b[i]);
////		}
////		byte[] values = new byte[byteList.size() + 1];
////		byte sumb = 0;
////		for (int i = 0; i < byteList.size(); i++) {
////			values[i] = byteList.get(i);
////			sumb = (byte) (sumb + byteList.get(i));
////		}
////		values[values.length - 1] = sumb;
////		System.out.println("sendbyte--->" + IntsToStr(values));
//		return byteList;
//
//	}

	// 接收数据
	public static byte[] receiveValues(byte[] b) {
		if (b != null && b.length > 3) {
			// 效验新数据
			int needSize = 0;
			if (b.length > 3) {
				needSize = b[2];
			}
			if (needSize > 0) {
				byte sumByte = 0;

				for (int i = 0; i < b.length - 1; i++) {
					sumByte += b[i];
				}
				if (b.length >= needSize + 3 && sumByte == b[b.length - 1]) {
					System.out.println("bfbyte-->"
							+ BLESendAndReceiveData.IntsToStr(b));
					byte[] values = new byte[needSize - 1];
					for (int i = 3; i < needSize + 2; i++) {
						values[i - 3] = b[i];
					}
					return values;
				}
			}

		}
		return null;

	}

	public static String int2String10(byte[] target) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0, j = target.length; i < j; i++) {
			if (i == target.length - 1) {
				buf.append(Integer.toString(target[i]));
			} else {
				buf.append(Integer.toString(target[i]) + ",");
			}

		}
		return buf.toString();
	}

	public static String IntsToStr16(byte[] target) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0, j = target.length; i < j; i++) {
			if (i == target.length - 1) {
				buf.append(Integer.toString(0xFF & target[i], 10));
			} else {
				buf.append(Integer.toString(0xFF & target[i], 10) + ",");
			}

		}
		return buf.toString();
	}

	public static String IntsToStr(byte[] target) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0, j = target.length; i < j; i++) {
			if (i == target.length - 1) {
				buf.append(Integer.toString(0xFF & target[i], 16));
			} else {
				buf.append(Integer.toString(0xFF & target[i], 16) + ",");
			}

		}
		return buf.toString();
	}

	public static String BytesToStr(byte[] target) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0, j = target.length; i < j; i++) {
			if (i == target.length - 1) {
				buf.append(Integer.toString(target[i]));
			} else {
				buf.append(Integer.toString(target[i]) + ",");
			}

		}
		return buf.toString();
	}

	public static byte[] StrToByte(String s) {
		String[] strings = s.split(",");
		if (strings.length > 0) {
			byte[] bytes = new byte[strings.length];
			try {
				for (int i = 0; i < bytes.length; i++) {
					bytes[i] = (byte) Integer.parseInt(strings[i], 16);
				}
				return bytes;
			} catch (Exception e) {
				// TODO: handle exception
				return null;
			}

		} else {
			return null;
		}

	}

	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[8];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < 8; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	// 获取当前时间
	public static String getTime() {
		SimpleDateFormat HMS = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat YMD = new SimpleDateFormat("yyyy年M月dd日");
		String sdfs = YMD.format(new Date());
		String dates = HMS.format(new Date());

		return sdfs + " " + dates;
	}

	/**
	 * 获取血糖值
	 * @param bytes	接收到的数据串
	 * @return	String 血糖值
	 */
	public static String getBloodSugars(byte[] bytes) {

		int num = Integer.parseInt(Integer.toString(0xFF & bytes[12]));
		float gloodglocose = (float) (Math.round((num / 18.0) * 10)) / 10;
//		
		return gloodglocose + "";
	}

	// 获得体重值
	public static float getWeight(byte[] bytes) {
		int h = Integer.parseInt((Integer.toString(bytes[12] << 8, 10)));
		int l = Integer.parseInt((Integer.toString(0xFF & bytes[13], 10)));

		float weight = (float) ((h + l) / 10.0);

		return (Math.round(weight * 10)) / 10.0f;
		//		return weight;
	}

	// 获取单位
	public static String getUnit(byte[] bytes) {
		String unit = null;
		if (Integer.toString(0xFF & bytes[14], 10).equals("1")) {
			unit = "Kg";
		} else if (Integer.toString(0xFF & bytes[14], 10).equals("2")) {
			unit = "lb";
		} else if (Integer.toString(0xFF & bytes[14], 10).equals("4")) {
			unit = "st";
		} else {
			unit = "单位";
		}
		return unit;
	}

	/**
	 *  集合中插入数据,返回：-1，继续监测，否则表示成功
	 */
	public static int insertEarNumber(byte[] bytes) {
//		if(isTrue(bytes) && isOk(getEarNumber(bytes))){	//产生结果值了
		if(isTrue(bytes)){	//产生结果值了
			int num = getEarNumber(bytes);
			if(isOk(num)){
				 num = mEars.get(mEars.size()-1);
				mEars.clear();
				mEars = null;
				return num;
			}
			return -num;
		}
		return -1;
	}

	//获取数据
	public static Integer getEarNumber(byte[] bytes) {
		int h = Integer.parseInt((Integer.toString(bytes[2] << 8, 10)));
		int l = Integer.parseInt(Integer.toString(0XFF & bytes[3], 10));
		int num = h + l;
		return (int)((4096f - num)/num * 680f);
	}

	private static List<Integer> mEars;
	/**
	 *  值有效范围：大于4096 - 当前值结果大于100，小于3000就是有效值？，连续20个值，差值小于30。
	 */
	private static boolean isOk(int num){
//		if(num>100 && num<3000){
		LOG("值：" + num);
		if(num>100){
			if(mEars == null ) mEars = new ArrayList<Integer>();
			int lastNum = mEars.size()>0?mEars.get(mEars.size()-1):-1;	//获取集合最新的一个值，为空时值为-1
			if(lastNum == -1){	//集合是空的
				mEars.add(num);
				LOG("集合值为空：" + mEars.size());
			}
			else if(Math.abs(num - lastNum) <= 30){
				mEars.add(num);
				LOG("临近值有效：" + mEars.size());
			}else{
				mEars.clear();
				mEars.add(num);
				LOG("新增值：" + mEars.size());
			}
			if(mEars.size() >= 20) return true;
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
			num1 = Integer.parseInt(Integer.toString((0xff & bytes[0]) + (0xff & bytes[1]) + (0xff & bytes[2]) + (0xff & bytes[3]), 10));
			num2 = Integer.parseInt(Integer.toString((0xff & bytes[4]), 10));
			LOG("校验和" + num1 + "::" + num2  + ";;bytes:" + (0xff & bytes[0]) + ","+ (0xff & bytes[1]) + ","+ (0xff & bytes[2]) + ","+ (0xff & bytes[3]) + ","+ (0xff & bytes[4]) + ",");
			if((0xff & bytes[0]) + (0xff & bytes[1]) + (0xff & bytes[2]) + (0xff & bytes[3]) == (0xff & bytes[4])) {
//				if(num1 == num2)
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
		Log.i("spoort_list", "数据运算工具类：" + data + "");
	}


//	// 获取步数
//	public static Integer getNumber(byte[] bytes) {
//		int h = Integer.parseInt((Integer.toString(bytes[2] << 8, 10)));
//		int l = Integer.parseInt(Integer.toString(0XFF & bytes[3], 10));
//		int num = h + l;
//		return num;
//	}

	// 获得卡路里数
	public static Integer getCalorie(float height, float weight, int brancelet,
			int bupin) {
		// float surfaceArea = (float) (0.0061*height + 0.0128*weight-0.1529);
		// int calorie = (int) (0.05*brancelet + (2213.09 *
		// surfaceArea)-1993.57);
		// int calorie = (int) (0.43*height + 0.57*weight + 0.26*brancelet*6 +
		// 0.92*(brancelet/26)*6 - 108.44);
		int calorie = (int) (0.43 * height + 0.57 * weight + 0.26 * bupin * 6
				+ 0.92 * (brancelet / bupin) * 6 - 108.44);
		return calorie;
	}

}