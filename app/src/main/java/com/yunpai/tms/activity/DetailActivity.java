package com.yunpai.tms.activity;

import android.widget.TextView;

import com.yunpai.tms.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用途: TODO
 * 创建者:ganyufei
 * 时间: 2017/3/20
 */
public class DetailActivity extends BaseActivity {
    @BindView(R.id.text)
    TextView textView;

    @Override
    protected int getContentView() {
        return R.layout.activity_detail;
    }

    @Override
    protected void afterView() {
        setTitle("请求结果");
        ButterKnife.bind(this);
        String str = getIntent().getStringExtra("str");
        textView.setText(str);
    }
}
