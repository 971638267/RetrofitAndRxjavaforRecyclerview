package com.yunpai.tms.pagers;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


import com.yunpai.tms.R;
import com.yunpai.tms.adapter.ContentAdapter;
import com.yunpai.tms.util.KeyBoardUtils;
import com.yunpai.tms.util.ToastUtil;
import com.zxing.activity.CaptureZxingActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 页面（运单）的实现
 *
 * @author 甘玉飞
 * @ClassName: WaybillPager
 * @Description: 页面（运单）的实现
 * @date 2017年02月21日
 */
public class WaybillPager extends ContentBasePager {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.waybill_page_search_et)
    EditText searchEt;
    private List<ContentBasePager> list;
    private ContentAdapter adapter;
    private String[] titles = {"待发车", "运输中", "已签收"};
    private int currentPage = 0;// 当前选择界面
    private boolean isFirstIn = true;

    public WaybillPager(AppCompatActivity activity) {
        super(activity);
        ButterKnife.bind(this, mRootView);
        initView();//初始化tab项
        initSearchView();//初始化搜索框
    }

    @Override
    public void initData() {
        if (isFirstIn) {
            list.get(0).initData();// 初始化第一个页面数据
            isFirstIn = false;
        }
    }

    @Override
    public void outData() {
        //ToastUtil.ToastCenter(mActivity,"离开ThirdPager");
    }

    @Override
    public int getContentView() {
        return R.layout.pager_waybill;
    }

    private void initView() {
        //页面，数据源
        list = new ArrayList<ContentBasePager>();
        list.add(new WaybillInnerPager(mActivity));
        list.add(new WaybillInnerPager(mActivity));
        list.add(new WaybillInnerPager(mActivity));
        //ViewPager的适配器
        adapter = new ContentAdapter(mActivity, list) {
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        //绑定
        tabLayout.setupWithViewPager(viewPager);

        //滑动监听
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                list.get(currentPage).outData();
                currentPage = arg0;
                list.get(arg0).initData();// 获取当前被选中的页面, 初始化该页面数据
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    private void initSearchView() {
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    search();
                    return false;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.waybill_page_qcode_iv)
    public void qrcode() {
        mActivity.startActivityForResult(new Intent(mActivity, CaptureZxingActivity.class), 200);
    }
    //点击查询按钮
    @OnClick(R.id.waybill_page_search_tv)
    public void search() {
        if (TextUtils.isEmpty(searchEt.getText().toString().trim())){
            ToastUtil.ToastCenter("请输入订单号");
        }else{
            doSearchWayBill();
        }
    }
    /**
     * 订单查询操作
     */
    private void doSearchWayBill() {
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {
        for (ContentBasePager c:list){
            c.onDestroy();
        }
        super.onDestroy();
    }
}
