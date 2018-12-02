package com.example.radio.radioble.activity;


import android.content.pm.PackageManager;
import android.view.View;

import com.example.radio.radioble.R;
import com.google.gson.Gson;

/**
 * 欢迎页
 * @author ZhuTao
 * @date 2017/3/7
 * @params id:值
*/

public class WelcomeActivity extends WelcomeBaseActivity {

    public WelcomeActivity() {
        super(R.layout.activity_welcome);
    }

    @Override
    protected void initHead() {
        Mview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);   //隐藏虚拟键盘，自适应
    }

    @Override
    protected void initLogic() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            LOG("设备不支持蓝牙");
            mHandler.sendEmptyMessageDelayed(1, 200);
        }else{
            mHandler.sendEmptyMessageDelayed(0, 200);
        }

    }


    @Override
    protected void pauseClose() {
        super.pauseClose();
    }

    @Override
    protected void destroyClose() {
        super.destroyClose();
    }
}
