package com.gan.base.util.image;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.gan.base.R;
import com.gan.base.activity.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gan on 2017/4/18.
 */

public class PhotoPreviewMoreActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @Override
    protected int getContentView() {
        return R.layout.activity_photo_preview_more;
    }

    @Override
    protected void afterView() {
        ButterKnife.bind(this);
        setTitle("图片预览");

       final ArrayList<String> photoPaths=getIntent().getStringArrayListExtra("photoPaths");
       int position=getIntent().getIntExtra("position",0);
        if (photoPaths.contains("default"))photoPaths.remove("default");
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return photoPaths.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
               ViewGroup viewGroup= (ViewGroup) View.inflate(PhotoPreviewMoreActivity.this,R.layout.pager_img,null);
              PhotoView  mPhotoView= (PhotoView) viewGroup.findViewById(R.id.photo_preview_head_pv);
                mPhotoView.enable();
                mPhotoView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                String pic=photoPaths.get(position);
                if (pic.startsWith("http") || pic.contains("drawable")) {
                    ImageLoader.getInstance().displayImage(pic, mPhotoView,ImageLoaderConfig.initNoScaleDisplayOptions(true));
                }
                else{
                    ImageLoader.getInstance().displayImage("file://"+pic, mPhotoView,ImageLoaderConfig.initNoScaleDisplayOptions(true));
                }
                container.addView(viewGroup);
                return viewGroup;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.setCurrentItem(position);
    }
}
