package com.gan.base.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gan.base.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用途: 广告详情
 * 时间: 2017/2/28
 */
public class BaseWebViewActivity extends BaseActivity implements DownloadListener {
    @Override
    protected int getContentView() {
        return R.layout.activity_base_webview;
    }


    @BindView(R.id.pg_webview)
    ProgressBar pgWebview;
    @BindView(R.id.wv_url)
    WebView wuUrl;
    @BindView(R.id.iv_loss)
    ImageView ivLoss;

    String title, url;

    @Override
  public  void afterView() {

        title=getIntent().getStringExtra("title");
        url=getIntent().getStringExtra("url");
        ButterKnife.bind(this);
        setTitle(title);
        WebSettings s = wuUrl.getSettings();
        s.setBuiltInZoomControls(false);
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setSaveFormData(true);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        wuUrl.requestFocus();
        wuUrl.loadUrl(url);
        wuUrl.setDownloadListener(this);
        wuUrl.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urls) {
                url = urls;
                wuUrl.loadUrl(url);
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                wuUrl.setVisibility(View.GONE);
                ivLoss.setVisibility(View.VISIBLE);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                doPageChangeFinish(view, url);
            }
        });
        wuUrl.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pgWebview.setProgress(newProgress);
                if (newProgress == 100) {
                    pgWebview.setVisibility(View.GONE);
                }
                else {
                    pgWebview.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 点击网页中自媒体条目调用
     * url 当前页面的url
     */
    public void doPageChangeFinish(WebView webview, String url) {
        //1.获取当前页的标题以及返回上一级页面时获取当前页面的标题
        setTitle(webview.getTitle());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.iv_loss)
    void ivLoss() {
        wuUrl.setVisibility(View.VISIBLE);
        ivLoss.setVisibility(View.GONE);
        wuUrl.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && wuUrl.canGoBack()) {
            wuUrl.goBack();// 返回前一个页面
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && !wuUrl.canGoBack()) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
