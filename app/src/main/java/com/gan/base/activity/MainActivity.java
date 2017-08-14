package com.gan.base.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.gan.apkdownloadlibrary.VersionUpdateManager;
import com.gan.base.R;
import com.gan.base.adapter.ContentAdapter;
import com.gan.base.application.AppStackManager;
import com.gan.base.constant.Constant;
import com.gan.base.net.resultbean.VerSionInfo;
import com.gan.base.net.subscribers.ProgressSubscriber;
import com.gan.base.net.subscribers.SubscriberOnNextListener;
import com.gan.base.pagers.ContentBasePager;
import com.gan.base.pagers.HomePager;
import com.gan.base.pagers.MePager;
import com.gan.base.pagers.MessagePager;
import com.gan.base.util.PermissionUtils;
import com.gan.base.util.ToastUtil;
import com.gan.base.util.Utils;
import com.gan.base.view.MyDialogSimple;
import com.gan.base.view.TabIndicatorView;
import com.zxing.decode.DecodeThread;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rb_home)
    TabIndicatorView rb_home;
    @BindView(R.id.rb_message)
    TabIndicatorView rb_message;
    @BindView(R.id.rb_me)
    TabIndicatorView rb_me;

    @BindView(R.id.vp_content)
    ViewPager mViewPager;
    private ArrayList<ContentBasePager> mPagerList;
    private HomePager homePager;
    private MessagePager messagePager;
    private MePager mePager;
    private int currentPage = 0;// 当前选择界面
    private long mExitTime = 0;
    private MyDialogSimple mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        createDialog();//更新的弹窗
        getVersion4Service();

    }


    String updatePath;
    /**
     * 获取版本信息
     */
    private void getVersion4Service() {
        SubscriberOnNextListener listener=new SubscriberOnNextListener<VerSionInfo>() {
            @Override
            public void onNext(VerSionInfo o) {
                //获取版本信息成功
                //比较版本号是否过期

                if (o==null)
                    return;
                if (Utils.getVersion(MainActivity.this, true).compareTo(o.version) < 0) {
                    Constant.UPLOAD_VERSION = true;
                    updatePath= o.url;
                    mDialog.setSimpleShow();
                } else {
                    Constant.UPLOAD_VERSION = false;
                }

            }
        };
        ProgressSubscriber subscriberVersion= new ProgressSubscriber(listener,this,true,false);
        // NetWorks.getInstance().getVersionInfo(subscriberVersion,new  BaseRequest());
    }

    /**
     * 初始化弹窗
     */
    private void createDialog() {
        mDialog=new MyDialogSimple(this);
        mDialog.setSimpleDialog(0, getString(R.string.str_version), getString(R.string.str_new_version_msg), getString(R.string.str_update_now), getString(R.string.str_tell_late));
        mDialog.setSimpleDialogLinstener(new MyDialogSimple.setSimpleDialog() {
            @Override
            public void setSimpleDialogYes(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!Utils.isFastClickLongTime()) {
                    setAPKDownLoad(updatePath);
                }
            }

            @Override
            public void setSimpleDialogNo(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 版本下载
     */
    void setAPKDownLoad(String url) {
        VersionUpdateManager manager = new VersionUpdateManager(this, getString(R.string.app_name), getString(R.string.str_new_version_msg), Constant.DOWNLOAD_APK, url);
        manager.DownloadStart();
    }



    private void initView() {

        //1初始化
        rb_home.setTabTitle(getResources().getString(R.string.home_pager_title));
        rb_home.setTabIcon(R.drawable.icon_home, R.drawable.icon_home_act);
        rb_home.setTabSelected(true);
        rb_home.setTabUnreadCount(0);

        rb_message.setTabTitle(getResources().getString(R.string.message_pager_title));
        rb_message.setTabIcon(R.drawable.icon_message, R.drawable.icon_mesaage_act);
        rb_message.setWarn(false);

        rb_me.setTabTitle(getResources().getString(R.string.me_pager_title));
        rb_me.setTabIcon(R.drawable.icon_me, R.drawable.icon_me_act);
        rb_me.setTabUnreadCount(0);

        // 初始化3个子页面
        mPagerList = new ArrayList<ContentBasePager>();
        homePager = new HomePager(this);
        mPagerList.add(homePager);
        messagePager = new MessagePager(this);
        mPagerList.add(messagePager);
        mePager = new MePager(this);
        mPagerList.add(mePager);
        mViewPager.setAdapter(new ContentAdapter(this, mPagerList));
        mViewPager.setOffscreenPageLimit(3);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPageSwitch(v.getId());
            }
        };
        rb_home.setOnClickListener(listener);
        rb_message.setOnClickListener(listener);
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
                rb_me.setTabSelected(false);
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.rb_message:
                rb_home.setTabSelected(false);
                rb_message.setTabSelected(true);
                rb_me.setTabSelected(false);
                mViewPager.setCurrentItem(1, true);
                break;

            case R.id.rb_me:
                rb_home.setTabSelected(false);
                rb_message.setTabSelected(false);
                rb_me.setTabSelected(true);
                mViewPager.setCurrentItem(2, true);// 设置当前页面false将可以去掉切换动画
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
                Bitmap barcode = null;
                byte[] compressedBitmap = extras.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    // Mutable copy:
                    barcode = barcode.copy(Bitmap.Config.RGB_565, true);
                }

                mResultImage.setImageBitmap(barcode);
*/

                Bitmap barcode = null;
                byte[] compressedBitmap = extras.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    // Mutable copy:
                    barcode = barcode.copy(Bitmap.Config.RGB_565, true);
                }
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
        }
    }

    /**
     * 订单查询操作
     */
    private void doSearchWayBill(String result) {

        ToastUtil.ToastCenter("扫描结果:"+result);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
