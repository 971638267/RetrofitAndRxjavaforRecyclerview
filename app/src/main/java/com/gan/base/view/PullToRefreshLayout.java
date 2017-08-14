package com.gan.base.view;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gan.base.R;
import com.gan.base.viewhelper.Pullable;

/**
 * 自定义的布局，用来管理三个子控件，其中一个是下拉头，一个是包含内容的pullableView（可以是实现Pullable接口的的任何View），

 */
public class PullToRefreshLayout extends RelativeLayout {
	public static final String TAG = "PullToRefreshLayout";
	// 初始状态
	public static final int INIT = 0;
	// 释放刷新
	public static final int RELEASE_TO_REFRESH = 1;
	// 正在刷新
	public static final int REFRESHING = 2;
	
	// 操作完毕
	public static final int DONE = 3;
	// 当前状态
	private int state = INIT;
	// 刷新回调接口
	private OnRefreshListener mListener;
	// 刷新成功
	public static final int SUCCEED = 0;
	// 刷新失败
	public static final int FAIL = 1;
	// 按下Y坐标，上一个事件点Y坐标
	private float downY, lastY;

	// 下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
	public float pullDownY = 0;

	// 释放刷新的距离
	private float refreshDist = 0;

	private MyTimer myTimer;
	// 回滚速度
	public float MOVE_SPEED = 8;
	// 第一次执行布局
	private boolean isLayout = false;
	// 在刷新过程中滑动操作
	private boolean isTouch = false;
	// 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
	private float radio = 2;

	// 下拉箭头的转180°动画
	private RotateAnimation rotateAnimation;
	// 均匀旋转动画
	private RotateAnimation refreshingAnimation;

	// 下拉头
	private View refreshView;
	// 下拉的箭头
	private View pullView;
	// 正在刷新的图标
	private View refreshingView;
	// 刷新结果图标
	private View refreshStateImageView;
	// 刷新结果：成功或失败
	private TextView refreshStateTextView;


	// 实现了Pullable接口的View
	private View pullableView;
	// 过滤多点触碰
	private int mEvents;
	// 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
	private boolean canPullDown = true;


	/**
	 * 执行自动回滚的handler
	 */

	 Handler updateHandler = new MyHandler(this); /*{

		@Override
		public void handleMessage(Message msg) {
			// 回弹速度随下拉距离moveDeltaY增大而增大
			MOVE_SPEED = (float) (8
					+ 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY )));
			if (!isTouch) {
				// 正在刷新，且没有往上推的话则悬停，显示"正在刷新..."
				if (state == REFRESHING && pullDownY <= refreshDist) {
					pullDownY = refreshDist;
					myTimer.cancel();
				}

			}
			if (pullDownY > 0)
				pullDownY -= MOVE_SPEED;
			
			if (pullDownY < 0) {
				// 已完成回弹
				pullDownY = 0;
				pullView.clearAnimation();
				// 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
				if (state != REFRESHING)
					changeState(INIT);
				myTimer.cancel();
				requestLayout();
			}
			
			//Log.d("handle", "handle");
			// 刷新布局,会自动调用onLayout
			requestLayout();
			// 没有拖拉或者回弹完成
			if (pullDownY == 0)
				myTimer.cancel();
		}

	};*/

	private  static  class MyHandler extends Handler{
		WeakReference<PullToRefreshLayout> weakReference ;
		public MyHandler(PullToRefreshLayout pullToRefreshLayout ){
			weakReference  = new WeakReference<PullToRefreshLayout>( pullToRefreshLayout) ;
		}
		@Override
		public void handleMessage(Message msg) {
			PullToRefreshLayout pl=weakReference.get();
			if (pl==null){
				removeCallbacksAndMessages(null);
				return;
			}
			// 回弹速度随下拉距离moveDeltaY增大而增大
			pl.MOVE_SPEED = (float) (8
					+ 5 * Math.tan(Math.PI / 2 / pl.getMeasuredHeight() * (pl.pullDownY )));
			if (!pl.isTouch) {
				// 正在刷新，且没有往上推的话则悬停，显示"正在刷新..."
				if (pl.state == REFRESHING && pl.pullDownY <= pl.refreshDist) {
					pl.pullDownY = pl.refreshDist;
					pl.myTimer.cancel();
				}

			}
			if (pl.pullDownY > 0)
				pl.pullDownY -= pl.MOVE_SPEED;

			if (pl.pullDownY < 0) {
				// 已完成回弹
				pl.pullDownY = 0;
				pl.pullView.clearAnimation();
				// 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
				if (pl.state != REFRESHING)
					pl.changeState(INIT);
				pl.myTimer.cancel();
				pl.requestLayout();
			}

			//Log.d("handle", "handle");
			// 刷新布局,会自动调用onLayout
			pl.requestLayout();
			// 没有拖拉或者回弹完成
			if (pl.pullDownY == 0)
				pl.myTimer.cancel();
		}

	}

