package com.yunpai.tms.pagers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;


import com.gan.myrecycleview.CommonAdapter;
import com.gan.myrecycleview.MyRecycleView;
import com.gan.myrecycleview.base.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunpai.tms.R;
import com.yunpai.tms.net.networks.NetWorks;
import com.yunpai.tms.net.requestbean.BaseRequest4List;
import com.yunpai.tms.net.requestbean.MovieInfo;
import com.yunpai.tms.net.subscribers.RecycleviewSubscriber;
import com.yunpai.tms.net.subscribers.RecycleviewSubscriberOnNextListener;
import com.yunpai.tms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 甘玉飞
 * @ClassName: MessagePager
 * @Description: 页面（消息）的实现
 * @date 2017年02月21日
 */
public class MessagePager extends BaseRecycleviewPager<MovieInfo> {

    public MessagePager(AppCompatActivity activity){
        super(activity);
    }

    @Override
    public boolean setRecyclerViewField() {
        //设置布局管理

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // GridLayoutManager layoutManager= new GridLayoutManager(mActivity, 2);
        recycleView.setLayoutManager(layoutManager);
        return true;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.fragment_discover_cardview_item;
    }

    @Override
    protected void doItemUI(ViewHolder viewHolder, MovieInfo o, int position) {
        viewHolder.setText(R.id.activity_title,o.title);
        viewHolder.setText(R.id.activity_date,o.year);
        ImageView iv=viewHolder.getView(R.id.img_iv);
        ImageLoader.getInstance().displayImage(o.images.large,iv);
    }

    @Override
    protected BaseRequest4List getNetRequest() {
        return new BaseRequest4List();
    }

    @Override
    protected String getNoDataString() {
        return "暂无消息";
    }

    @Override
    protected int getNoDataDrawable() {
        return R.drawable.no_data;
    }

    @Override
    protected void getNetData(RecycleviewSubscriber subscriber, BaseRequest4List request) {
        NetWorks.getInstance().inTheaters(subscriber,request);
    }

    @Override
    public int getContentView() {
        return R.layout.pager_message;
    }

}
