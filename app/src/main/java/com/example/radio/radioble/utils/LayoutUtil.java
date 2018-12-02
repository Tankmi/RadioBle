package com.example.radio.radioble.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout.LayoutParams;

import com.example.radio.radioble.context.ApplicationData;
import com.example.radio.radioble.context.PreferenceEntity;

/**
 *  用于进行主页（没有滑动效果）以及其他页面的控件适配 ，传入的参数包括控件的宽高，
 * 距离父控件上下左右的距离（这几个参数不一定都有，根据使用的方法来定）。大部分视图的父类都是view，所以drawviewLayout基本上都适用；
 * 只有单选框一类的需要单独调用对应的方法
 * @author ZhuTao
 * @date 2017/3/13
 * @params
*/

public class LayoutUtil {
	
	/** 切图宽高比例值 */
	private float ratio;
	/** 设备宽高比 */
	private float mRatio;	
	private float ScreenWidth;	//屏幕的宽
	private float ScreenHeight;	//屏幕的高
	/** 虚拟键盘的高 */
	private float navigationBarHeight = PreferenceEntity.ScreenTitle_navigationBarHeight;	//虚拟键盘的高
	/** false 显示了，true 没显示 */
	private boolean hasNavigationBar = false;
	/** 切图宽 */
	private float normalW;
	/** 切图高 */
	private float normalH;
	private static LayoutUtil mLayoutUtilInstance;

	public static LayoutUtil getInstance(){
		synchronized(LayoutUtil.class){
			if(mLayoutUtilInstance==null){
				mLayoutUtilInstance=new LayoutUtil();
			}
		}
		return mLayoutUtilInstance;
	}

	public LayoutUtil() {
		normalW = 750;
		normalH = 1334;
		ScreenWidth = PreferenceEntity.screenWidth;	//屏幕的宽
		ScreenHeight = PreferenceEntity.screenHeight;	//屏幕的高
		
//		Log.i("spoort_list","ScreenWidth" + ScreenWidth + ",ScreenHeight:" + ScreenHeight + "navigationBarHeight" +navigationBarHeight);
		//判断是否显示虚拟返回键： true没有显示，false显示了
		if(!PreferenceEntity.hasNavigationBar || Build.MANUFACTURER.equals("Meizu")){
			hasNavigationBar = true;
		}else{
			if(ViewConfiguration.get(ApplicationData.context).hasPermanentMenuKey()){
				hasNavigationBar = true;
			}else{
				hasNavigationBar = false;
			}
		}
		
		if(!hasNavigationBar){	//计算出实际绘制时的屏幕高度
			ScreenHeight = ScreenHeight - navigationBarHeight;
		}
		
		ratio = 0.5622f;//这个是按750/1334算出来的比例值！要使用ui图的宽高！ 0.5622
		mRatio = ScreenWidth/ScreenHeight;
		if(mRatio != ratio){	// 默认是全屏布局，没有拉伸布局
			ScreenWidth = ScreenHeight * ratio;
		}
//		Log.i("spoort_list",hasNavigationBar + "mLayoutUtil ScreenWidth" + ScreenWidth + ",ScreenHeight:" + ScreenHeight + "mRatio" +mRatio);
	}
	/** 设置是否全屏。
	 * 默认是全屏布局，没有拉伸布局。
	 * 建议初始化界面的时候调用一次   */
	public void setIsFullScreen(boolean fullScreen){
		if(fullScreen){
			ScreenHeight = PreferenceEntity.screenHeight;	//屏幕的高
			if(!hasNavigationBar){	//计算出实际绘制时的屏幕高度
				ScreenHeight = ScreenHeight - navigationBarHeight;
			}
			if(mRatio != ratio){
				ScreenWidth = ScreenHeight * ratio;
			}
		}else{
			ScreenWidth = PreferenceEntity.screenWidth;
			if(mRatio != ratio){
				ScreenHeight = ScreenWidth / ratio;
			}
		}
	}

	public float getScreenHeight(){

		return ScreenHeight;
	}
	
	/**
	 * 绘制单选框父布局
	 */
	public void drawRadiogroup(RadioGroup view,  float width, float height, float marginleft,float marginright, float marginTop,float marginBottom) {

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view
				.getLayoutParams();

			if (width <= 0.0f) {} else {
				params.width = (int) (ScreenWidth * (width / normalW));
			}

			if (height <= 0.0f) {} else {
				params.height = (int) (ScreenHeight *( height  / normalH) );
			}

			if (marginTop <= 0.0f) {} else {
				//判断是否显示虚拟返回键： true没有显示，false显示了
				if(hasNavigationBar){
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}else{
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}
			}

			if (marginBottom <= 0.0f) {

			} else {
				params.bottomMargin = (int) (ScreenHeight * ( marginBottom  / normalH));
			}

		if (marginleft <= 0.0f) {} else {
			params.leftMargin = (int) (ScreenWidth * ( marginleft  / normalW));
		}

		if (marginright <= 0.0f) {

		} else {
			params.rightMargin = (int) (ScreenWidth *  ( marginright  / normalW));
		}

		view.setLayoutParams(params);
	}
	
	/**
	 * 绘制单选框
	 */
	public void drawRadiobutton(RadioButton view, float width, float height, float marginleft,float marginright, float marginTop,float marginBottom) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view
				.getLayoutParams();

