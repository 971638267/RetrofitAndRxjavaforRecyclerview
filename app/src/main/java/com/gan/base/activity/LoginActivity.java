package com.gan.base.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;


import com.gan.base.R;
import com.gan.base.net.networks.NetWorks;
import com.gan.base.net.resultbean.UserInfoResult;
import com.gan.base.net.subscribers.ProgressSubscriber;
import com.gan.base.net.subscribers.SubscriberOnNextListener;
import com.gan.base.net.utils.MD5;
import com.gan.base.util.PrefUtils;
import com.gan.base.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.login_username)
    EditText userNameEt;
    @BindView(R.id.login_password)
    EditText passWordEt;
    private SubscriberOnNextListener onNextLisenter;
    private String companyCode;
    private String loginName;
    private String loginPwd;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void afterView() {
        ButterKnife.bind(this);
        setToolBarVisible(false);
        initLisenter();
       // init();
    }

    private void init() {

        if (PrefUtils.getBoolean("isLogin", false)) {//如果之前已经登录自动登录
              gotoMain();
        }

    }

    /**
     * 初始化网络请求监听
     */
    private void initLisenter() {
        onNextLisenter = new SubscriberOnNextListener<UserInfoResult>() {
            @Override
            public void onNext(UserInfoResult o) {
                storeUserInfo(o);
                gotoMain();
            }
        };
    }

    /**
     * 跳到主页面
     */
    private void gotoMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    /**
     * 用户信息本地初始化到本地
     */
    private void storeUserInfo(UserInfoResult info) {
        PrefUtils.setBoolean("isLogin", true);
      /*  PrefUtils.SetString(this,"companyCode",companyCode);
        PrefUtils.SetString(this,"loginName",loginName);
        PrefUtils.SetString(this,"loginPwd",loginPwd);*/
        PrefUtils.SetString("tokenId", info.getTokenId());
        PrefUtils.SetInt("userId", info.getUserId());
        PrefUtils.SetInt("companyId", info.getCompanyId());

    }



   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);//加载menu文件到布局
        return true;
    }*/

    public void login(View v) {
        if (checkOk()) {
            loginName = userNameEt.getText().toString().trim();
            loginPwd = MD5.md5(passWordEt.getText().toString().trim());
            NetWorks.getInstance().postLogin(new ProgressSubscriber<UserInfoResult>(onNextLisenter, this, true, false), loginName, loginPwd);
        }

    }

    /**
     * 验证通过
     *
     * @return
     */
    private boolean checkOk() {
        if (TextUtils.isEmpty(userNameEt.getText().toString().trim())) {
            ToastUtil.ToastCenter("账户不能为空");
            return false;
        }
        if (TextUtils.isEmpty(passWordEt.getText().toString().trim())) {
            ToastUtil.ToastCenter("密码不能为空");
            return false;
        }
        return true;
    }
}
