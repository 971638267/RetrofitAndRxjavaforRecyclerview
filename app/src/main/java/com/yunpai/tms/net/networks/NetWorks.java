package com.yunpai.tms.net.networks;


import com.yunpai.tms.net.apiexception.ApiException;
import com.yunpai.tms.net.resultbean.HttpResult;
import com.yunpai.tms.net.resultbean.Subject;
import com.yunpai.tms.net.subscribers.ProgressSubscriber;
import com.yunpai.tms.net.utils.RetrofitUtils;

import java.util.List;
import java.util.Objects;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;


/**
 * Created by gan on 2017/2/20 0003.
 */
public class NetWorks extends RetrofitUtils {
    private NetWorks() {
        super();
    }


    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final NetWorks INSTANCE = new NetWorks();
    }

    //获取单例
    public static NetWorks getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //测试用例子
    public void Test250(Subscriber<List<Subject>> subscriber, int start, int count) {
        Observable observable = service.top250(start, count).map(new HttpResultFunc<List<Subject>>());
        setSubscribe(observable, subscriber);
    }

    /**
     * 获取运单详情
     * @param subscriber
     * @param billNo
     * @param start
     * @param pageSize
     */
    public void getWyBillByNo(ProgressSubscriber<Object> subscriber, String billNo, int start, int pageSize) {
       // Observable observable = service.getWyBillByNo(billNo,start,pageSize).map(new HttpResultFunc<Object>());
       // setSubscribe(observable, subscriber);
    }

    /**
     * 登录
     * @param subscriber
     * @param companyCode
     * @param loginName
     * @param loginPwd
     */
    public void postLogin(ProgressSubscriber<Object> subscriber, String companyCode, String loginName, String loginPwd) {
        //Observable observable = service.postLogin(companyCode,loginName,loginPwd).map(new HttpResultFunc<Object>());
        //setSubscribe(observable, subscriber);
    }


    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
            /*if (httpResult.getCount() == 0) {
                throw new ApiException(100);
            }*/
            return httpResult.getSubjects();
        }
    }
}
