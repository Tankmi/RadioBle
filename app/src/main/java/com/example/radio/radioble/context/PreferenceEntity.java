package com.example.radio.radioble.context;

public class PreferenceEntity {
    /**
     * 本地缓存地址 mnt/sdcard/huidf_doc
     */
    public static final String KEY_CACHE_PATH = "/huitx_yuedong";
    // 清除文件夹里面的文件！
    // boolean file = DelectFileUties.delAllFile("mnt/sdcard/menmen/");
    // if (file) {
    // ToastUtils.show(ApplicationData.context, "清除缓存成功！");
    // }else {
    // ToastUtils.show(ApplicationData.context, "清除缓存失败");
    // }

    //**********************************chat
    /** 是否登录聊天服务器 */
    public static boolean isLoginChat=false;
    /** 加载表情 */
    public static boolean isFace = true;
    //**********************************chat

    /**
     * 照相，照片保存的地址！
     */
    public static final String KEY_CHAT_PHONE_PATH = "chat_phone_path";//
    /**
     * 图片选择器中，图片的地址
     */
    public static final String KEY_CHAT_PHOTOS_PATH = "chat_photos_path";//

    /**
     * 是否登录
     */
    public static boolean isLogin = false;
    /**
     * 标记是不是第一次打开APP
     */
    public static String KEY_IS_FIRST_OPEN = "";

    //屏幕信息
    public static int screenWidth;
    public static int screenHeight;
    /**
     * 状态栏的高度
     */
    public static float ScreenTop;
    /**
     * 标题栏与状态栏的高度占比
     */
    public static float ScreenTitle;
    /**
     * 标题栏的高度占比
     */
    public static float ScreenTitle_title;
    /**
     * 虚拟键盘的高度
     */
    public static float ScreenTitle_navigationBarHeight;

    /** 是否显示虚拟键*/
    public static boolean hasNavigationBar = false;
    //屏幕信息

}
