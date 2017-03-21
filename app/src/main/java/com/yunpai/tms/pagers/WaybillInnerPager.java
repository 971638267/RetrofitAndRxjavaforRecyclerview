package com.yunpai.tms.pagers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
 * 用途: 运单列表的pager
 * 创建者:ganyufei
 * 时间: 2017/2/22
 */

public class WaybillInnerPager extends ContentBasePager implements MyRecycleView.RefreshLoadMoreListener {

    @BindView(R.id.waybill_recycleview)
    MyRecycleView recycleView;
    private boolean isFirstIn=true;
    private int page = 1;
    private final static int PAGESIZE = 10;
    private CommonAdapter<Subject> mAdapter;
    private RecycleviewSubscriberOnNextListener<List<Subject>> getTopMovieOnNext;
    private List<Subject> actAllList = new ArrayList<Subject>();
    private int oldPage=1;
    private RecycleviewSubscriber<List<Subject>> subscriber;

    public WaybillInnerPager(AppCompatActivity activity) {
        super(activity);
        ButterKnife.bind(this,mRootView);
    }


    private void initView() {

        initAdapter();//初始化适配器
        recycleView.setRefreshLoadMoreListener(this);//下拉上拉加载更多监听
        //prrv.setPullRefreshEnable(false);//禁用刷新
        //prrv.setCanMore(false);//禁用加载更多用在setAdapter（）之前

        //recycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        //设置布局管理
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager layoutManager= new GridLayoutManager(mActivity, 2);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(mAdapter);
        //条目监听
        recycleView.setOnItemClickListener(new MyRecycleView.ItemClickListener() {
            @Override
            public void onClick(View view, RecyclerView.ViewHolder holder, int position) {
                ToastUtil.ToastCenter("click-pos = " + position);
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
    public void initData() {
        // 第一次进入自动刷新
        if (isFirstIn) {
            initView();
            recycleView.firstLoadingView("数据加载中");
            isFirstIn = false;
        }

    }

    @Override
    public void outData() {

    }

    @Override
    public int getContentView() {
        return R.layout.fragment_waybill;
    }

    @Override
    public void onRefresh() {
        oldPage=page;
        page = 1;
        subscriber=new RecycleviewSubscriber<List<Subject>>(getTopMovieOnNext,recycleView,R.drawable.icon_nonet,R.drawable.icon_err);
        NetWorks.getInstance().Test250(subscriber,0,PAGESIZE );
    }

    @Override
    public void onLoadMore() {
        page++;
        subscriber=new RecycleviewSubscriber<List<Subject>>(getTopMovieOnNext,recycleView,R.drawable.icon_nonet,R.drawable.icon_err);
        NetWorks.getInstance().Test250(subscriber,(page-1)*PAGESIZE ,PAGESIZE );

    }

    private void initNetListener() {

        //loadMoreSubscriber=new RecycleviewSubscriber<List<Subject>>(getTopMovieOnNext,recycleView,R.drawable.icon_nonet,R.drawable.icon_err);
        getTopMovieOnNext = new RecycleviewSubscriberOnNextListener<List<Subject>>() {
            @Override
            public void onNext(List<Subject> subjects) {
                if(page==1){
                    recycleView.setDateRefresh(actAllList, subjects,R.drawable.icon_no_order,"暂无订单");
                    ToastUtil.ToastCenter( "刷新完成");
                }else{
                    recycleView.setDateLoadMore(actAllList, subjects);
                }
            }

            @Override
            public void onErr(int drawable, String msg) {
                if (page==1){
                    if (actAllList.isEmpty())
                        recycleView.setDateRefreshErr(drawable,msg);//显示错误面板
                    else{
                        ToastUtil.ToastCenter( msg);//提示信息
                        recycleView.stopRefresh();//停止刷新
                        page=oldPage;//恢复当前页记录
                    }
                    return;
                }else{
                    page--;//当前页恢复记录数据
                    ToastUtil.ToastCenter(msg);//提示错误
                    recycleView.setLoadMoreCompleted();//停止加载
                }
            }
        };

    }

    @Override
    public void onDestroy() {
    if (subscriber!=null){
        subscriber.onActivityDestroy();
    }
        super.onDestroy();
    }
}
