package com.yunpai.tms.net.subscribers;

import com.gan.myrecycleview.MyRecycleView;
import com.yunpai.tms.net.apiexception.ApiException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * 用于跟recycleview组合使用时的Subscriber
 * @param <T>
 */
public class RecycleviewSubscriber<T> extends Subscriber<T>  {

    private final MyRecycleView recycleView;
    private final int noNet;
    private final int onErr;
    private RecycleviewSubscriberOnNextListener mSubscriberOnNextListener;

    /**
     *
     * @param mSubscriberOnNextListener
     * @param recycleView
     * @param  onErr  出现异常时图片
     * @param noNet //无网络时的图片
     */
    public RecycleviewSubscriber(RecycleviewSubscriberOnNextListener mSubscriberOnNextListener, MyRecycleView recycleView, int noNet, int onErr) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.recycleView = recycleView;
        this.onErr=onErr;
        this.noNet=noNet;
    }


    /**
     * 订阅开始时调用
     */
    @Override
    public void onStart() {

    }

    /**
     * 完成
     */
    @Override
    public void onCompleted() {
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            doErr(noNet,"网络连接超时，请检查您的网络状态");
        } else if (e instanceof ConnectException) {
            doErr(noNet,"网络中断，请检查您的网络状态");
        }else if (e instanceof HttpException){             //HTTP错误
            doErr(noNet,"网络异常，请检查您的网络状态");
            e.printStackTrace();
        } else if (e instanceof ApiException){
            //ToastUtil.ToastCenter(e.getMessage());
            doErr(onErr,e.getMessage());
            e.printStackTrace();
        }else {
            doErr(onErr,"服务器忙");
            e.printStackTrace();
        }
    }

    /**
     * 处理未知异常
     */
    private void doErr(int pic ,String err) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onErr(pic,err);
        }
    }




    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * activity销毁，取消对observable的订阅，同时也取消了http请求
     */
    public void onActivityDestroy() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}