package com.yunpai.tms.net.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


import com.yunpai.tms.application.MyApplication;
import com.yunpai.tms.constant.Constant;
import com.yunpai.tms.constant.UrlConstant;

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

    //设置缓存目录和缓存空间大小
    private static Cache provideCache() {
        Cache cache = null;
        try {
            //cache = new Cache( new File(MyApplication.getInstance().getApplicationContext().getCacheDir(), "response" ),
            cache = new Cache(new File(MyApplication.getInstance().getApplicationContext().getExternalCacheDir(), "response"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e("cache", "Could not create Cache!");
        }
        return cache;
    }

    /**
     * 获取OkHttpClient对象
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {

        if (null == mOkHttpClient) {

            //同样okhttp3后也使用build设计模式
            mOkHttpClient = new OkHttpClient.Builder()
                    .cache(provideCache())
                    //设置一个自动管理cookies的管理器
                    //.cookieJar(new CookiesManager())
                    //网络拦截器
                    .addInterceptor(baseInterceptor)
                    .addNetworkInterceptor(rewriteCacheControlInterceptor)
                    //设置请求读写的超时时间
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
        return mOkHttpClient;
    }

    /**
     * 获取缓存
     */
    private static Interceptor baseInterceptor = new Interceptor() {
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
                    //.addQueryParameter("regionZId", "")
                    //.addQueryParameter("os", "android");
            //.addQueryParameter("time", URLEncoder.encode(time, "UTF-8"))
            //.addQueryParameter("version", "1.1.0")
            //.addQueryParameter("sign", MD5.md5("key=" + mKey));
            // 构建新的请求
            Request newRequest = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(authorizedUrlBuilder.build())
                    .build();
            //gan-----end
            if (UrlConstant.CACHE) {//使用缓存
                //缓存控制
                CacheControl tempCacheControl = CacheControl.FORCE_NETWORK;//不走缓存
                if (!isNetworkReachable(MyApplication.getInstance().getApplicationContext())) {
                    /**
                     * 离线缓存控制  总的缓存时间=在线缓存时间+设置离线缓存时间
                     */
                    int maxStale = 60 * 60 * 24 * 7; // 离线时缓存保存1周,单位:秒
                    tempCacheControl = new CacheControl.Builder()
                            .onlyIfCached()
                            .maxStale(maxStale, TimeUnit.SECONDS)
                            .build();

                    LogUtils.D("gan-retrofit-okhttp3", "无网络===========>读取缓存");
                }

                newRequest = newRequest.newBuilder()
                        .cacheControl(tempCacheControl)//使用缓存
                        .build();
            }

            Response response = chain.proceed(newRequest);
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
                    Constant.NET_DATA_SHOW = false;
                }

            }
            return response;
        }
    };

    private static Interceptor rewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            if (isNetworkReachable(MyApplication.getInstance().getApplicationContext())) {
                int maxAge = 0; // 有网络时 设置缓存超时时间0秒
                response = response.newBuilder()
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 7; // 无网络时，设置超时为1周
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    };

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

