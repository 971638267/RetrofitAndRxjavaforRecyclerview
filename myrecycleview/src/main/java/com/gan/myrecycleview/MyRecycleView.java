package com.gan.myrecycleview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gan.myrecycleview.wrapper.HeaderAndFooterWrapper;
import com.gan.myrecycleview.wrapper.LoadMoreWrapper;

import java.util.List;

/**
 * 用途: 自定义recycleview实现下拉刷新和自动加载
 * 创建者:ganyufei
 * 时间: 2017/2/8
 */

public class MyRecycleView<T> extends LinearLayout {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRfl;
    // private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    private CommonAdapter mAdapter;
    private RefreshLoadMoreListener mRefreshLoadMoreListner;//下拉和加载更多监听
    private ItemClickListener itemClickListener;//item点击监听
    private LinearLayout mExceptView;
    private LinearLayout mLoadingView;
    private boolean hasMore = false;//是否还有更多数据加载
    private boolean canMore = true;//是否可以加载更多
    private boolean isCanRefresh = true;//是否可以刷新更多
    private boolean isRefresh = false;//正在刷新
    private boolean isLoadMore = false;//正在加载更多
    private LoadMoreWrapper mLoadMoreWrapper;//为了实现加载更多footview

    private ImageView exceptIv;//异常图片控件
    private TextView exceptTv;//异常内容文本控件

    private  ProgressBar loadingIv;//正在加载图片控件
    private TextView loadingTv;//正在加载文本控件
    private RecyclerView.ItemAnimator itemAnimator;
    private HeaderAndFooterWrapper<T> headerWrapper;//头布局
    private boolean addHead=false;//是否添加头布局
    private int headViewId;

    public MyRecycleView(Context context) {
        super(context);
    }

