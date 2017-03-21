package com.yunpai.tms.pagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunpai.tms.R;
import com.yunpai.tms.activity.DetailActivity;
import com.yunpai.tms.activity.MainActivity;
import com.yunpai.tms.banner.Banner;
import com.yunpai.tms.banner.BannerData;
import com.yunpai.tms.net.networks.NetWorks;
import com.yunpai.tms.net.resultbean.Subject;
import com.yunpai.tms.net.subscribers.ProgressSubscriber;
import com.yunpai.tms.net.subscribers.RecycleviewSubscriber;
import com.yunpai.tms.net.subscribers.RecycleviewSubscriberOnNextListener;
import com.yunpai.tms.net.subscribers.SubscriberOnNextListener;
import com.yunpai.tms.util.DensityUtils;
import com.yunpai.tms.util.KeyBoardUtils;
import com.yunpai.tms.util.ToastUtil;
import com.zxing.activity.CaptureZxingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author 甘玉飞
 * @ClassName: HomePager
 * @Description: 页面（首页）的实现
 * @date 2017年02月21日
 */
public class HomePager extends ContentBasePager {

    // 轮播器数据列表
    private List<BannerData> bannerList;
    //表格菜单
    @BindView(R.id.home_grid)
    GridView gridView;
    @BindView(R.id.home_top_vp)
    Banner banner;


    public HomePager(AppCompatActivity activity) {
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
        return R.layout.pager_home;
    }


    private void initView() {
        initBanner();
        initGridview();
    }


    /**
     * 初始化gridView
     */
    private void initGridview() {
        ArrayList<HashMap<String, Object>> gridData = new ArrayList<HashMap<String, Object>>();
        final int[] imageint = new int[21];
        for (int i = 0; i < 21; i++) {
            imageint[i] = R.drawable.icon_network;
        }
        String[] griditemtext = new String[21];
        for (int i = 0; i < 21; i++) {
            griditemtext[i] = "功能" + (i + 1);
        }

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
                /*switch (position) {
                    case 0://
                        break;
                    case 1://
                        break;
                    case 2://
                        break;
                    case 3://
                        break;
                    case 4://

                        break;
                    case 5://
                        break;
                    case 6://
                        break;
                    case 7://
                        break;
                    case 8://
                        break;

                    default:
                        break;
                }*/
                SubscriberOnNextListener<List<Subject>> getTopMovieOnNext = new SubscriberOnNextListener<List<Subject>>() {
                    @Override
                    public void onNext(List<Subject> subjects) {
                        ToastUtil.ToastCenter("请求数据成功！");
                        Intent it=new Intent(mActivity,DetailActivity.class);
                        it.putExtra("str",subjects.toString());
                        mActivity.startActivity(it);
                    }
                };
                ProgressSubscriber<List<Subject>> subscriber = new ProgressSubscriber<List<Subject>>(getTopMovieOnNext, mActivity, true, true);
                NetWorks.getInstance().Test250(subscriber, 0, 10);
            }

        });
    }

    /**
     * 轮播器初始化
     */
    private void initBanner() {
        bannerList = new ArrayList<BannerData>();
        String[] urls = new String[]{
                "drawable://" + R.drawable.banner1,
                "drawable://" + R.drawable.banner1,
                "drawable://" + R.drawable.banner1
        };
        for (int i = 0; i < urls.length; i++) {
            BannerData d = new BannerData();
            d.setImage(urls[i]);
            //d.setTitle("测试tile" + i);
            d.setId(i);
            bannerList.add(d);
        }

        Banner.ImageCycleViewListener mAdCycleViewListener = new Banner.ImageCycleViewListener() {

            @Override
            public void onImageClick(int position, View imageView) {
                // 单击图片处理事件
                // ToastUtil.ToastCenter("position" + position + "title:" + bannerList.get(position).getTitle());
            }

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                ImageLoader.getInstance().displayImage(imageURL, imageView);// 此处本人使用了ImageLoader对图片进行加装！
            }
        };
        banner.setImageResources(bannerList, mAdCycleViewListener);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    /**
     * 自定义适配器，能够自动调整表格高度
     *
     * @author 甘玉飞
     * @ClassName: MyGridAdapter
     * @Description:
     * @date 2016年6月27日 下午4:41:53
     */
    class MyGridAdapter extends BaseAdapter {

        private Context context;
        private GridView mGv;
        private ArrayList<HashMap<String, Object>> lstDate;
        // private int row;

        public MyGridAdapter(Context mContext, GridView gv, ArrayList<HashMap<String, Object>> list) {
            this.context = mContext;
            this.mGv = gv;
            this.lstDate = list;
            // this.row=row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(context, R.layout.item_home_grid, null);
            // 高度计算

            int heigt1 = mGv.getMeasuredWidth() / mGv.getNumColumns();
            int heigt2 = (mGv.getMeasuredHeight() - DensityUtils.dp2px(mActivity, 1) * 2) / 3;
            int heigt = 0;
            if (heigt1 >= heigt2) {
                if (heigt1 > heigt2 * 6 / 5) {
                    heigt = heigt1;
                } else {
                    heigt = heigt2;
                }
            } else {
                heigt = heigt2;
            }

            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, heigt);
            ImageView iv = (ImageView) convertView.findViewById(R.id.home_grid_img);
            TextView tv = (TextView) convertView.findViewById(R.id.home_grid_text);
            iv.setImageResource(Integer.parseInt(lstDate.get(position).get("image").toString()));
            tv.setText(lstDate.get(position).get("text").toString());
            convertView.setLayoutParams(param);

            return convertView;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public int getCount() {
            return lstDate.size();
        }

    }
}
