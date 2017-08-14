package com.gan.base.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.gan.base.R;
import com.gan.base.application.AppStackManager;
import com.gan.base.util.PermissionUtils;


public abstract class BaseActivity extends AppCompatActivity {
    public ViewGroup contentView;//中间布局
    public Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        //设置toolbar
        // toolbar.setLogo(R.drawable.ic_launcher);
        //toolBar.setTitle("标题");
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(toolBar);
        //设置点击左侧按钮（菜单或者返回）
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        contentView = (ViewGroup) findViewById(R.id.base_contentview);
        contentView.addView(View.inflate(this, getContentView(), null));
        afterView();
    }
    //获取中间布局
    protected abstract int getContentView();

    /**
     * 布局设置以后
     */
    protected abstract void afterView();

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (toolBar != null) {
            getSupportActionBar().setTitle(title);
        }
    }
    /**
     * 设置TitleBar是否显示
     * @param visible
     */
    public void setToolBarVisible(Boolean visible) {
        if (toolBar!=null) {
            if (visible) {
                toolBar.setVisibility(View.VISIBLE);
            }else{
                toolBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStart() {
        AppStackManager.getInstance().addActivity(this);//加入栈
        super.onStart();
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
        AppStackManager.getInstance().finishActivity(this);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
