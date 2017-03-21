package com.yunpai.tms.net.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


import com.yunpai.tms.application.MyApplication;
import com.yunpai.tms.constant.Constant;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

/**
 * 类描述：封装一个OkHttp3的获取类
 * Created by ganyufei on 2016/6/3.
 */
public class OkHttp3Utils {

    private static OkHttpClient mOkHttpClient;

    //设置缓存目录
    // private static File cacheDirectory = new File(MyApplication.getInstance().getApplicationContext().getCacheDir().getAbsolutePath(), "MyCache");
    private static File cacheDirectory = new File(Constant.BASE_PATH, "MyCache");

    private static Cache cache = new Cache(cacheDirectory, 10 * 1024 * 1024);


    /**
     * 获取OkHttpClient对象
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {

        if (null == mOkHttpClient) {

            //同样okhttp3后也使用build设计模式
            mOkHttpClient = new OkHttpClient.Builder()
                    //设置一个自动管理cookies的管理器
                    // .cookieJar(new CookiesManager())
                    //没网络时的拦截器
                    .addInterceptor(new MyIntercepter())
                    //设置请求读写的超时时间
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(cache)
                    .build();
        }
        return mOkHttpClient;
    }


    /**
     * 拦截器
     */
    private static class MyIntercepter implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request oldRequest = chain.request();
            //gan-----start----------------以下代码为添加一些公共参数使用--------------------------
            // 添加新的参数
            String time = System.currentTimeMillis() / 1000 + "";
            String mKey = "f6f712249f4b725fac309504d633f839";
            HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host());
            //.addQueryParameter("regionAId", "")
            //.addQueryParameter("regionZId", "");
            //.addQueryParameter("os", "android")
            //.addQueryParameter("time", URLEncoder.encode(time, "UTF-8"))
            //.addQueryParameter("version", "1.1.0")
            //.addQueryParameter("sign", MD5.md5("key=" + mKey));
            // 构建新的请求
            Request newRequest = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(authorizedUrlBuilder.build())
                    .build();
            //gan-----end
            if (!isNetworkReachable(MyApplication.getInstance().getApplicationContext())) {
                newRequest = newRequest.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
                        .build();
            }

            Response response = chain.proceed(newRequest);
            if (isNetworkReachable(MyApplication.getInstance().getApplicationContext())) {
                int maxAge = 60 * 60; // 有网络时 设置缓存超时时间1个小时
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            //***************打印Log*****************************
            if (Constant.DEBUG) {
                String requestUrl = newRequest.url().toString(); // 获取请求url地址
                String methodStr = newRequest.method(); // 获取请求方式
                RequestBody body = newRequest.body(); // 获取请求body
                String bodyStr = (body == null ? "" : body.toString());
                // 打印Request数据
                LogUtils.D("gan-retrofit-okhttp3", "requestUrl=====>" + requestUrl);
                LogUtils.D("gan-retrofit-okhttp3", "requestMethod=====>" + methodStr);
                LogUtils.D("gan-retrofit-okhttp3", "requestBody=====>" + bodyStr);
                if (Constant.NET_DATA_SHOW) {
                    //打印返回数据
                    LogUtils.D("gan-retrofit-okhttp3", "responseBody=====>" + response.body().string());
                    Constant.NET_DATA_SHOW=false;
                }

            }
            return response;
        }
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }


    /**
     * 自动管理Cookies
     */
    private static class CookiesManager implements CookieJar {
        private final PersistentCookieStore cookieStore = new PersistentCookieStore(MyApplication.getInstance().getApplicationContext());

        //在接收时，读取response header中的cookie
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                }
            } else {
                Log.i("gan", "cookie为null");
            }
        }

        //分别是在发送时向request header中加入cookie
        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            Log.i("gan", "url为---" + url);
            List<Cookie> cookies = cookieStore.get(url);
            if (cookies.size() < 1) {
                Log.i("gan", "cookies为null");
            }
            return cookies;
        }
    }

    /**
     * 判断网络是否可用
     *
     * @param context Context对象
     */
    public static Boolean isNetworkReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.isAvailable());
    }

}
