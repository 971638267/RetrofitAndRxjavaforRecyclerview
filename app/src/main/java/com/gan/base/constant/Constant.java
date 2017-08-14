package com.gan.base.constant;


import android.os.Environment;

public class Constant {
    //是否允许弹出toast
    public static final boolean TOAST = true;
    public static  boolean UPLOAD_VERSION =false ;
    //调试模式
    public static boolean DEBUG = true;//调试模式开关
    // 缓存图片路径(imageLoder用)
    public static final String BASE_PATH = "gan/";

    public static final String BASE_IMAGE_CACHE = BASE_PATH + "cache/images/";
    public static final String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath() + BASE_PATH;
    public static final String DOWNLOAD_APK = SD_CARD + "download/apk/";
}
