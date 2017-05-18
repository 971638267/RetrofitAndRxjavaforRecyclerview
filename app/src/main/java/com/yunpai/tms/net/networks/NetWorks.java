package com.yunpai.tms.net.networks;


import android.text.TextUtils;

import com.yunpai.tms.net.requestbean.BaseRequest4List;
import com.yunpai.tms.net.requestbean.MovieInfo;
import com.yunpai.tms.net.resultbean.HttpResult;
import com.yunpai.tms.net.resultbean.UserInfoResult;
import com.yunpai.tms.net.subscribers.ProgressSubscriber;
import com.yunpai.tms.net.utils.RetrofitUtils;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.yunpai.tms.net.utils.RetrofitUtils.setSubscribe;


/**
 * Created by gan on 2017/2/20 0003.
 */
public class NetWorks extends RetrofitUtils {
    private NetWorks() {
        super();
    }

    public void inTheaters(Subscriber<List<MovieInfo>> subscriber, BaseRequest4List requset) {
        Observable observable = service.inTheaters(requset.toMap()).map(new HttpResultFunc<List<MovieInfo>>());
        setSubscribe(observable, subscriber);
    }

    /**
     * 登录
     * @param subscriber
     * @param loginName
     * @param loginPwd
     */
    public void postLogin(ProgressSubscriber<UserInfoResult> subscriber, String loginName, String loginPwd) {
        Observable observable = service.postLogin(loginName,loginPwd).map(new HttpResultFunc<UserInfoResult>());
        setSubscribe(observable, subscriber);
    }
    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
  */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
            return httpResult.getSubjects();
        }
    }


    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final NetWorks INSTANCE = new NetWorks();
    }

    //获取单例
    public static NetWorks getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
