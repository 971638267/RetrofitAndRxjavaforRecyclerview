package com.gan.base.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.gan.base.R;
import com.gan.base.net.networks.NetWorks;
import com.gan.base.net.requestbean.BaseRequest4List;
import com.gan.base.net.requestbean.MovieInfo;
import com.gan.base.net.subscribers.RecycleviewSubscriber;
import com.gan.base.util.StringFormatUtil;
import com.gan.myrecycleview.MyRecycleView;
import com.gan.myrecycleview.base.ViewHolder;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by gan on 2017/9/11.
 */
public class RecycleviewActivity extends BaseRecycleviewActivity<MovieInfo>{
    @Override
    public boolean setRecyclerViewField() {
        setTitle("测试列表页");
        recycleView.setOnItemClickListener(new MyRecycleView.ItemClickListener() {
            @Override
            public void onClick(View view, RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public void onLongClick(View view, RecyclerView.ViewHolder holder, int position) {

            }
        });
        return false;
    }


    @Override
    protected int getItemLayoutId() {
        return R.layout.item_movie_list;
    }

    @Override
    protected void doItemUI(ViewHolder viewHolder, MovieInfo o, int position) {
        ViewHelper.setScaleX(viewHolder.getConvertView(),0.8f);
        ViewHelper.setScaleY(viewHolder.getConvertView(),0.8f);
        ViewPropertyAnimator.animate(viewHolder.getConvertView()).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(viewHolder.getConvertView()).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        viewHolder.setText(R.id.tv_one_title,o.title);
        viewHolder.setText(R.id.tv_one_directors, StringFormatUtil.formatName(o.directors));
        viewHolder.setText(R.id.tv_one_casts, StringFormatUtil.formatName(o.casts));
        viewHolder.setText(R.id.tv_one_genres,"类型："+StringFormatUtil.formatGenres(o.genres));
        viewHolder.setText(R.id.tv_one_rating_rate,"评分："+o.rating.average);
        ImageView view=viewHolder.getView(R.id.iv_one_photo);
        ImageLoader.getInstance().displayImage(o.images.large,view);
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
}
