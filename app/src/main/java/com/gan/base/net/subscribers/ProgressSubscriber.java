package com.gan.base.net.subscribers;

import android.content.Context;
import android.content.Intent;


import com.gan.base.activity.LoginActivity;
import com.gan.base.application.AppStackManager;
import com.gan.base.application.MyApplication;
import com.gan.base.net.apiexception.ApiException;
import com.gan.base.net.progress.ProgressCancelListener;
import com.gan.base.net.progress.ProgressDialogHandler;
import com.gan.base.util.PrefUtils;
import com.gan.base.util.ToastUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by liukun on 16/3/10.
 */
public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private final boolean canShow;//是否显示进度条
    private SubscriberOnNextListener mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;

    private Context context;

    /**
     *
     * @param mSubscriberOnNextListener
     * @param context
     * @param canShow 是否显示进度条
     * @param canCancel 是否可以取消
     */
    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context, boolean canShow, boolean canCancel) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        this.canShow=canShow;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, canCancel);
    }

    private void showProgressDialog(){
        if (mProgressDialogHandler != null && canShow ) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog(){
        if (mProgressDialogHandler != null && canShow ) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            ToastUtil.ToastCenter("请求超时，请检查您的网络状态");
        } else if (e instanceof ConnectException) {
            ToastUtil.ToastCenter("无法连接服务器，请检查您的网络状态");
        }else if (e instanceof HttpException){
            //HTTP错误
            int code= ((HttpException) e).code();
            doNetErr(code);
            e.printStackTrace();
        }else if (e instanceof UnknownHostException){  //无网络
            ToastUtil.ToastCenter("网络异常，请检查您的网络状态");
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
                ToastUtil.ToastCenter(e.getMessage());
            }
            e.printStackTrace();
        }else {
            ToastUtil.ToastCenter("服务器忙");
            e.printStackTrace();
        }
        dismissProgressDialog();
    }


    /**
     * 分析网络异常
     * @param code
     */
    private void doNetErr(int code) {
        switch (code){
            case 404:
                ToastUtil.ToastCenter("请求接口异常");
                break;
            default:
                ToastUtil.ToastCenter("网络异常，请检查您的网络状态");
                break;
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
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}