	public void shutDown(){
		if (updateHandler!=null){
			updateHandler.removeCallbacksAndMessages(null);
			updateHandler=null;
		}
		if (myTimer!=null)myTimer.cancel();
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	public PullToRefreshLayout(Context context) {
		super(context);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		myTimer = new MyTimer(updateHandler);
		rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.reverse_anim);
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotating);
		// 添加匀速转动动画
		LinearInterpolator lir = new LinearInterpolator();
		rotateAnimation.setInterpolator(lir);
		refreshingAnimation.setInterpolator(lir);
	}

	private void hide() {
		myTimer.schedule(5);
	}

	/**
	 * 完成刷新操作，显示刷新结果。注意：刷新完成后一定要调用这个方法
	 */
	/**
	 * @param refreshResult
	 *            PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
	 */
	public void refreshFinish(int refreshResult) {
		if (refreshingView==null) {
			return;
		}
		refreshingView.clearAnimation();
		refreshingView.setVisibility(View.GONE);
		switch (refreshResult) {
		case SUCCEED:
			// 刷新成功
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText(R.string.refresh_succeed);
			refreshStateImageView.setBackgroundResource(R.drawable.load_succeed);
			break;
		case FAIL:
		default:
			// 刷新失败
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText(R.string.refresh_fail);
			refreshStateImageView.setBackgroundResource(R.drawable.load_failed);
			break;
		}
		if (pullDownY > 0) {
			// 刷新结果停留1秒
			new Handler() {
				@Override
				public void handleMessage(Message msg) {
					changeState(DONE);
					hide();
				}
			}.sendEmptyMessageDelayed(0, 1000);
		} else {
			changeState(DONE);
			hide();
		}
	}

	
	private void changeState(int to) {
		state = to;
		switch (state) {
		case INIT:
			// 下拉布局初始状态
			refreshStateImageView.setVisibility(View.GONE);
			refreshStateTextView.setText(R.string.pull_to_refresh);
			pullView.clearAnimation();
			pullView.setVisibility(View.VISIBLE);
			
			break;
		case RELEASE_TO_REFRESH:
			// 释放刷新状态
			refreshStateTextView.setText(R.string.release_to_refresh);
			pullView.startAnimation(rotateAnimation);
			break;
		case REFRESHING:
			// 正在刷新状态
			pullView.clearAnimation();
			refreshingView.setVisibility(View.VISIBLE);
			pullView.setVisibility(View.INVISIBLE);
			refreshingView.startAnimation(refreshingAnimation);
			refreshStateTextView.setText(R.string.refreshing);
			break;
	
		case DONE:
			// 刷新或加载完毕，啥都不做
			break;
		}
	}

	/**
	 * 不限制上拉或下拉
	 */
	private void releasePull() {
		canPullDown = true;
	}

	/*
	 * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
	 * 
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			lastY = downY;
			myTimer.cancel();
			mEvents = 0;
			releasePull();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_UP:
			// 过滤多点触碰
			mEvents = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mEvents == 0) {
				if (pullDownY > 0 || (((Pullable) pullableView).canPullDown() && canPullDown )) {
					// 可以下拉，正在加载时不能下拉
					// 对实际滑动距离做缩小，造成用力拉的感觉
					pullDownY = pullDownY + (ev.getY() - lastY) / radio;
					if (pullDownY < 0) {
						pullDownY = 0;
						canPullDown = false;
					}
					if (pullDownY > getMeasuredHeight())
						pullDownY = getMeasuredHeight();
					if (state == REFRESHING) {
						// 正在刷新的时候触摸移动
						isTouch = true;
					}
				} else
					releasePull();
			} else
				mEvents = 0;
			lastY = ev.getY();
			// 根据下拉距离改变比例
			radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * pullDownY));
			if (pullDownY > 0 )
				requestLayout();
			if (pullDownY > 0) {
				if (pullDownY <= refreshDist && (state == RELEASE_TO_REFRESH || state == DONE)) {
					// 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
					changeState(INIT);
				}
				if (pullDownY >= refreshDist && state == INIT) {
					// 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
					changeState(RELEASE_TO_REFRESH);
				}
			} 
			// 因为刷新和加载操作不能同时进行，所以pullDownY和pullUpY不会同时不为0，因此这里用(pullDownY +
			// Math.abs(pullUpY))就可以不对当前状态作区分了
			if (pullDownY  > 8) {
				// 防止下拉过程中误触发长按事件和点击事件
				ev.setAction(MotionEvent.ACTION_CANCEL);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (pullDownY >= refreshDist )
			// 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
			{
				isTouch = false;
			}
			if (state == RELEASE_TO_REFRESH) {
				changeState(REFRESHING);
				// 刷新操作
				if (mListener != null)
					mListener.onRefresh(this);
			} 
			hide();
		default:
			break;
		}
		// 事件分发交给父类
		super.dispatchTouchEvent(ev);
		return true;
	}



	/**
	 * @author chenjing 自动模拟手指滑动的task
	 * 
	 */
	private class AutoRefreshAndLoadTask extends AsyncTask<Integer, Float, String> {

		@Override
		protected String doInBackground(Integer... params) {
			while (pullDownY < 4 / 3 * refreshDist) {
				pullDownY += MOVE_SPEED;
				publishProgress(pullDownY);
				try {
					Thread.sleep(params[0]);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			changeState(REFRESHING);
			// 刷新操作
			if (mListener != null)
				mListener.onRefresh(PullToRefreshLayout.this);
			hide();
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			if (pullDownY > refreshDist)
				changeState(RELEASE_TO_REFRESH);
			requestLayout();
		}

	}

	/**
	 * 自动刷新
	 */
	public void autoRefresh() {
		AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
		task.execute(20);
	}

	
	private void initView() {
		// 初始化下拉布局
		pullView = refreshView.findViewById(R.id.pull_icon);
		refreshStateTextView = (TextView) refreshView.findViewById(R.id.state_tv);
		refreshingView = refreshView.findViewById(R.id.refreshing_icon);
		refreshStateImageView = refreshView.findViewById(R.id.state_iv);
		}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//Log.d("Test", "Test");
		if (!isLayout) {
			// 这里是第一次进来的时候做一些初始化
			refreshView = getChildAt(0);
			pullableView = getChildAt(1);
			
			isLayout = true;
			initView();
			refreshDist = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight()+8;
		}
		// 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分
		refreshView.layout(0, (int) pullDownY - refreshView.getMeasuredHeight(),
				refreshView.getMeasuredWidth(), (int) pullDownY);
		pullableView.layout(0, (int) pullDownY, pullableView.getMeasuredWidth(),
				(int) pullDownY + pullableView.getMeasuredHeight());
	}

	static class MyTimer {
		private Handler handler;
		private Timer timer;
		private MyTask mTask;

		public MyTimer(Handler handler) {
			this.handler = handler;
			timer = new Timer();
		}

		public void schedule(long period) {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
			mTask = new MyTask(handler);
			timer.schedule(mTask, 0, period);
		}

		public void cancel() {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
		}
		public void stop() {
			cancel();
			if (timer!=null)timer.cancel();
		}

	}
	private static class  MyTask extends TimerTask {
		private Handler handler;
		public MyTask(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			if (handler!=null)
				handler.obtainMessage().sendToTarget();
		}

	}
	/**
	 * 刷新加载回调接口
	 * 
	 * @author chenjing
	 * 
	 */
	public interface OnRefreshListener {
		/**
		 * 刷新操作
		 */
		void onRefresh(PullToRefreshLayout pullToRefreshLayout);

	}

}
