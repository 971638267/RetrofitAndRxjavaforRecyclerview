package com.gan.base.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.gan.base.R;
import com.gan.base.adapter.ContentAdapter;
import com.gan.base.pagers.ContentBasePager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
* Created by gan on 2017/5/9.
*tab页面的
*/

public abstract class BaseTabActivity extends BaseActivity {
    public List<ContentBasePager> pagerlist;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private ContentAdapter adapter;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private int currentPage = 0;// 当前选择界面
    private boolean isFirstIn=true;

    @Override
    protected int getContentView() {
        return R.layout.activity_base_tab;
    }

    @Override
    protected void afterView() {
        ButterKnife.bind(this);
        initTabView();
    }
    private void initTabView() {
        //页面，数据源
        pagerlist = new ArrayList<ContentBasePager>();
        getPager();
        //ViewPager的适配器
        adapter = new ContentAdapter(this, pagerlist) {
            @Override
            public CharSequence getPageTitle(int position) {
                return getTitles()[position];
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        //绑定
        tabLayout.setupWithViewPager(viewPager);

        //滑动监听
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                pagerlist.get(currentPage).outData();
                currentPage = arg0;
                pagerlist.get(arg0).initData();// 获取当前被选中的页面, 初始化该页面数据
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 首次进入刷新
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirstIn) {
            pagerlist.get(0).initData();// 初始化第一个页面数据
            isFirstIn=false;
        }
    }

    @Override
    protected void onDestroy() {
        for (ContentBasePager b:pagerlist){
            b.onDestroy();
        }
        super.onDestroy();
    }

    public abstract String[] getTitles();

    public abstract void getPager();
}
