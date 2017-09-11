package com.gan.base.activity;

import android.view.View;

import com.gan.base.R;
import com.gan.base.net.rx.RxBus;
import com.gan.base.net.rx.RxCodeConstants;

/**
 * Created by gan on 2017/9/11.
 */
public class RxBusActivity extends BaseActivity{
    @Override
    protected int getContentView() {
        return R.layout.rxbus_activity;
    }

    @Override
    protected void afterView() {

    }
    public void jump(View  view){
        RxBus.getDefault().post(RxCodeConstants.JUMP_TYPE, "跳转到消息页");
        finish();
    }

}