    public MyRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LinearLayout rootLl = new LinearLayout(context);
        rootLl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mLoadingView = initLoadingView(context);
        mLoadingView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mExceptView = initExceptionView(context);
        mExceptView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mExceptView.setVisibility(View.GONE);
        swipeRfl = new SwipeRefreshLayout(context);
        swipeRfl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        swipeRfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light);
        FrameLayout bootLl = new FrameLayout(context);
        bootLl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        recyclerView = new RecyclerView(context);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setHorizontalScrollBarEnabled(true);
        if (itemAnimator!=null)
            recyclerView.setItemAnimator(itemAnimator);
        else {
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        recyclerView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        bootLl.addView(mLoadingView);
        bootLl.addView(recyclerView);
        bootLl.addView(mExceptView);
        swipeRfl.addView(bootLl);
        rootLl.addView(swipeRfl);
        this.addView(rootLl);
        /**
         * 下拉至顶部刷新监听
         */
        mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!isRefresh && !isLoadMore) {
                    isRefresh = true;
                    refresh();
                }
            }
        };
        swipeRfl.setOnRefreshListener(mRefreshListener);
        recyclerView.setHasFixedSize(true);//不是瀑布流这个将可以优化性能
    }


    public MyRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isRefreshOrLoadmore(){
        return isRefresh||isLoadMore;
    }

    /**
     * 错误提示界面初始化
     *
     * @param context
     * @return
     */
    private LinearLayout initExceptionView(Context context) {
        LinearLayout rootLl = (LinearLayout) View.inflate(context, R.layout.mycycleview_err, null);
        exceptIv = (ImageView) rootLl.findViewById(R.id.myrecle_img);
        exceptIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点击图片刷新
                if (!isRefresh) {
                    swipeRfl.setRefreshing(true);
                    isRefresh = true;
                    refresh();
                }
            }
        });
        exceptTv = (TextView) rootLl.findViewById(R.id.myrecle_msg);
        return rootLl;
    }

    /**
     * 初始化正在加载页面
     *
     * @param context
     * @return
     */
    private LinearLayout initLoadingView(Context context) {
        LinearLayout rootLl = (LinearLayout) View.inflate(context, R.layout.mycycleview_firstload, null);
        loadingIv = (ProgressBar) rootLl.findViewById(R.id.myrecle_load_progress);
        loadingTv = (TextView) rootLl.findViewById(R.id.myrecle_load_msg);
        return rootLl;
    }

    /**
     * drawableId 错误提示图片
     * exceptStr 错误提示语
     */
    private void customExceptView(int drawableId, String exceptStr) {
        recyclerView.setVisibility(View.INVISIBLE);
        mExceptView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
        exceptIv.setImageResource(drawableId);
        exceptTv.setText(exceptStr);
        swipeRfl.setEnabled(false);//出现错误之后，将设定无法下拉，运用点击图片进行刷新
    }

    /**
     * drawableId 正在加载提示图片
     * exceptStr 正在加载提示语
     */
    public void customLoadView(String exceptStr) {
        recyclerView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
        mExceptView.setVisibility(View.INVISIBLE);
        loadingTv.setText(exceptStr);
        swipeRfl.setEnabled(false);
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    public void setAdapter(CommonAdapter adapter) {
        if (adapter != null) {
            this.mAdapter = adapter;
            if (canMore) {//是否可以加载更多
                mLoadMoreWrapper = new LoadMoreWrapper(mAdapter);
               // mLoadMoreWrapper.setLoadMoreView(false);//是否有加载更多默认有
                mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        /**
                         * 无论水平还是垂直
                         */
                        if (hasMore && !isLoadMore && !isRefresh && canMore) {
                            isLoadMore = true;
                            loadMore();
                        }
                    }
                });
                if (addHead) {
                    this.headerWrapper = new HeaderAndFooterWrapper<T>(mLoadMoreWrapper);
                    headerWrapper.addHeaderView(headViewId);
                    recyclerView.setAdapter(headerWrapper);
                }else{
                    recyclerView.setAdapter(mLoadMoreWrapper);
                }

            } else {
                if (addHead) {
                    this.headerWrapper = new HeaderAndFooterWrapper<T>(mAdapter);
                    headerWrapper.addHeaderView(headViewId);
                    recyclerView.setAdapter(headerWrapper);
                }else{
                    recyclerView.setAdapter(mAdapter);
                }
            }

            mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    if (itemClickListener != null)
                        itemClickListener.onClick(view, holder, position);
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    if (itemClickListener != null)
                        itemClickListener.onLongClick(view, holder, position);
                    return true;
                }
            });
        }
    }

    private void setHasMore(boolean enable) {
        this.hasMore = enable;
        if (mLoadMoreWrapper!=null)
        mLoadMoreWrapper.setFootCanLoad(hasMore);
    }

   /* public boolean isHasMore() {
        return hasMore;
    }

    public boolean isCanMore() {
        return canMore;
    }*/

    public boolean isCanMore() {
        return canMore;
    }

    public void setCanMore(boolean canMore) {
        this.canMore = canMore;
    }

    public void setPullRefreshEnable(boolean enable) {
        isCanRefresh = enable;
        swipeRfl.setEnabled(enable);
    }

    public boolean getPullRefreshEnable() {
        return swipeRfl.isEnabled();
    }

    public void loadMore() {
        if (mRefreshLoadMoreListner != null && hasMore && canMore) {
            mRefreshLoadMoreListner.onLoadMore();
        }
    }

    /**
     * 加载更多完毕,为防止频繁网络请求,isLoadMore为false才可再次请求更多数据
     */
    public void setLoadMoreCompleted() {
        isLoadMore = false;
    }

    public void stopRefresh() {
        isRefresh = false;
        swipeRfl.setRefreshing(false);
        if (isCanRefresh) swipeRfl.setEnabled(true);
    }

    public void setRefreshLoadMoreListener(RefreshLoadMoreListener listener) {
        mRefreshLoadMoreListner = listener;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    /**
     * 刷新动作，用于请求网络数据
     */
    public void refresh() {
        swipeRfl.setRefreshing(true);
        mExceptView.setVisibility(View.INVISIBLE);
        if (mRefreshLoadMoreListner != null) {
            mRefreshLoadMoreListner.onRefresh();
        }
    }

    public void notifyDataSetChanged() {
        //firstload布局只能出现一次，所以这里判断如果显示，就隐藏
        if (mLoadingView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            mExceptView.setVisibility(View.INVISIBLE);
            mLoadingView.setVisibility(View.INVISIBLE);
        }
       /* if (mLoadMoreWrapper != null)
            mLoadMoreWrapper.notifyDataSetChanged();
        else
            mAdapter.notifyDataSetChanged();*/
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * 第一次自动加载，不与无数据用同样布局是因为，这里要有动画效果，所以单独一个布局
     */
    public void firstLoadingView(String exceptStr) {

        customLoadView(exceptStr);
        isRefresh = true;
        if (mRefreshLoadMoreListner != null) {
            mRefreshLoadMoreListner.onRefresh();
        }
    }


    /**
     * 获取刷新数据以后的处理
     * @param actAllList
     * @param tmp
     * @param drawableId 当没有数据时提示图片
     * @param msg 没有数据时提示语
     */
    public void setDateRefresh(List<T> actAllList, List<T> tmp,int drawableId,String msg) {
        actAllList.clear();
        stopRefresh();//如果刷新则停止刷新
        if (tmp==null || tmp.isEmpty()) {
            customExceptView(drawableId, msg);
            setHasMore(false);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            setHasMore(true);
            actAllList.addAll(tmp);
        }
        notifyDataSetChanged();//刷新完毕
    }

    /**
     * 获取加载更多数据的处理
     *
     * @param actAllList
     * @param tmpLoadmore
     */
    public void setDateLoadMore(List<T> actAllList, List<T> tmpLoadmore) {
        if (tmpLoadmore==null|| tmpLoadmore.isEmpty()) {
            setHasMore(false);//如果没有更多数据则设置不可加载更多
            setLoadMoreCompleted();//加载完毕
            stopRefresh();//如果刷新则停止刷新
            return;
        }
        setHasMore(true);
        actAllList.addAll(tmpLoadmore);
        setLoadMoreCompleted();//加载完毕
        notifyDataSetChanged();//加载更多完毕
        stopRefresh();//如果刷新则停止刷新
    }

    /**
     * 刷新数据失败
     *
     * @param darwable
     * @param msg
     */
    public void setDateRefreshErr(int darwable, String msg) {
        stopRefresh();//如果刷新则停止刷新
        customExceptView(darwable, msg);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration div) {
        recyclerView.addItemDecoration(div);
    }

    /**
     * 设置item动画效果
     * @param defaultItemAnimator
     */
    public void setItemAnimator(RecyclerView.ItemAnimator defaultItemAnimator) {
        this.itemAnimator=defaultItemAnimator;
        recyclerView.setItemAnimator(itemAnimator);
    }

    /**
     * 添加头布局
     * @param headerViewId
     */
    public void addHeaderView(int headerViewId) {
        addHead=true;
        this.headViewId=headerViewId;
    }
    /**
     * 下拉刷新和自动加载监听
     */
    public interface RefreshLoadMoreListener {
        public void onRefresh();

        public void onLoadMore();
    }

    public interface ItemClickListener {
        public void onClick(View view, RecyclerView.ViewHolder holder, int position);

        public void onLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

}
