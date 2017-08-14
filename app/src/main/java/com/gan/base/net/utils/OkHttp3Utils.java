package com.gan.base.net.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.gan.base.application.MyApplication;
import com.gan.base.constant.Constant;
import com.gan.base.util.PrefUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 类描述：封装一个OkHttp3的获取类
 * Created by ganyufei on 2016/6/3.
 */
public class OkHttp3Utils {

    private static OkHttpClient mOkHttpClient;

    //设置缓存目录
    // private static File cacheDirectory = new File(MyApplication.getInstance().getApplicationContext().getCacheDir(), "response");
    private static File cacheDirectory = new File(MyApplication.getInstance().getApplicationContext().getExternalCacheDir(), "response");
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
                    .cache(cache)
                    //设置一个自动管理cookies的管理器
                   // .cookieJar(new CookiesManager())
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
            HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host());

            // 构建新的请求
            RequestBody body = oldRequest.body();
            RequestBody newBody = null;
            //收集请求参数，方便调试
            StringBuilder paramsBuilder = new StringBuilder();
            if (oldRequest.method() == "GET") {
                authorizedUrlBuilder.addQueryParameter("tokenId", PrefUtils.getString("tokenId", ""))
                        .addQueryParameter("userId", PrefUtils.getInt("userId", 0) + "");
            } else {


                if (body instanceof FormBody) {
                    newBody = addParamsToFormBody((FormBody) body, paramsBuilder);
                } else if (body instanceof MultipartBody) {
                    newBody = addParamsToMultipartBody((MultipartBody) body, paramsBuilder);
                } else {
                    body=null;
                    newBody = addParamsToFormBody((FormBody) body, paramsBuilder);
                }
            }
            Request newRequest;
            if (newBody != null) {
                newRequest = oldRequest.newBuilder()
                        .method(oldRequest.method(), newBody)
                        .url(authorizedUrlBuilder.build())
                        .build();
                LogUtils.D("gan-retrofit-okhttp3", "resquestBody=====>" + paramsBuilder.toString());
            } else {
                newRequest = oldRequest.newBuilder()
                        .method(oldRequest.method(), body)
                        .url(authorizedUrlBuilder.build())
                        .build();
            }

            //gan-----end

            //缓存控制
            if (!isNetworkReachable(MyApplication.getInstance().getApplicationContext())) {
                /**
                 * 离线缓存控制  总的缓存时间=在线缓存时间+设置离线缓存时间
                 */
                CacheControl tempCacheControl = CacheControl.FORCE_NETWORK;//不允许缓存
                if (com.gan.base.constant.UrlConstant.CACHE) {//使用缓存
                    int maxStale = 60 * 60 * 24 * 7; // 离线时缓存保存1周,单位:秒
                    tempCacheControl = new CacheControl.Builder()
                            .onlyIfCached()
                            .maxStale(maxStale, TimeUnit.SECONDS)
                            .build();
                }

                newRequest = newRequest.newBuilder()
                        .cacheControl(tempCacheControl)//使用缓存
                        .build();
                LogUtils.D("gan-retrofit-okhttp3", "无网络===========>读取缓存");
            }


            Response response = chain.proceed(newRequest);
            //***************打印Log*****************************
            if (Constant.DEBUG) {
                String requestUrl = newRequest.url().toString(); // 获取请求url地址
                String methodStr = newRequest.method(); // 获取请求方式
                // 打印Request数据
                LogUtils.D("gan-retrofit-okhttp3", "requestUrl=====>" + requestUrl);
                LogUtils.D("gan-retrofit-okhttp3", "requestMethod=====>" + methodStr);
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
                int maxAge = 1 * 60; // 有网络时 设置缓存超时时间1分钟
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
                Log.i("gan---saveFromResponse", "cookie为null");
            }
        }

        //分别是在发送时向request header中加入cookie
        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            Log.i("gan--loadForRequest", "url为---" + url);
            List<Cookie> cookies = cookieStore.get(url);
            if (cookies.size() < 1) {
                Log.i("gan--loadForRequest", "cookies为null");
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


    /**
     * 为MultipartBody类型请求体添加参数
     *
     * @param body
     * @param paramsBuilder
     * @return
     */
    private static MultipartBody addParamsToMultipartBody(MultipartBody body, StringBuilder paramsBuilder) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        //添加tokenId
        String tokenId =  PrefUtils.getString("tokenId","");
        builder.addFormDataPart("tokenId", tokenId);
        //userId
        int userId =  PrefUtils.getInt("userId",0);
        builder.addFormDataPart("userId", userId+"");
        //添加原请求体
        paramsBuilder.append("tokenId="+tokenId).append("&").append("userId="+userId);
        for (int i = 0; i < body.size(); i++) {
            builder.addPart(body.part(i));
            paramsBuilder.append("&");
            paramsBuilder.append(body.part(i));
            paramsBuilder.append("=");
            paramsBuilder.append(body.part(i));
        }
        return builder.build();
    }

    /**
     * 为FormBody类型请求体添加参数
     *
     * @param body
     * @param paramsBuilder
     * @return
     */
    private static FormBody addParamsToFormBody(FormBody body, StringBuilder paramsBuilder) {
        FormBody.Builder builder = new FormBody.Builder();
        //添加tokenId
        String tokenId =  PrefUtils.getString("tokenId","");
        builder.add("tokenId", tokenId);
        //userId
        int userId =  PrefUtils.getInt("userId",0);
        builder.add("userId", userId+"");
        //添加原请求体
        paramsBuilder.append("tokenId="+tokenId).append("&").append("userId="+userId);
        if (body != null) {
            for (int i = 0; i < body.size(); i++) {
                builder.addEncoded(body.encodedName(i), body.encodedValue(i));
                paramsBuilder.append("&");
                paramsBuilder.append(body.name(i));
                paramsBuilder.append("=");
                paramsBuilder.append(body.value(i));
            }
        }
        return builder.build();
    }


}
