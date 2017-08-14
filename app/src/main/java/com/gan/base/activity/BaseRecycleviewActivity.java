package com.gan.base.activity;

import android.support.v7.widget.LinearLayoutManager;

import com.gan.myrecycleview.CommonAdapter;
import com.gan.myrecycleview.MyRecycleView;
import com.gan.myrecycleview.base.ViewHolder;
import com.gan.base.R;
import com.gan.base.net.requestbean.BaseRequest4List;
import com.gan.base.net.subscribers.RecycleviewSubscriber;
import com.gan.base.net.subscribers.RecycleviewSubscriberOnNextListener;
import com.gan.base.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gan on 2017/5/9.
 */

public abstract class BaseRecycleviewActivity<T> extends BaseActivity implements MyRecycleView.RefreshLoadMoreListener{
    @Override
    protected void afterView() {
        ButterKnife.bind(this);
    }

    @BindView(R.id.pager_base_recycleview)
    MyRecycleView recycleView;
    private boolean isFirstIn=true;
    private int page = 1;
    private int PAGESIZE = 10;
    private int oldPage=1;
    private CommonAdapter<T> mAdapter;
    private RecycleviewSubscriberOnNextListener<List<T>> getTopMovieOnNext;
    public List<T> dataAllList = new ArrayList<T>();
    private RecycleviewSubscriber<List<T>> subscriber;

    private void initView() {
        mAdapter =getRecyclerViewAdapter();;//初始化适配器
        recycleView.setRefreshLoadMoreListener(this);//下拉上拉加载更多监听
        if (!setRecyclerViewField()){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recycleView.setLayoutManager(layoutManager);
        };
        recycleView.setAdapter(mAdapter);
        initNetListener();
    }

    /**
     * 设置recycleview属性
     */
    public abstract boolean setRecyclerViewField();

    /**
     * 初始化适配器
     */
    public CommonAdapter<T> getRecyclerViewAdapter(){
        return new CommonAdapter<T>(this,getItemLayoutId(), dataAllList) {
            @Override
            protected void convert(ViewHolder holder, T t, int position) {
                doItemUI(holder,t,position);
            }
        };
    }

   public void freshItem(){
       recycleView.notifyDataSetChanged();
   }

    protected abstract int getItemLayoutId();

    protected abstract void doItemUI(ViewHolder viewHolder, T t, int position);

    /**
     * 首次进入刷新
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirstIn) {
            initView();
            recycleView.firstLoadingView("数据加载中");
            isFirstIn = false;
        }
    }

    @Override
    public void onRefresh() {
        oldPage=page;
        page = 1;
        subscriber=new RecycleviewSubscriber<List<T>>(getTopMovieOnNext,recycleView,R.drawable.icon_nonet,R.drawable.icon_err);
        BaseRequest4List request=getNetRequest();
        request.setStart((page-1)*PAGESIZE);
        request.setCount(PAGESIZE);
        getNetData(subscriber,request);
    }

    /**
     * 写网络请求接口
     * @param subscriber
     * @param request
     */
    protected abstract void getNetData(RecycleviewSubscriber<List<T>> subscriber, BaseRequest4List request);

    /**
     * 网络请求体requset
     * @return
     */
    protected abstract BaseRequest4List getNetRequest();

    @Override
    public void onLoadMore() {
        page++;
        subscriber=new RecycleviewSubscriber<List<T>>(getTopMovieOnNext,recycleView,R.drawable.icon_nonet,R.drawable.icon_err);
        BaseRequest4List request=getNetRequest();
        request.setStart((page-1)*PAGESIZE);
        request.setCount(PAGESIZE);
        getNetData(subscriber,request);
    }

    private void initNetListener() {

        getTopMovieOnNext = new RecycleviewSubscriberOnNextListener<List<T>>() {
            @Override
            public void onNext(List<T> subjects) {
                if(page==1){
                    recycleView.setDateRefresh(dataAllList, subjects,getNoDataDrawable(),getNoDataString());
                    ToastUtil.ToastCenter( "刷新完成");
                }else{
                    recycleView.setDateLoadMore(dataAllList, subjects);
                }
            }

            @Override
            public void onErr(int drawable, String msg) {
                if (page==1){
                    if (dataAllList.isEmpty())
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

    /**
     * 没数据时的提示信息
     * @return
     */
    protected abstract String getNoDataString();

    /**
     * 没数据时的提示图片
     * @return
     */
    protected abstract int getNoDataDrawable();

    @Override
    public void onDestroy() {
        if (subscriber!=null){
            subscriber.onActivityDestroy();
        }
        super.onDestroy();
    }

    @Override
    public int getContentView() {
        return R.layout.pager_base_recycleview;
    }

}
