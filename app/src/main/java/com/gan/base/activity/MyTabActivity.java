package com.gan.base.activity;

import android.support.v7.app.AppCompatActivity;

import com.gan.base.R;
import com.gan.base.pagers.ContentBasePager;

/**
 * Created by gan on 2017/5/18.
 */
public class MyTabActivity extends BaseTabActivity{
    @Override
    public String[] getTitles() {
        setTitle("tab演示页面");
        return new String[]{"tab1","tab2"};
    }

    @Override
    public void getPager() {
        pagerlist.add(new TestPager(this));
        pagerlist.add(new TestPager(this));
    }

    class TestPager extends ContentBasePager{

        public TestPager(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public void initData() {

        }

        @Override
        public void outData() {

        }

        @Override
        public int getContentView() {
            return R.layout.activity_test;
        }
    }
}
