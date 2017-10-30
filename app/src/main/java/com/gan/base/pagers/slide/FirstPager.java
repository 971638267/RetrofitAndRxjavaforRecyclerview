package com.gan.base.pagers.slide;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.gan.base.R;
import com.gan.base.pagers.ContentBasePager;

import butterknife.ButterKnife;

/**
 * 页面（朋友）的实现
 * 
 * @ClassName: FirstPager
 * @Description: TODO
 * @author 甘玉飞
 * @date 2016年6月21日 下午4:48:37
 *
 */
public class FirstPager extends ContentBasePager {

	public FirstPager(AppCompatActivity activity) {
		super(activity);
		ButterKnife.bind(this, mRootView);
		initView();
	}

	@Override
	public void initData() {
		//ToastUtil.ToastCenter(mActivity,"进入FisrtPager");
	}

	@Override
	public void outData() {
		//ToastUtil.ToastCenter(mActivity,"离开FisrtPager");
	}

	@Override
	public int getContentView() {
		return R.layout.pager_first;
	}

	private void initView() {

	}
}
