package com.yunpai.tms.pagers;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 *
* @ClassName: ContentBasePager  
* @Description: 4个页面的基类
* @author 甘玉飞
* @date 2017年02月21日
*
 */
public abstract class ContentBasePager  {

	public AppCompatActivity mActivity;// Mainactivity的引用
	public View mRootView;// 布局对象
	
	public ContentBasePager(AppCompatActivity activity) {
		mActivity = activity;
		int layoutId=getContentView();
		if (layoutId==0 ) {
			throw new NullPointerException("no mRootView!");
		}
		mRootView = View.inflate(mActivity, layoutId, null);
	}

	

	/**
	 * 进入该界面
	 */
	public abstract  void initData();
	/**
	 * 离开该界面
	 */
	public abstract void outData();

	public abstract int getContentView();

	/**
	 * 页面销毁
	 */
	public void onDestroy() {

	}
}
