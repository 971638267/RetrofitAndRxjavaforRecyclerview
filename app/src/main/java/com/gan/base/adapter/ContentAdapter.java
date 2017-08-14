package com.gan.base.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


import com.gan.base.pagers.ContentBasePager;

import java.util.List;

/**
 * viewpager适配器
 *
 * @ClassName: ContentAdapter
 * @Description:
 * @author 甘玉飞
 * @date 2016年6月21日 下午4:51:47
 *
 */

public class ContentAdapter extends PagerAdapter {
    private final Context mContext;
    private List<ContentBasePager> mPagerList;
    public ContentAdapter(Context mContext, List<ContentBasePager> mPagerList){
        this.mContext=mContext;
        this.mPagerList=mPagerList;
    }

    @Override
    public int getCount() {
        return mPagerList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ContentBasePager pager = mPagerList.get(position);
        container.addView(pager.mRootView);
        return pager.mRootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
