package com.gan.base.banner;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gan.base.R;


public class Banner extends LinearLayout {

	/**
	 * 上下文
	 */
	private Context mContext;

	/**
	 * 图片轮播视图
	 */
	private NoactionViewPager mAdvPager = null;

	/**
	 * 滚动图片视图适配器
	 */
	private ImageCycleAdapter mAdvAdapter;

	/**
	 * 图片轮播指示器控件
	 */
	private LinearLayout mGroup;

	
	/**
	 * 滚动图片指示器-视图列表
	 */
	private View[] mImageViews = null;

	/**
	 * 图片滚动当前图片下标
	 */
	private int mImageIndex = 0;


	//图片说明文本
	private TextView tv_title;
	//数据实体
	private List<BannerData> data;

	/**
	 * @param context
	 */
	public Banner(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public Banner(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.banner, this);
		mAdvPager = (NoactionViewPager) findViewById(R.id.adv_pager);
		tv_title = (TextView) findViewById(R.id.tv_title);
		mAdvPager.setOnPageChangeListener(new GuidePageChangeListener());
		mAdvPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						// 开始图片滚动
						startImageTimerTask();
						break;
					default:
						// 停止图片滚动
						stopImageTimerTask();
						break;
				}
				return false;
			}
		});
		// 滚动图片右下指示器视图
		mGroup = (LinearLayout) findViewById(R.id.viewGroup);
	}
	
	
	/**
	 * 装填图片数据
	 * @param bannerList
	 * @param imageCycleViewListener
	 */
	public void setImageResources(List<BannerData> bannerList, ImageCycleViewListener imageCycleViewListener) {
		// 清除所有子视图
		mGroup.removeAllViews();
		this.data = bannerList;
		// 图片广告数量
		final int imageCount = bannerList.size();
		if (imageCount>0) {
			tv_title.setText(data.get(0).getTitle());
			mImageViews = new View[imageCount];
			for (int i = 0; i < imageCount; i++) {
				//mImageView = new View(mContext);
				View mImageView=LayoutInflater.from(mContext).inflate(R.layout.banner_dot, this, false);
				mImageView.setVisibility(View.VISIBLE);
				mImageViews[i] = mImageView;
				if (i == 0) {
					mImageViews[i].setBackgroundResource(R.drawable.banner_dot_focused);
				} else {
					mImageViews[i].setBackgroundResource(R.drawable.banner_dot_normal);
				}
				mGroup.addView(mImageViews[i]);
			}
			mAdvAdapter = new ImageCycleAdapter(mContext, data, imageCycleViewListener);
			mAdvPager.setAdapter(mAdvAdapter);
			startImageTimerTask();
		}
	}
	/**
	 * 开始轮播(手动控制自动轮播与否，便于资源控制)
	 */
	public void startImageCycle() {
		startImageTimerTask();
	}

	/**
	 * 暂停轮播——用于节省资源
	 */
	public void pushImageCycle() {
		stopImageTimerTask();
	}

	/**
	 * 开始图片滚动任务
	 */
	private void startImageTimerTask() {
		stopImageTimerTask();
		// 图片每3秒滚动一次
		mHandler.postDelayed(mImageTimerTask, 3000);
	}

	/**
	 * 停止图片滚动任务
	 */
	private void stopImageTimerTask() {
		mHandler.removeCallbacks(mImageTimerTask);
	}

	private Handler mHandler = new Handler();

	/**
	 * 图片自动轮播Task
	 */
	private Runnable mImageTimerTask = new Runnable() {

		@Override
		public void run() {
			if (mImageViews != null) {
				// 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
				if ((++mImageIndex) == mImageViews.length) {
					mImageIndex = 0;
				}
				mAdvPager.setCurrentItem(mImageIndex);
			}
		}
	};

	private int indicator = Gravity.BOTTOM;
	public  void setIndicator(int indicator) {
		this.indicator = indicator;
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mGroup.getLayoutParams();
		//此处相当于布局文件中的Android:layout_gravity属性
		lp.gravity = indicator| Gravity.BOTTOM;
		mGroup.setLayoutParams(lp);
	}
	/**
	 * 轮播图片状态监听器
	 * 
	 * @author minking
	 */
	private final class GuidePageChangeListener implements OnPageChangeListener {

		

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE)
				startImageTimerTask(); // 开始下次计时
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {
			// 设置当前显示的图片下标
			mImageIndex = index;
			// 设置图片滚动指示器背景
			mImageViews[index].setBackgroundResource(R.drawable.banner_dot_focused);
			for (int i = 0; i < mImageViews.length; i++) {
				if (index != i) {
					mImageViews[i].setBackgroundResource(R.drawable.banner_dot_normal);
				}
			}
            tv_title.setText(data.get(index).getTitle());
		}

	}

	private class ImageCycleAdapter extends PagerAdapter {

		/**
		 * 图片视图缓存列表
		 */
		private ArrayList<ImageView> mImageViewCacheList;

		/**
		 * 图片资源列表
		 */
		private List<BannerData> mAdList = new ArrayList<BannerData>();

		/**
		 * 广告图片点击监听器
		 */
		private ImageCycleViewListener mImageCycleViewListener;

		private Context mContext;

		public ImageCycleAdapter(Context context, List<BannerData> adList, ImageCycleViewListener imageCycleViewListener) {
			mContext = context;
			mAdList = adList;
			mImageCycleViewListener = imageCycleViewListener;
			mImageViewCacheList = new ArrayList<ImageView>();
		}

		@Override
		public int getCount() {
			return mAdList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			BannerData bannerData = mAdList.get(position);
			ImageView imageView = null;
			if (mImageViewCacheList.isEmpty()) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				
			} else {
				imageView = mImageViewCacheList.remove(0);
			}
			// 设置图片点击监听
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mImageCycleViewListener.onImageClick(position, v);
				}
			});
			imageView.setTag(bannerData.getImage());
			container.addView(imageView);
			mImageCycleViewListener.displayImage(bannerData.getImage(), imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ImageView view = (ImageView) object;
			container.removeView(view);
			mImageViewCacheList.add(view);
		}

	}

	/**
	 * 轮播控件的监听事件
	 * 
	 * @author minking
	 */
	public static interface ImageCycleViewListener {

		/**
		 * 加载图片资源
		 * 
		 * @param imageURL
		 * @param imageView
		 */
		public void displayImage(String imageURL, ImageView imageView);

		/**
		 * 单击图片事件
		 * 
		 * @param position
		 * @param imageView
		 */
		public void onImageClick(int position, View imageView);
	}

	

}
