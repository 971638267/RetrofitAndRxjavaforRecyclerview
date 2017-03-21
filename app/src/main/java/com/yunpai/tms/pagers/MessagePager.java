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
import com.yunpai.tms.net.resultbean.Subject;
import com.yunpai.tms.net.subscribers.RecycleviewSubscriber;
import com.yunpai.tms.net.subscribers.RecycleviewSubscriberOnNextListener;
import com.yunpai.tms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 甘玉飞
 * @ClassName: MessagePager
 * @Description: 页面（消息）的实现
 * @date 2017年02月21日
 */
public class MessagePager extends ContentBasePager implements MyRecycleView.RefreshLoadMoreListener {
    @BindView(R.id.message_page_recycleview)
    MyRecycleView recycleView;
    private CommonAdapter<Subject> mAdapter;
    private RecycleviewSubscriberOnNextListener<List<Subject>> getTopMovieOnNext;
    private List<Subject> actAllList = new ArrayList<Subject>();
    private boolean isFirstIn =true;
    private RecycleviewSubscriber<List<Subject>> subscriber;

    public MessagePager(AppCompatActivity activity) {
        super(activity);
        ButterKnife.bind(this, mRootView);

    }

    @Override
    public void initData() {
        if (isFirstIn){
            initView();
            recycleView.firstLoadingView("数据加载中");
            isFirstIn = false;
        }
    }

    @Override
    public void outData() {
        //ToastUtil.ToastCenter(mActivity,"离开SecondPager");
    }

    @Override
    public int getContentView() {
        return R.layout.pager_message;
    }

    private void initView() {
        initAdapter();//初始化适配器
        recycleView.setRefreshLoadMoreListener(this);//下拉上拉加载更多监听
        //prrv.setPullRefreshEnable(false);//禁用刷新
        recycleView.setCanMore(false);//禁用加载更多用在setAdapter（）之前

        //设置布局管理
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(mAdapter);
        //条目监听
        recycleView.setOnItemClickListener(new MyRecycleView.ItemClickListener() {
            @Override
            public void onClick(View view, RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public void onLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                ToastUtil.ToastCenter("longclick-pos = " + position);
            }
        });
        initNetListener();
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mAdapter = new CommonAdapter<Subject>(mActivity, R.layout.fragment_discover_cardview_item, actAllList) {

            @Override
            protected void convert(ViewHolder holder, Subject s, int position) {
                holder.setText(R.id.activity_title, s.getTitle());
                /*holder.setText(R.id.activity_date, Utils.longtimeToDayDate(a
                        .getStartDate())
                        + "-"
                        + Utils.longtimeToDayDate(a.getEndDate()));*/
                holder.setText(R.id.activity_date, s.getYear());
                ImageView imageView=holder.getView(R.id.img_iv);
                ImageLoader.getInstance().displayImage(s.getImages().getLarge(), imageView);
            }
        };
    }

    @Override
    public void onRefresh() {
        //Constant.NET_DATA_SHOW=true;//开启数据打印到log
        subscriber =new RecycleviewSubscriber<List<Subject>>(getTopMovieOnNext, recycleView, R.drawable.icon_nonet, R.drawable.icon_err);
        NetWorks.getInstance().Test250(subscriber, 0, 10);

    }

    @Override
    public void onLoadMore() {

    }

    private void initNetListener() {
        getTopMovieOnNext = new RecycleviewSubscriberOnNextListener<List<Subject>>() {
            @Override
            public void onNext(List<Subject> subjects) {
                recycleView.setDateRefresh(actAllList, subjects, R.drawable.icon_no_order, "暂无订单");
                ToastUtil.ToastCenter("刷新完成");
            }

            @Override
            public void onErr(int drawable, String msg) {
                if (actAllList.isEmpty())
                    recycleView.setDateRefreshErr(drawable, msg);//显示错误面板
                else {
                    ToastUtil.ToastCenter(msg);//提示信息
                    recycleView.stopRefresh();//停止刷新
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        if (subscriber!=null)subscriber.onActivityDestroy();
        super.onDestroy();
    }
}
