package com.gan.base.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.gan.base.application.MyApplication;


/**
 * 作者：Create on 2017/1/19 13:34  by  gan
 * 邮箱：
 * 描述：shareprefrence的类工具
 * 最近修改：2017/1/19 13:34 modify by gan
 */

public class PrefUtils {
    public static boolean getBoolean( String key, boolean defalt){
        SharedPreferences sp= MyApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        Boolean b=sp.getBoolean(key, defalt);
        return b;
    }

    public  static void  setBoolean(String key,Boolean value) {
        SharedPreferences sp= MyApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static String getString(String key,String defalt){
        SharedPreferences sp=MyApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        String s=sp.getString(key, defalt);
        return s;
    }

    public static void SetString(String key,String value){
        SharedPreferences sp=MyApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();

    }

    public static int getInt(String key,int defalt){
        SharedPreferences sp=MyApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        int l=sp.getInt(key, defalt);
        return l;
    }

    public static void SetInt(String key,int value){
        SharedPreferences sp=MyApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();

    }
    public static long getLong(String key,long defalt){
        SharedPreferences sp=MyApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        long l=sp.getLong(key, defalt);
        return l;
    }

    public static void SetLong(String key,long value){
        SharedPreferences sp=MyApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        sp.edit().putLong(key, value).commit();

    }
}
