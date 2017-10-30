package com.gan.base.activity;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gan.base.R;
import com.gan.base.adapter.ContentAdapter;
import com.gan.base.pagers.ContentBasePager;
import com.gan.base.pagers.slide.FirstPager;
import com.gan.base.pagers.slide.SecondPager;
import com.gan.base.pagers.slide.ThirdPager;
import com.gan.base.view.DragLayout;
import com.gan.base.view.TabIndicatorView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gan on 2017/10/30.
 */
public class SlideMenuActivity extends BaseActivity{

    @BindView(R.id.dl)
    DragLayout dl;

    @BindView(R.id.lv)
    ListView lv;

    @BindView(R.id.btn_exit)
    Button btn_exit;

    @BindView(R.id.rb_home)
    TabIndicatorView rb_home;
    @BindView(R.id.rb_friend)
    TabIndicatorView rb_contact;
    @BindView(R.id.rb_me)
    TabIndicatorView rb_me;

    @BindView(R.id.vp_content)
    ViewPager mViewPager;
    private ArrayList<ContentBasePager> mPagerList;
    //public static AccountInfo accountInfo;// 账户信息
    private FirstPager firstPagerr;
    private SecondPager secondPager;
    public ThirdPager thirdPager;

    private String[] menutypes;
    private int[] menuimgs;
    private String fileCount;

    private MenuAdapter menuAdapter;

    @Override
    protected void afterView() {
        setToolBarVisible(false);//隐藏标题
        ButterKnife.bind(this);
        initView();
        initDragLayout();
    }

    private int currentPage = 0;// 当前选择界面

    /**
     * 主面板
     */
    private void initView() {
        menutypes = new String[]{"菜单1", "菜单2", "菜单3"};
        menuimgs = new int[]{R.drawable.icon_home_act, R.drawable.icon_home_act, R.drawable.icon_home_act};


        //1初始化

        rb_home.setTabTitle("第一页");
        rb_home.setTabIcon(R.drawable.icon_home, R.drawable.icon_home_act);
        rb_home.setTabSelected(true);
        rb_home.setTabUnreadCount(125);

        rb_contact.setTabTitle("第二页");
        rb_contact.setTabIcon(R.drawable.icon_home, R.drawable.icon_home_act);
        rb_contact.setWarn(true);

        rb_me.setTabTitle("第三页");
        rb_me.setTabIcon(R.drawable.icon_home, R.drawable.icon_home_act);
        rb_me.setTabUnreadCount(1);

        // 初始化3个子页面
        mPagerList = new ArrayList<ContentBasePager>();
        firstPagerr = new FirstPager(this);
        mPagerList.add(firstPagerr);
        secondPager = new SecondPager(this);
        mPagerList.add(secondPager);
        thirdPager = new ThirdPager(this);
        mPagerList.add(thirdPager);
        mViewPager.setAdapter(new ContentAdapter(this, mPagerList));


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.rb_home:
                        rb_home.setTabSelected(true);
                        rb_contact.setTabSelected(false);
                        rb_me.setTabSelected(false);
                        mViewPager.setCurrentItem(0, true);
                        break;
                    case R.id.rb_friend:
                        rb_home.setTabSelected(false);
                        rb_contact.setTabSelected(true);
                        rb_me.setTabSelected(false);
                        mViewPager.setCurrentItem(1, true);
                        break;
                    case R.id.rb_me:
                        rb_home.setTabSelected(false);
                        rb_contact.setTabSelected(false);
                        rb_me.setTabSelected(true);
                        mViewPager.setCurrentItem(2, true);// 设置当前页面false将可以去掉切换动画
                        break;
                    default:
                        break;
                }

            }
        };
        rb_home.setOnClickListener(listener);
        rb_contact.setOnClickListener(listener);
        rb_me.setOnClickListener(listener);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mPagerList.get(currentPage).outData();
                currentPage = arg0;
                mPagerList.get(arg0).initData();// 获取当前被选中的页面, 初始化该页面数据
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mPagerList.get(0).initData();// 初始化首页数据

    }

    /**
     * 侧滑面板
     */
    private void initDragLayout() {
        menuAdapter = new MenuAdapter();
        lv.setAdapter(menuAdapter);
        // dl.setSlideLeftWidthScale(1f);//设置侧滑面板宽度默认0.75
        // 设置左侧listview监听
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://
                        break;
                    case 1://
                        break;
                    case 2://
                        break;
                    default:
                        break;
                }

            }
        });
        // 侧滑动作监听
        dl.setDragListener(new DragLayout.DragListener() {
            @Override
            public void onOpen() {

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onDrag(float percent) {

            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_slide;
    }


    class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menutypes.length;
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(SlideMenuActivity.this, R.layout.menu_item, null);
            ImageView head = (ImageView) view.findViewById(R.id.menu_head_img);
            ImageView next = (ImageView) view.findViewById(R.id.menu_next_img);
            TextView title = (TextView) view.findViewById(R.id.menu_text_title);
            TextView content = (TextView) view.findViewById(R.id.menu_text_content);
            head.setImageResource(menuimgs[position]);
            title.setText(menutypes[position]);
            if (position != 2) {
                content.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
            } else {
                content.setText(fileCount);
            }
            return view;
        }

    }

    @Override
    public void onBackPressed() {
        if (dl.getStatus()== DragLayout.Status.Open){
            dl.close(true);
            return;
        }
        super.onBackPressed();
    }
}
