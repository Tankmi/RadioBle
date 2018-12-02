package com.example.radio.radioble.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
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
import com.example.radio.radioble.utils.TransitionTime;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;

import java.util.List;

public abstract class BaseWelcome extends FragmentActivity {


	protected View Mview; // 当前界面的根
	private int MlayoutId; // 当前界面对应的布局
	public static Context mContext;	//上下文
	/** 获取当前类名 */
	public String TAG;

	/********************* 头部分控件 *************************/
	public RelativeLayout mTitleView;
	public Button mBtnLeft; // 标题左边按钮
	public TextView mTvTitle; // 标题
	public Button mBtnRight; // 右边按钮

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

	public BaseWelcome(int layoutId) {
		super();
		this.MlayoutId = layoutId;
		mContext = ApplicationData.context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Mview = View.inflate(this,MlayoutId, null);
		if (savedInstanceState != null) {
			LOG("非正常退出：" + savedInstanceState.getString("home_datas"));
		}

//		view = LayoutInflater.from(this).inflate(layoutId, null);
//		Mview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);  //隐藏虚拟键盘，自适应
		setContentView(Mview);
		ApplicationData.getInstance().addActivity(this);
		Log.i("打开的页面", this.getClass().getName());
		/** 设置竖屏加载 */
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ViewUtils.inject(this,Mview);//初始化注解必要要加上！
		init(); // 初始化头中的各个控件,以及公共控件ImageLoader
		initTitle();//初始化标题栏的控件
		initHead(); // 初始化设置当前界面要显示的头状态
		initContent(); // 初始化当前界面的主要内容
		initLocation(); // 初始化空间位置
		initLogic(); // 初始化逻辑
	}

	/**
	 * 初始化标题栏的控件
	 */
	public void initTitle(){
		setStatusBarColor(getResources().getColor(R.color.back_color_title));
		mTitleView = (RelativeLayout) Mview.findViewById(R.id.title);
		mBtnLeft = (Button) Mview.findViewById(R.id.btn_title_view_left);
		mTvTitle = (TextView) Mview.findViewById(R.id.tv_title_view_title);
		mBtnRight = (Button) Mview.findViewById(R.id.btn_title_view_right);

	}
	/**
	 * 初始化头中的各个控件,以及公共控件ImageLoader
	 *
	 */
	protected void init() {

//		mgetNetData = new GetNetData();
		tranTimes = new TransitionTime(System.currentTimeMillis());
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法
	}


	/**
	 * 判断是否登录
	 * @return 登录返回true，否则返回false
	 */
	public boolean isLogin(){
		return PreferenceEntity.isLogin;
	}

	/**
	 * 初始化当前界面的主要内容,即除了头部以外的其它部分
	 */
	protected abstract void initContent();

	/**
	 * 初始化控件位置
	 */
	protected abstract void initLocation();


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
	protected void setTittle(String title,int color) {
		mTvTitle.setText(title);
		mTvTitle.setTextColor(color);
	}
	/**
	 * 设置当前界面所对应头的标题
	 *
	 * @param title
	 */
	protected void setTittle(String title) {
		mTvTitle.setText(title);
	}

	protected void setRightButtonText(String text,int color){
		mBtnRight.setText(text);
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

	public <T> T findViewByIds(View view,int viewId) {
		return (T) view.findViewById(viewId);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		FragmentManager fm = getSupportFragmentManager();
		int index = requestCode >> 16;
		if (index != 0) {
			index--;
			if (fm.getFragments() == null || index < 0
					|| index >= fm.getFragments().size()) {
				Log.w(TAG, "Activity result fragment index out of range: 0x"
						+ Integer.toHexString(requestCode));
				return;
			}
			Fragment frag = fm.getFragments().get(index);
			if (frag == null) {
				Log.w(TAG, "Activity result no fragment exists for index: 0x"
						+ Integer.toHexString(requestCode));
			} else {
				handleResult(frag, requestCode, resultCode, data);
			}
			return;
		}

	}

	/**
	 * 递归调用，对所有子Fragement生效
	 *
	 * @param frag
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	private void handleResult(Fragment frag, int requestCode, int resultCode,
							  Intent data) {
		frag.onActivityResult(requestCode & 0xffff, resultCode, data);
		List<Fragment> frags = frag.getChildFragmentManager().getFragments();
		if (frags != null) {
			for (Fragment f : frags) {
				if (f != null)
					handleResult(f, requestCode, resultCode, data);
			}
		}
	}


	/**
	 * 打印日志
	 * @param data	需要打印的内容
	 */
	public void LOG(String data){
		Log.i("spoort_list", data + "");
	}

	/**
	 * 设置状态栏背景色
	 * @param color
	 */
	public void setStatusBarColor(int color){
		View view_title = findViewByIds(R.id.title);
		view_title.setBackgroundColor(color);
		//设置状态栏透明，这个方法只有在安卓4.4以上才能起作用！
		if (android.os.Build.VERSION.SDK_INT > 18) {
//        	LOG("首页设置状态栏透明");
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			// 创建TextView
			TextView textView = new TextView(this);
			LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, (int) PreferenceEntity.ScreenTop);
			textView.setBackgroundColor(color);
//            textView.setBackgroundColor(Color.parseColor("#f03069"));
			textView.setLayoutParams(lParams);
			// 获得根视图并把TextView加进去。
			ViewGroup view = (ViewGroup) getWindow().getDecorView();
			view.addView(textView);
		}
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

}
