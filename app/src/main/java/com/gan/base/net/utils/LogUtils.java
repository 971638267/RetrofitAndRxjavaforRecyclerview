package com.gan.base.net.utils;

import android.util.Log;

import com.gan.base.constant.Constant;


/**
 * 用途: 日志管理类
 * 创建者:ganyufei
 * 时间: 2017/2/23
 */

public class LogUtils {
    public static void D(String tag,String msg){
        if (Constant.DEBUG){
            Log.d(tag,msg);
        }
    }
    public static void D(String msg){
        if (Constant.DEBUG){
            Log.d("gan-yunpai-tms====>",msg);
        }
    }
}
