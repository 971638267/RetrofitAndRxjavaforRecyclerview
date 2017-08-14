package com.gan.base.pagers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gan.base.R;
import com.gan.base.activity.LoginActivity;
import com.gan.base.util.PrefUtils;
import com.gan.base.view.MyGridView;
import com.gan.base.view.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * 
 * @ClassName: MePager
 * @Description: 页面（个人中心）的实现
 * @author 甘玉飞
 * @date 2017年02月21日
 *
 */
public class MePager extends ContentBasePager implements PullToRefreshLayout.OnRefreshListener {
	@BindView(R.id.refresh_view)
	PullToRefreshLayout ptrl;
	@BindView(R.id.me_page_gd)
	MyGridView gridView;

	public MePager(AppCompatActivity activity) {
		super(activity);
		ButterKnife.bind(this, mRootView);
		initView();
	}
	private void initView() {
		ptrl.setOnRefreshListener(this);
		initGridView();
	}

	private void initGridView() {
		ArrayList<HashMap<String, Object>> gridData = new ArrayList<HashMap<String, Object>>();
		final int[] imageint = new int[4];
		imageint[0] = R.drawable.icon_network;
		imageint[1] = R.drawable.icon_network;
		imageint[2] = R.drawable.icon_network;
		imageint[3] = R.drawable.icon_network;
		String[] griditemtext = {"提现", "充值", "结算账户", "代收账户"};

		for (int i = 0; i < imageint.length; i++) {
			HashMap<String, Object> hash = new HashMap<String, Object>();
			hash.put("image", imageint[i]);
			hash.put("text", griditemtext[i]);
			gridData.add(hash);
		}

		MyGridAdapter adapter = new MyGridAdapter(mActivity, gridView, gridData);

		gridView.setAdapter(adapter);

		// 监听gridview点击事件
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
					default:
						break;
				}

			}

		});
	}

	/**
	 * 当进入该页面时调用
	 */
	@Override
	public void initData() {

	}
	/**
	 * 当离开该页面时调用
	 */
	@Override
	public void outData() {

	}

	@Override
	public int getContentView() {
		return R.layout.pager_me;
	}

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					if (ptrl!=null)
						ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
					break;
				default:
					break;
			}

		}
	};
	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		handler.sendEmptyMessageDelayed(1,3000);
	}

	@Override
	public void onDestroy() {
	if (ptrl!=null){
		ptrl.shutDown();
		ptrl=null;
	}
	super.onDestroy();
	}

	private class MyGridAdapter extends BaseAdapter {
		private Context context;
		private GridView mGv;
		private ArrayList<HashMap<String, Object>> lstDate;
		public MyGridAdapter(Context context, GridView gridView, ArrayList<HashMap<String, Object>> gridData) {
			this.context = context;
			this.mGv = gridView;
			this.lstDate = gridData;
		}

		@Override
		public int getCount() {
			return lstDate.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(context, R.layout.item_home_grid, null);
			ImageView iv = (ImageView) convertView.findViewById(R.id.home_grid_img);
			TextView tv = (TextView) convertView.findViewById(R.id.home_grid_text);
			iv.setImageResource(Integer.parseInt(lstDate.get(position).get("image").toString()));
			tv.setText(lstDate.get(position).get("text").toString());
			return convertView;
		}
	}

	@OnClick(R.id.me_exit)
	public void exit() {
		PrefUtils.setBoolean("isLogin", false);
		PrefUtils.SetString("tokenId", "");
		PrefUtils.SetInt("userId", 0);
		PrefUtils.SetInt("companyId", 0);
		mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
		mActivity.finish();
	}
}
