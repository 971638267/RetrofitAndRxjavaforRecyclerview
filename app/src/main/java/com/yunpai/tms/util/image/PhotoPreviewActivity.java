package com.yunpai.tms.util.image;

import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunpai.tms.R;
import com.yunpai.tms.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gan on 2017/4/18.
 */

public class PhotoPreviewActivity extends BaseActivity {

    @BindView(R.id.photo_preview_head_pv)
    PhotoView mPhotoView;


    @Override
    protected int getContentView() {
        return R.layout.activity_photo_preview;
    }

    @Override
    protected void afterView() {
        ButterKnife.bind(this);
        setTitle("图片预览");
        mPhotoView.enable();
        String photoPath=getIntent().getStringExtra("photoPath");
        if (photoPath.contains("http") || photoPath.contains("drawable")) {
            ImageLoader.getInstance().displayImage(photoPath, mPhotoView);
        }
        else{
            ImageLoader.getInstance().displayImage("file://"+photoPath, mPhotoView);
        }
        mPhotoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
