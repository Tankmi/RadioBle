package com.example.radio.radioble.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.radio.radioble.R;
import com.example.radio.radioble.context.ApplicationData;
import com.example.radio.radioble.context.PreferenceEntity;
import com.example.radio.radioble.utils.LayoutUtil;
import com.example.radio.radioble.utils.TransitionTime;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseActivity extends Activity {

	protected View Mview; // 当前界面的根
	private int MlayoutId; // 当前界面对应的布局
	public Context mContext;	//上下文
	/** 获取当前类名 */
	public String TAG;
	
	/********************* 头部分控件 *************************/
	public RelativeLayout mTitleView;
	public Button mBtnLeft; // 标题左边按钮
	public TextView tv_title_view_left_text; // 返回按钮提示文字（聊天也用于圈子人数展示）
	public TextView mTvTitle; // 标题
	public Button mBtnRight; // 右边按钮
	/*****************************************************/
	
	//***************图片操作
	public ImageLoader imageLoader_base;
	//***************图片操作
	
	/** 时间展示格式转换工具 */
	public TransitionTime tranTimes;
	
	/** 网络请求 */
	public static HttpUtils httpUtils;
	/** 网络请求 回调 */
//	public GetNetData mgetNetData;
	
	/**
	 *	软键盘的处理
	 * 	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);	//显示软键盘
	 *	imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘  
	 */
	public InputMethodManager imm;	
	
	//**************登录弹窗
	public AlertDialog.Builder login_dialog;
	
	// 布局
	public int screenWidth;
	public int screenHeight;
	protected LayoutUtil mLayoutUtil;
	

	public BaseActivity(int layoutId) {
		super();
		this.MlayoutId = layoutId;
		mContext = ApplicationData.context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Mview = View.inflate(this,MlayoutId, null);
		setContentView(Mview);
		ApplicationData.getInstance().addActivity(this);
		/** 设置竖屏加载 */
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ViewUtils.inject(this);	//初始化注解工具！//记住使用注解必须要在setContentView之后初始化！
		init(); // 初始化头中的各个控件,以及公共控件ImageLoader
		initHead(); // 初始化设置当前界面要显示的头状态，以及视图内容
		initContent(); // 初始化当前界面的主要内容
		initLocation(); // 初始化空间位置
		initLogic(); // 初始化逻辑
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		pauseClose();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setContentView(R.layout.view_null);
		destroyClose();
	}

	/**
	 * 初始化头中的各个控件,以及公共控件ImageLoader
	 * 
	 */
	protected void init() {
		//初始化软键盘操作
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//初始化布局参数
		screenWidth = PreferenceEntity.screenWidth;
		screenHeight = PreferenceEntity.screenHeight;
		mLayoutUtil = LayoutUtil.getInstance();
				
//		mgetNetData = new GetNetData();
		tranTimes = new TransitionTime(System.currentTimeMillis());
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法
		
//		setStatusBarColor(getResources().getColor(R.color.back_color_title));
		mTitleView = (RelativeLayout) Mview.findViewById(R.id.title);
		mBtnLeft = findViewByIds(R.id.btn_title_view_left);
		tv_title_view_left_text = findViewByIds(R.id.tv_title_view_left_text);
		mTvTitle = findViewByIds(R.id.tv_title_view_title);
		mBtnRight = findViewByIds(R.id.btn_title_view_right);

		if (mTitleView!=null) {
			mLayoutUtil.drawViewRBLayout(mTitleView, 0, 90, 0, 0, 0, 0);
		}
		if (mBtnLeft!=null) {
			mLayoutUtil.drawViewRBLayout(mBtnLeft, 44, 44, 10, 0, 0, 0);
		}

		if (mBtnRight!=null) {
			mLayoutUtil.drawViewRBLayout(mBtnRight, 0, 0, 0, 18,0,0);
		}
		if (tv_title_view_left_text!=null) {
			mLayoutUtil.drawViewRBLayout(tv_title_view_left_text, 0, 0, 18, 0.0f,0,0);
		}
		mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),  0);
				BaseActivity.this.onBackPressed();
			}
		});
		//初始化ImageLoader
		imageLoader_base = ImageLoader.getInstance();
	}

	/**
	 * 初始化当前界面的主要内容,即除了头部以外的其它部分
	 */
	protected abstract void initContent();

	/**
	 * 初始化控件位置
	 */
	protected abstract void initLocation();

	/* 初始化信息*/
	protected abstract void initHead();

	/**
	 * 初始化逻辑
	 */
	protected abstract void initLogic();

	/**
	 * pause关闭方法
	 */
	protected abstract void pauseClose();

	/**
	 * destroy关闭方法
	 */
	protected abstract void destroyClose();

	/**
	 * 设置当前界面所对应头的标题
	 * 
	 * @param title
	 */
	protected void setTittle(String title) {
		mTvTitle.setText(title);
	}

	/**
	 * 隐藏左边返回键
	 */
	public void setLeftButtonVisibleGone() {
		mBtnLeft.setVisibility(View.GONE);
	}
	
	/**
	 * 显示右边按钮
	 */
	public void setRightButtonVisible() {
		mBtnRight.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置右边的字体内容
	 */
	public void setRightText(String text) {
		mBtnRight.setText(text);
	}

	/**
	 * 设置右边的字体颜色
	 */
	public void setRightTextColor(int color) {
		mBtnRight.setTextColor(color);
	}

	/**
	 * 避免每次都进行强转
	 * 
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T findViewByIds(int viewId) {
		return (T) Mview.findViewById(viewId);
	}
	
	/**
	 * 打印日志
	 * @param data	需要打印的内容
	 */
	public void LOG(String data){
		Log.i("spoort_list", data + "");
	}
	
}
