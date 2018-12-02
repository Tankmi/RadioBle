package com.example.radio.radioble.context;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.example.radio.radioble.utils.CrashHandler;
import com.example.radio.radioble.utils.files.FileUtils;
import com.lidroid.xutils.DbUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ApplicationData extends Application {

    public static Context context;
    /**
     * imei号
     */
    public static String imei;

    //初始化数据库！
    private static DbUtils dbUtils;

    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象  
    private static ApplicationData ApplicationDatainstance;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());//初始化抓取异常的工具类！

        dbUtils = DbUtils.create(getApplicationContext(), "database");

        context = getApplicationContext();
        initImageLoader(context);
        getDatas(this);
        getInstance();

//		initGotye();
    }

    public void initGotye() {
    }

    public static DbUtils getDbuties() {
        return dbUtils;// 获取到创建数据库的对象！
    }

    /**
     * 获取imei号
     *
     * @param context
     */
    public static void getDatas(Context context) {
        // 获取IMEI号
        TelephonyManager tele = (TelephonyManager) context
                .getSystemService(TELEPHONY_SERVICE);
        imei = tele.getDeviceId();
    }

    /**
     * 初始化ImageLoader
     *
     * @param context
     */
    public static void initImageLoader(Context context) {

        FileUtils.makeDirs(PreferenceEntity.KEY_CACHE_PATH);//创建缓存目录

        @SuppressWarnings("deprecation")
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .discCacheSize(50 * 1024 * 1024)//
                .discCacheFileCount(100)// 缓存一百张图片
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3).memoryCacheSize(getMemoryCacheSize(context))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new WeakMemoryCache())//这个类缓存bitmap的总大小没有限制，唯一不足的地方就是不稳定，缓存的图片容易被回收掉
                //缓存到sd卡把图片！
                .discCache(new UnlimitedDiscCache(new File(PreferenceEntity.KEY_CACHE_PATH)))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs()//
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     */
    private static int getMemoryCacheSize(Context context) {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory
            // limit
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }
        return memoryCacheSize;
    }

    //实例化一次  
    public synchronized static ApplicationData getInstance() {
        if (null == ApplicationDatainstance) {
            ApplicationDatainstance = new ApplicationData();
        }
        return ApplicationDatainstance;
    }

    // add Activity    
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    //关闭每一个list内的activity  
    public void exit() {
        if (mList.size() == 0) {
            return;
        }
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            System.exit(0);   
        }
    }

    //杀进程  
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
