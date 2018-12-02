package com.example.radio.radioble.utils;

import java.text.DecimalFormat;

/**
 * 单位转换
 * @author ZhuTao
 * @date 2017/5/12
*/

public class UnitConversion {

	private static UnitConversion mUnitConversion;

	public static UnitConversion getInstance(){
		synchronized(LayoutUtil.class){
			if(mUnitConversion==null){
				mUnitConversion=new UnitConversion();
			}
		}
		return mUnitConversion;
	}

	/** 满万转万位
	 *  @param  defaultData 需要转换的对象
	 *  @param  places 小数点后保留的位数
	 * */
	public String turnOver(float defaultData,int places){
		if(defaultData>10000){
			defaultData = defaultData/10000;
			DecimalFormat df = new DecimalFormat("#." + places);
			return df.format (defaultData) + "万";
		}else{
			return defaultData + "";
		}
	}

}