			if (width <= 0.0f) {} else {
				params.width = (int) (ScreenWidth * (width / normalW));
			}

			if (height <= 0.0f) {} else {
				params.height = (int) (ScreenHeight *( height  / normalH) );
			}

			if (marginTop <= 0.0f) {} else {
				//判断是否显示虚拟返回键： true没有显示，false显示了
				if(hasNavigationBar){
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}else{
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}
			}

			if (marginBottom <= 0.0f) {

			} else {
				params.bottomMargin = (int) (ScreenHeight * ( marginBottom  / normalH));
			}

		if (marginleft <= 0.0f) {} else {
			params.leftMargin = (int) (ScreenWidth * ( marginleft  / normalW));
		}

		if (marginright <= 0.0f) {

		} else {
			params.rightMargin = (int) (ScreenWidth *  ( marginright  / normalW));
		}

		view.setLayoutParams(params);
	}
	
	/**
	 * 绘制多选框 CheckBox
	 */
	public void drawCheckBox(CheckBox view, float width, float height, float marginleft,float marginright, float marginTop,float marginBottom) {
		LayoutParams params = (LayoutParams) view.getLayoutParams();

			if (width <= 0.0f) {} else {
				params.width = (int) (ScreenWidth * (width / normalW));
			}

			if (height <= 0.0f) {} else {
				params.height = (int) (ScreenHeight *( height  / normalH) );
			}

			if (marginTop <= 0.0f) {} else {
				//判断是否显示虚拟返回键： true没有显示，false显示了
				if(hasNavigationBar){
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}else{
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}
			}

			if (marginBottom <= 0.0f) {

			} else {
				params.bottomMargin = (int) (ScreenHeight * ( marginBottom  / normalH));
			}

		if (marginleft <= 0.0f) {} else {
			params.leftMargin = (int) (ScreenWidth * ( marginleft  / normalW));
		}

		if (marginright <= 0.0f) {

		} else {
			params.rightMargin = (int) (ScreenWidth *  ( marginright  / normalW));
		}

		view.setLayoutParams(params);
	}
	
	public void drawViewRBLayout(View view, float width, float height, float marginleft,float marginright, float marginTop,float marginBottom) {
		LayoutParams params = (LayoutParams) view.getLayoutParams();
		
			if (width <= 0.0f) {} else {
				params.width = (int) (ScreenWidth * (width / normalW));
			}

			if (height <= 0.0f) {} else {
				params.height = (int) (ScreenHeight *( height  / normalH) );
			}
			
			if (marginTop <= 0.0f) {} else {
				//判断是否显示虚拟返回键： true没有显示，false显示了
				if(hasNavigationBar){
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}else{
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}
			}
			
			if (marginBottom <= 0.0f) { } else {
				params.bottomMargin = (int) (ScreenHeight * ( marginBottom  / normalH));
			}

		if (marginleft <= 0.0f) {} else {
			params.leftMargin = (int) (ScreenWidth * ( marginleft  / normalW));
		}
		
		if (marginright <= 0.0f) {
			
		} else {
			params.rightMargin = (int) (ScreenWidth *  ( marginright  / normalW));
		}
	
		view.setLayoutParams(params);
	}
	
	public void drawViewRBLinearLayout(View view, float width, float height, float marginleft,float marginright,
			float marginTop,float marginBottom) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();

			if (width <= 0.0f) {} else {
				params.width = (int) (ScreenWidth * (width / normalW));
			}

			if (height <= 0.0f) {} else {
				params.height = (int) (ScreenHeight *( height  / normalH) );
			}

			if (marginTop <= 0.0f) {} else {
				//判断是否显示虚拟返回键： true没有显示，false显示了
				if(hasNavigationBar){
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}else{
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}
			}

			if (marginBottom <= 0.0f) {

			} else {
				params.bottomMargin = (int) (ScreenHeight * ( marginBottom  / normalH));
			}

		if (marginleft <= 0.0f) {} else {
			params.leftMargin = (int) (ScreenWidth * ( marginleft  / normalW));
		}

		if (marginright <= 0.0f) {

		} else {
			params.rightMargin = (int) (ScreenWidth *  ( marginright  / normalW));
		}

		view.setLayoutParams(params);
	}

	public void drawViewFLLayout(View view, float width, float height, float marginleft,float marginright,
			float marginTop,float marginBottom) {
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();

			if (width <= 0.0f) {} else {
				params.width = (int) (ScreenWidth * (width / normalW));
			}

			if (height <= 0.0f) {} else {
				params.height = (int) (ScreenHeight *( height  / normalH) );
			}

			if (marginTop <= 0.0f) {} else {
				//判断是否显示虚拟返回键： true没有显示，false显示了
				if(hasNavigationBar){
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}else{
					params.topMargin = (int) ((ScreenHeight) * ( marginTop  / normalH));
				}

			if (marginBottom <= 0.0f) {

			} else {
				params.bottomMargin = (int) (ScreenHeight * ( marginBottom  / normalH));
			}
		}

		if (marginleft <= 0.0f) {} else {
			params.leftMargin = (int) (ScreenWidth * ( marginleft  / normalW));
		}

		if (marginright <= 0.0f) {

		} else {
			params.rightMargin = (int) (ScreenWidth *  ( marginright  / normalW));
		}

		view.setLayoutParams(params);
	}
	
}
