package com.example.radio.radioble.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

/**
 * @author 作者 E-mail: ZT 
 * @version 创建时间：2016年9月19日 上午10:42:41 
 * 控件管理类 
 */
public class WidgetSetting {

	/**
	 * 设置控件过渡颜色
	 * @param view	文本控件
	 * @param color	特殊字体颜色
	 * @param text	文本内容
	 * @param start	特殊字体设置起始位置
	 */
	public static void setTvColor(TextView view,int color,String text,int start){
		
		view.setText("");
		SpannableString spanText = new SpannableString(text);
		spanText.setSpan(new ForegroundColorSpan(color), start, spanText.length(),
		Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		view.append(spanText);
	}
	
	/**
	 * 为文本框设置内容，过滤null字段
	 * @param view 视图对象
	 * @param text 文本
	 */
	public static void setViewText(TextView view,String text){
		view.setText(text == null?"":text.equals("")?"":text);
	}
	
	/**
	 * 为文本框设置内容，过滤null字段
	 * @param view 视图对象
	 * @param text 文本
	 * @param normal 文本对象为空时的默认值
	 */
	public static void setViewText(TextView view,String text,String normal){
		view.setText(text == null?"" + normal:text.equals("")?"" + normal:text.equals("null")?"" + normal:text);
	}
	
	/**
	 * 为文本框设置内容，过滤null字段
	 * @param view 视图对象
	 * @param text1 默认文本
	 * @param text 文本
	 * @param def 文本的默认值（不是默认文本）
	 * @param state true，默认文本在前
	 * 
	 */
	public static void setViewText(TextView view,String text1,String text,String def,boolean state){
		if(state){
			view.setText(text1 + (text == null?def:text.equals("")?def:text));
		}else{
			view.setText((text == null?def:text.equals("")?def:text) + text1);
		}
	
	}
	
	/**
	 * 判断文本框内容是否为空
	 * @param view
	 * @param hint 为空时的提示语句
	 * @return 为空返回false
	 */
	public static boolean notNull(TextView view,String hint){
		
		if(view.getText().toString().trim().length() <=0){
			ToastUtils.showToast(hint);
			return false;
		}
		
		return true;
		
	}

	/**
	 * 设置控件过渡大小
	 * @param view	文本控件
	 * @param color	特殊字体大小
	 * @param text	文本内容
	 * @param start	特殊字体设置起始位置
	 */
	public static void setTvSizeAndColor(TextView view,float size,String text,int start){
		
		view.setText("");
		SpannableString spanText = new SpannableString(text);
		spanText.setSpan(new RelativeSizeSpan(size), start, spanText.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		view.append(spanText);
	}
}
