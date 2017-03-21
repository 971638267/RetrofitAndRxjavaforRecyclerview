package com.yunpai.tms.net.subscribers;

import android.content.Context;
import android.widget.Toast;


import com.yunpai.tms.net.apiexception.ApiException;
import com.yunpai.tms.net.progress.ProgressCancelListener;
import com.yunpai.tms.net.progress.ProgressDialogHandler;
import com.yunpai.tms.util.ToastUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
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
            ToastUtil.ToastCenter("网络中断，请检查您的网络状态");
        } else if (e instanceof ConnectException) {
            ToastUtil.ToastCenter("网络中断，请检查您的网络状态");
        }else if (e instanceof HttpException){             //HTTP错误
            ToastUtil.ToastCenter("网络异常，请检查您的网络状态");
            e.printStackTrace();
        } else if (e instanceof ApiException){
            ToastUtil.ToastCenter(e.getMessage());
            e.printStackTrace();
        }else {
            //这里可以收集未知错误上传到服务器
            ToastUtil.ToastCenter("服务器忙");
            e.printStackTrace();
        }
        dismissProgressDialog();
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