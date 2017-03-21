package com.yunpai.tms.application;

import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;
import com.yunpai.tms.constant.Constant;
import com.yunpai.tms.util.image.ImageLoaderConfig;


/**
 * 作者：Create on 2017/1/10 16:00  by  gan
 * 邮箱：
 * 描述：当前Application，用来初始化数据
 * 最近修改：2017/1/10 16:00 modify by gan
 */

public class MyApplication extends MultiDexApplication{
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        if(!Constant.DEBUG)//如果不是在调试，将启用异常扑捉器
        CrashHandler.getInstance().init(getApplicationContext());//初始化全局异常扑捉器
        ImageLoaderConfig.initImageLoader(this, (Constant.BASE_IMAGE_CACHE));
        this.instance = this;
        LeakCanary.install(this);
    }
    public static MyApplication getInstance() {
        return instance;
    }
}
