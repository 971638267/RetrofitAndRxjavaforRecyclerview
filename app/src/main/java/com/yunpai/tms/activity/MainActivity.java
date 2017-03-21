package com.yunpai.tms.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yunpai.tms.BuildConfig;
import com.yunpai.tms.R;
import com.yunpai.tms.adapter.ContentAdapter;
import com.yunpai.tms.application.AppStackManager;
import com.yunpai.tms.net.apiexception.ApiException;
import com.yunpai.tms.pagers.ContentBasePager;
import com.yunpai.tms.pagers.HomePager;
import com.yunpai.tms.pagers.MePager;
import com.yunpai.tms.pagers.MessagePager;
import com.yunpai.tms.pagers.WaybillPager;
import com.yunpai.tms.util.KeyBoardUtils;
import com.yunpai.tms.util.ToastUtil;
import com.yunpai.tms.view.TabIndicatorView;
import com.zxing.decode.DecodeThread;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rb_home)
    TabIndicatorView rb_home;
    @BindView(R.id.rb_message)
    TabIndicatorView rb_message;
    @BindView(R.id.rb_waybill)
    TabIndicatorView rb_waybill;
    @BindView(R.id.rb_me)
    TabIndicatorView rb_me;

    @BindView(R.id.vp_content)
    ViewPager mViewPager;
    private ArrayList<ContentBasePager> mPagerList;
    private HomePager homePager;
    private MessagePager messagePager;
    private WaybillPager waybillPager;
    private MePager mePager;
    private int currentPage = 0;// 当前选择界面
    private long mExitTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //1初始化

        rb_home.setTabTitle(getResources().getString(R.string.home_pager_title));
        rb_home.setTabIcon(R.drawable.icon_home, R.drawable.icon_home_act);
        rb_home.setTabSelected(true);
        rb_home.setTabUnreadCount(125);

        rb_message.setTabTitle(getResources().getString(R.string.message_pager_title));
        rb_message.setTabIcon(R.drawable.icon_message, R.drawable.icon_mesaage_act);
        rb_message.setWarn(true);

        rb_waybill.setTabTitle(getResources().getString(R.string.waybill_pager_title));
        rb_waybill.setTabIcon(R.drawable.icon_waybill, R.drawable.icon_waybill_act);
        rb_waybill.setTabUnreadCount(1);

        rb_me.setTabTitle(getResources().getString(R.string.me_pager_title));
        rb_me.setTabIcon(R.drawable.icon_me, R.drawable.icon_me_act);
        rb_me.setTabUnreadCount(1);

        // 初始化3个子页面
        mPagerList = new ArrayList<ContentBasePager>();
        homePager = new HomePager(this);
        mPagerList.add(homePager);
        messagePager = new MessagePager(this);
        mPagerList.add(messagePager);
        waybillPager = new WaybillPager(this);
        mPagerList.add(waybillPager);
        mePager = new MePager(this);
        mPagerList.add(mePager);
        mViewPager.setAdapter(new ContentAdapter(this, mPagerList));
        mViewPager.setOffscreenPageLimit(4);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPageSwitch(v.getId());
            }
        };
        rb_home.setOnClickListener(listener);
        rb_message.setOnClickListener(listener);
        rb_waybill.setOnClickListener(listener);
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

    private void doPageSwitch(int id) {
        if (id == 0) return;
        switch (id) {
            case R.id.rb_home:
                rb_home.setTabSelected(true);
                rb_message.setTabSelected(false);
                rb_waybill.setTabSelected(false);
                rb_me.setTabSelected(false);
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.rb_message:
                rb_home.setTabSelected(false);
                rb_message.setTabSelected(true);
                rb_waybill.setTabSelected(false);
                rb_me.setTabSelected(false);
                mViewPager.setCurrentItem(1, true);
                break;
            case R.id.rb_waybill:
                rb_home.setTabSelected(false);
                rb_message.setTabSelected(false);
                rb_waybill.setTabSelected(true);
                rb_me.setTabSelected(false);
                mViewPager.setCurrentItem(2, true);// 设置当前页面false将可以去掉切换动画
                break;
            case R.id.rb_me:
                rb_home.setTabSelected(false);
                rb_message.setTabSelected(false);
                rb_waybill.setTabSelected(false);
                rb_me.setTabSelected(true);
                mViewPager.setCurrentItem(3, true);// 设置当前页面false将可以去掉切换动画
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppStackManager.getInstance().addActivity(this);//加入栈
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        for (ContentBasePager b:mPagerList){
            b.onDestroy();
        }
        AppStackManager.getInstance().finishActivity(this);
        AppStackManager.getInstance().finishAllActivity();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            // 扫码获取运单信息
            Bundle extras = data.getExtras();
            if (null != extras){

               /* int width = extras.getInt("width");
                int height = extras.getInt("height");

                LayoutParams lps = new LayoutParams(width, height);
                lps.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                lps.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                lps.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

                mResultImage.setLayoutParams(lps);

                String result = extras.getString("result");
                mResultText.setText(result);

                Bitmap barcode = null;
                byte[] compressedBitmap = extras.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    // Mutable copy:
                    barcode = barcode.copy(Bitmap.Config.RGB_565, true);
                }

                mResultImage.setImageBitmap(barcode);
*/

                byte[] compressedBitmap = extras.getByteArray(DecodeThread.BARCODE_BITMAP);
                String result = extras.getString("result");
                doSearchWayBill(result);



            // 判断是否是自己的二维码
            /*try{

            }catch (Exception e ){
                e.printStackTrace();
                // TODO
                final Dialog dialogmask = new Dialog(this, R.style.customDialog);
                View dialog = View.inflate(this, R.layout.dialog_msg, null);
                dialogmask.setContentView(dialog);
                dialogmask.show();
                TextView contengt = (TextView) dialog.findViewById(R.id.dialog_title);
                String err="错误:" + e.getMessage()+"；扫码结果:" + result;
                contengt.setText(err);
                Button ok = (Button) dialog.findViewById(R.id.dialog_ok_msg);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogmask.dismiss();
                    }
                });

            }*/
            }
        } else {
            if (homePager != null) homePager.onActivityResult(requestCode, resultCode, data);
            if (waybillPager != null) waybillPager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 订单查询操作
     */
    private void doSearchWayBill(String result) {
        final Dialog dialogmask = new Dialog(this, R.style.customDialog);
        View dialog = View.inflate(this, R.layout.dialog_msg, null);
        dialogmask.setContentView(dialog);
        dialogmask.show();
        TextView contengt = (TextView) dialog.findViewById(R.id.dialog_title);
        String err="扫码结果:" + result;
        contengt.setText(err);
        Button ok = (Button) dialog.findViewById(R.id.dialog_ok_msg);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogmask.dismiss();
            }
        });

    }

    /**
     * 页面跳转
     *
     * @param postion
     */
    public void goToPage(int postion) {

        int id = 0;
        switch (postion) {
            case 0:
                id = R.id.rb_home;
                break;
            case 1:
                id = R.id.rb_message;
                break;
            case 2:
                id = R.id.rb_waybill;
                break;
            case 3:
                id = R.id.rb_me;
                break;
            default:
                break;
        }
        doPageSwitch(id);
    }


    /**
     * 监听返回键 点击2次退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 点击两次返回键退出APP
     */
    private void closeActivity() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtil.ToastCenter("再按一次退出程序");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
