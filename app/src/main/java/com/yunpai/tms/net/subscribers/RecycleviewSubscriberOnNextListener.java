package com.yunpai.tms.net.subscribers;

/**
 * Created by liukun on 16/3/10.
 */
public interface RecycleviewSubscriberOnNextListener<T> {
    void onNext(T t);

    void onErr(int drawable, String msg);
}
