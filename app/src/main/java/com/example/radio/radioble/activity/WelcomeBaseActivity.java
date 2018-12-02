package com.example.radio.radioble.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;

import com.example.radio.radioble.R;
import com.example.radio.radioble.activity.scan.*;
import com.example.radio.radioble.base.BaseWelcome;
import com.example.radio.radioble.context.ApplicationData;
import com.example.radio.radioble.context.PreferenceEntity;
import com.example.radio.radioble.utils.PreferencesUtils;
import com.example.radio.radioble.utils.ToastUtils;
import com.lidroid.xutils.http.RequestParams;

public class WelcomeBaseActivity extends BaseWelcome {
    public boolean setscr = false;

    public ImageView iv_welcome;


    public WelcomeBaseActivity(int layoutId) {
        super(layoutId);
    }

    @Override
    protected void initContent() {
        iv_welcome = findViewByIds(R.id.iv_welcome);
    }

    @Override
    protected void initLocation() {

    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent_home = null;
            switch (msg.what) {
                case 0:    //没有登录成功
                    intent_home = new Intent(mContext, com.example.radio.radioble.activity.scan.DeViceScanActivity.class);
                    break;
                case 1:    //设备不支持蓝牙
                    LOG("设备不支持蓝牙！");
                    ToastUtils.showToast("设备不支持蓝牙！");
                    break;
                case 2:    //跳转到引导页
                    break;
                default:
                    break;
            }
            if(intent_home != null){
                startActivity(intent_home);
                finish();
            }
        }
    };


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (setscr) {
            return;
        }
        setscr = true;

        Mview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);  //隐藏虚拟键盘，自适应

        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        Resources resources = WelcomeBaseActivity.this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        int top = rect.top; // 状态栏的高度
        View view = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        int top2 = view.getTop(); // 状态栏与标题栏的高度
        int width = view.getWidth(); // 视图的宽度
        int height = view.getHeight(); // 视图的高度
        int navigationBarHeight = resources.getDimensionPixelSize(resourceId);    //虚拟按键的高度


        PreferenceEntity.ScreenTop = top2;
        PreferenceEntity.ScreenTitle_navigationBarHeight = navigationBarHeight;
        PreferenceEntity.screenWidth = width;
        //判断是否显示虚拟返回键： true没有显示，false显示了
        LOG("虚拟键盘：" + ((ViewConfiguration.get(ApplicationData.context).hasPermanentMenuKey()) ? "没显示" : "显示了") + "");

        PreferenceEntity.screenHeight = height + top2;
        PreferenceEntity.ScreenTitle = ((float) (top2 + 100) / (float) PreferenceEntity.screenHeight);
        PreferenceEntity.ScreenTitle_title = ((float) (100) / (float) PreferenceEntity.screenHeight);

        boolean hasNavigationBar = false;
        Resources rs = this.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
            LOG(" hasNavigationBar =  " + hasNavigationBar);
        }
        LOG(" hasNavigationBar =  " + hasNavigationBar);

        PreferenceEntity.hasNavigationBar = hasNavigationBar;

        LOG("状态栏的高度：" + top + ",标题栏与状态栏的高度:" + top2 + ",标题栏与状态栏的高度占比:"
                + PreferencesUtils.getFloat(this, "ScreenTitle")
                + ",视图的宽度:" + width + ",视图的高度:" + height + ",屏幕的宽度:"
                + PreferenceEntity.screenWidth + ",屏幕的高度:"
                + PreferenceEntity.screenHeight + "虚拟键盘的高度：" + navigationBarHeight + "手机厂商：" + Build.MANUFACTURER);
    }

    @Override
    protected void initHead() {

    }

    @Override
    protected void initLogic() {

    }

    @Override
    protected void pauseClose() {

    }

    @Override
    protected void destroyClose() {

    }
}
