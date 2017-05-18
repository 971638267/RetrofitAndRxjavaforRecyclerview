package com.yunpai.tms.activity;

import android.widget.TextView;

import com.yunpai.tms.R;
import com.yunpai.tms.net.networks.NetWorks;
import com.yunpai.tms.net.requestbean.BaseRequest4List;
import com.yunpai.tms.net.requestbean.MovieInfo;
import com.yunpai.tms.net.subscribers.ProgressSubscriber;
import com.yunpai.tms.net.subscribers.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * 用途: TODO
 * 创建者:ganyufei
 * 时间: 2017/3/20
 */
public class DetailActivity extends BaseActivity {
    @BindView(R.id.text)
    TextView textView;
    SubscriberOnNextListener lisenter=new SubscriberOnNextListener<List<MovieInfo>>() {
        @Override
        public void onNext(List<MovieInfo> o) {
            initView(o);
        }
    };
    private ProgressSubscriber subscriber;

    private void initView(List<MovieInfo> o) {
        if (o!=null && !o.isEmpty())
        textView.setText(o.toString());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_detail;
    }

    @Override
    protected void afterView() {
        setTitle("网络请求");
        ButterKnife.bind(this);
        subscriber=new ProgressSubscriber<List<MovieInfo>>(lisenter,this,true,false);
        NetWorks.getInstance().inTheaters(subscriber,new BaseRequest4List());
    }

    @Override
    protected void onDestroy() {
        if (subscriber!=null)subscriber.unsubscribe();
        super.onDestroy();
    }
}
