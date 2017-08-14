package com.gan.base.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gan.base.net.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：Create on 2017/1/19 13:37  by  gan
 * 邮箱：
 * 描述：基本的工具类
 * 最近修改：2017/1/19 13:37 modify by gan
 */

public class Utils {
    // 记录屏幕的高度、宽度、密度等信息。
    public static int screenH;
    public static int screenW;
    public static float screenDensity; // 屏幕密度（0.75 / 1.0 / 1.5）
    public static int screenDensityDpi; // 屏幕密度DPI（120 / 160 / 240）
    public static int statusBarHeight; // 状态栏高度

    // 获取屏幕的高度和宽度
    public static int getScreenW(Activity mActivity) {
        if (screenW == 0) {
            DisplayMetrics metric = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            screenW = metric.widthPixels; // 屏幕宽度（像素）
            screenH = metric.heightPixels; // 屏幕高度（像素）
            screenDensity = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
            screenDensityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 /
        }
        return screenW;
    }

    public static int getScreenH(Activity mActivity) {
        if (screenH == 0) {
            DisplayMetrics metric = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            screenW = metric.widthPixels; // 屏幕宽度（像素）
            screenH = metric.heightPixels; // 屏幕高度（像素）
            screenDensity = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
            screenDensityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 /
        }
        return screenH;
    }

    @SuppressLint("SimpleDateFormat")
    public static String longtimeToDate(long time) {
        Date now = new Date(time *1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
        String dateStr = dateFormat.format(now);
        return dateStr;
    }

    @SuppressLint("SimpleDateFormat")
    public static String longtimeToDayDate(long time) {
        Date now = new Date(time *1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期格式
        String dateStr = dateFormat.format(now);
        return dateStr;
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static long stringDateToLong(String dateStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    /**
     * 升级检测
     *
     * @param locVersionName
     * @param lastVersion
     * @return 是否升级
     */
    public static boolean checkUpdate(String locVersionName, String lastVersion) {
        boolean hasUpdate = false;
        String[] locVersionS = locVersionName.split("\\.");
        String[] lastVersionS = lastVersion.split("\\.");

        if (!locVersionName.equals(lastVersion)) {
            if (locVersionS != null && lastVersion != null) {
                int localLenth = locVersionS.length;
                int lastVerLenth = lastVersionS.length;

                // int netLenth = lastVersion.length();
                for (int i = 0; i < lastVerLenth; i++) {
                    if (localLenth < lastVerLenth && i == localLenth) {
                        hasUpdate = true;
                        return hasUpdate;
                    }

                    if (Integer.valueOf(lastVersionS[i]) > Integer
                            .valueOf(locVersionS[i])) {
                        hasUpdate = true;
                        return hasUpdate;
                    } else if (Integer.valueOf(lastVersionS[i]) < Integer
                            .valueOf(locVersionS[i])) {
                        hasUpdate = false;
                        return hasUpdate;
                    }
                }
            }
        } else {
            hasUpdate = false;
        }
        return hasUpdate;
    }

    /**
     * bitmap转byte数组
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp,
                                        final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 实现文本复制功能 add by lif
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能 add by lif
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInputMethod(Activity act) {
        View view = act.getWindow().peekDecorView();
        if (view != null) {
            // 隐藏虚拟键盘
            InputMethodManager inputmanger = (InputMethodManager) act
                    .getSystemService(act.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 切换软件盘 显示隐藏
     */
    public static void switchSoftInputMethod(Activity act) {
        // 方法一(如果输入法在窗口上已经显示，则隐藏，反之则显示)
        InputMethodManager imm = (InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 验证是否手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$");
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    /**
     * 中文识别
     *
     */
    public static boolean hasChinese(String source) {
        String reg_charset = "([\\u4E00-\\u9FA5]*+)";
        Pattern p = Pattern.compile(reg_charset);
        Matcher m = p.matcher(source);
        boolean hasChinese = false;
        while (m.find()) {
            if (!"".equals(m.group(1))) {
                hasChinese = true;
            }
        }
        return hasChinese;
    }

    /**
     * 用户名规则判断
     *
     * @param uname
     * @return
     */
    public static boolean isAccountStandard(String uname) {
        Pattern p = Pattern.compile("[A-Za-z0-9_]+");
        Matcher m = p.matcher(uname);
        return m.matches();
    }

    // java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * map转对象
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null)
            return null;

        Object obj = beanClass.newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                continue;
            }

            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }

        return obj;
    }

    /**
     * 对象转map
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj)  {
        Map<String, Object> map=new HashMap<String, Object>();
        try {
            if(obj == null){
                return null;
            }
             map = new HashMap<String, Object>();
            for (Class<?> clazz = obj.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()){
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    if (field.get(obj)!=null)
                        map.put(field.getName(), field.get(obj));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        LogUtils.D(map.toString());
        return map;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean fileIsExists(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 防止重复点击（长点击）
     *
     * @return 是否重复点击了 true:代表重复点击
     */
    private static  long lastClickTime;
    public static boolean isFastClickLongTime() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 4000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 获取版本号
     *
     * @param type =true 返回版本名称 type=false 返回版本code
     * @return
     */
    public static String getVersion(Context context, boolean type) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            int versionCode = info.versionCode;
            if (type)
                return versionName;
            else
                return versionCode + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "100.0.0";
        }
    }

}
