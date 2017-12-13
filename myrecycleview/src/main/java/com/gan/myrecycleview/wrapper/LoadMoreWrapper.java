package com.gan.myrecycleview.wrapper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gan.myrecycleview.R;
import com.gan.myrecycleview.base.ViewHolder;
import com.gan.myrecycleview.utils.WrapperUtils;

public class LoadMoreWrapper<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;

    private RecyclerView.Adapter mInnerAdapter;
    private View mLoadMoreView;
   // private int mLoadMoreLayoutId;
    private RelativeLayout mFooterView;
    private View mNodataView;
    private boolean hasLoadMore=true;

    public LoadMoreWrapper(RecyclerView.Adapter adapter)
    {
        mInnerAdapter = adapter;
    }

    private boolean hasLoadMore()
    {
        return hasLoadMore;
    }


    private boolean isShowLoadMore(int position)
    {
        return hasLoadMore() && (position >= mInnerAdapter.getItemCount());
    }

    @Override
    public int getItemViewType(int position)
    {
        if (isShowLoadMore(position))
        {
            return ITEM_TYPE_LOAD_MORE;
        }
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == ITEM_TYPE_LOAD_MORE)
        {
            ViewHolder holder;
            if (mLoadMoreView != null)
            {
                mFooterView = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.myrecycle_footview, parent,
                        false);

                mNodataView=LayoutInflater.from(parent.getContext()).inflate(R.layout.myrecycle_nodata_view, parent,
                        false);
                mFooterView.addView(mLoadMoreView);
                mFooterView.addView(mNodataView);
                mNodataView.setVisibility(View.GONE);
                holder = ViewHolder.createViewHolder(parent.getContext(),mFooterView);
            } else
            {
                mFooterView = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.myrecycle_footview, parent,
                        false);
                mNodataView=LayoutInflater.from(parent.getContext()).inflate(R.layout.myrecycle_nodata_view, parent,
                        false);
                mLoadMoreView=LayoutInflater.from(parent.getContext()).inflate(R.layout.mycycle_foot_default_loading, parent,
                        false);
                mFooterView.addView(mLoadMoreView);
                mFooterView.addView(mNodataView);
                mNodataView.setVisibility(View.GONE);
                holder = ViewHolder.createViewHolder(parent.getContext(),mFooterView);
            }
            return holder;
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (isShowLoadMore(position))
        {
            if (mOnLoadMoreListener != null)
            {
                mOnLoadMoreListener.onLoadMoreRequested();
            }
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback()
        {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position)
            {
                if (isShowLoadMore(position))
                {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null)
                {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder)
    {
        mInnerAdapter.onViewAttachedToWindow(holder);

        if (isShowLoadMore(holder.getLayoutPosition()))
        {
            setFullSpan(holder);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder holder)
    {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams)
        {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

            p.setFullSpan(true);
        }
    }

    @Override
    public int getItemCount()
    {
        return mInnerAdapter.getItemCount() + (hasLoadMore() ? 1 : 0);
    }




    public interface OnLoadMoreListener
    {
        void onLoadMoreRequested();
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public void setFootCanLoad(boolean footCanload) {
        if(hasLoadMore()) {
            if (footCanload) {//如果可以加载更多布局
                if (mNodataView!=null)
                mNodataView.setVisibility(View.GONE);
            } else {
                if (mNodataView!=null)
                mNodataView.setVisibility(View.VISIBLE);
            }
        }
    }
    public LoadMoreWrapper setOnLoadMoreListener(OnLoadMoreListener loadMoreListener)
    {
        if (loadMoreListener != null)
        {
            mOnLoadMoreListener = loadMoreListener;
        }
        return this;
    }

    public LoadMoreWrapper setLoadMoreView(View loadMoreView)
    {
        mLoadMoreView = loadMoreView;
        return this;
    }

    public LoadMoreWrapper setLoadMoreView(boolean hasLoadMore)
    {
        this.hasLoadMore = hasLoadMore;
        return this;
    }
}
