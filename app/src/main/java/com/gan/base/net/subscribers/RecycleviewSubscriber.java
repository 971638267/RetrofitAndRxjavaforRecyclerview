package com.gan.base.net.subscribers;

import android.content.Intent;

import com.gan.myrecycleview.MyRecycleView;
import com.gan.base.activity.LoginActivity;
import com.gan.base.application.AppStackManager;
import com.gan.base.application.MyApplication;
import com.gan.base.net.apiexception.ApiException;
import com.gan.base.util.PrefUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {

    }

    /**
     * 完成，隐藏ProgressDialog
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
            doErr(noNet,"请求超时，请检查您的网络状态");
        } else if (e instanceof ConnectException) {
            doErr(noNet,"无法连接到服务器，请检查您的网络状态");
        }else if (e instanceof HttpException){             //HTTP错误
           int code= ((HttpException) e).code();
            doNetErr(code);
            e.printStackTrace();
        }else if (e instanceof UnknownHostException){  //无网络
            doErr(noNet,"网络异常，请检查您的网络状态");
            e.printStackTrace();
        } else if (e instanceof ApiException){
            if ("NOT_LOGIN".equals(e.getMessage())){
                PrefUtils.setBoolean("isLogin",false);
                PrefUtils.SetString("tokenId","");
                PrefUtils.SetString("userId","");
                Intent it= new Intent(MyApplication.getInstance(), LoginActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getInstance().startActivity(it);
                AppStackManager.getInstance().LoginActivity();
            }else{
                doErr(onErr,e.getMessage());
            }
            e.printStackTrace();
        }else {
            doErr(onErr,"服务器忙");
            e.printStackTrace();
        }

    }

    /**
     * 分析网络异常
     * @param code
     */
    private void doNetErr(int code) {
        switch (code){
            case 404:
                doErr(noNet,"请求接口异常");
                break;
            default:
                doErr(noNet,"网络异常，请检查您的网络状态");
                break;
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