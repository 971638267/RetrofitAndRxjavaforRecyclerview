package com.gan.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;

import com.gan.base.R;
import com.gan.base.util.ToastUtil;
import com.gan.base.util.Utils;
import com.gan.base.util.dialog.CameraDialog;
import com.gan.base.util.dialog.FileInfo;
import com.gan.base.util.dialog.MyImgGridAdapter;
import com.gan.base.util.image.PhotoPreviewActivity;
import com.gan.base.util.image.PhotoPreviewMoreActivity;
import com.gan.base.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gan on 2017/5/18.
 */
public class PhotoPickActivity extends BaseActivity implements CameraDialog.OnUrlBackLinstener{
    @BindView(R.id.mygridview)
    MyGridView gridView;


    @Override
    protected int getContentView() {
        return R.layout.activity_hotoppick;
    }

    @Override
    protected void afterView() {
        setTitle("图片选择");
        ButterKnife.bind(this);
        initGridView();
    }

    final ArrayList<String> paths = new ArrayList<String>();
    MyImgGridAdapter adapter;
    private CameraDialog cDialog ;
    private String picPath;
    private final int MAX_PHOTOS = 5;// 最大上传图片张数
    /**
     * 初始化图片选择grid
     */
    private void initGridView() {
        paths.clear();
        paths.add("default");
        adapter = new MyImgGridAdapter(this, paths,MAX_PHOTOS);
        gridView.setAdapter(adapter);
        adapter.setDelListener(new MyImgGridAdapter.DelListener() {
            @Override
            public void del(int postion) {
                paths.remove(postion);
                adapter.refresh(paths);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == paths.size() - 1) {
                    //如果是最后一张弹出图片选择方式选择框
                    showChooseImgDialog();//
                } else {
                    //预览图片
                    startActivity(new Intent(PhotoPickActivity.this, PhotoPreviewMoreActivity.class).putExtra("photoPaths",paths).putExtra("position",position));

                }
            }
        });

    }


    /**
     *
     * 选择图片上传的方式
     */
    private void showChooseImgDialog() {
        if(cDialog==null){
            cDialog=new CameraDialog(this);
            cDialog.setDate(new ArrayList<FileInfo>(), 1, 1, false);
            cDialog.setOnUrlBackLinstener(this);
        }
        cDialog.show();
    }

    /**
     * 相机拍照的回调
     * @param path
     */
    @Override
    public void setOnPhotoBack(String path) {
        this.picPath=path;
    }

    @Override
    public void setOnVideoBack(String path) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != cDialog && cDialog.isShowing())
            cDialog.dismiss();
        if (resultCode == Activity.RESULT_OK) {
            doPhoto(requestCode, data);
        }

    }
    /**
     * 处理返回的图片信息
     */
    private void doPhoto(int requestCode, Intent data) {
        Uri photoUri=null;
        if (requestCode == 1001) {// 从相册取图片，有些手机有异常情况，请注意

            if (data == null) {
                ToastUtil.ToastCenter("选择图片文件出错1");
                return;
            }
            photoUri = data.getData();
            if (photoUri == null) {
                ToastUtil.ToastCenter("选择图片文件出错2");
                return;
            }
        }else if (requestCode == 1003) {//从拍照获取
            Uri uri=null;
            if (data != null && data.getData() != null) {
                uri = data.getData();
            }
            // 一些机型无法从getData中获取uri，则需手动指定拍照后存储照片的Uri
            if (uri != null) {
                photoUri=uri;
            }
        }
        if (photoUri!=null) {
            //根据uri获取图片本地路径
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor actualimagecursor = managedQuery(photoUri,proj,null,null,null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            picPath = actualimagecursor.getString(actual_image_column_index);
        }
        if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg")|| picPath.endsWith(".JPG"))) {
            if (!Utils.fileIsExists(picPath))
                return;
            paths.add(paths.size()-1,picPath);
            adapter.refresh(paths);
            // ImageLoader.getInstance().displayImage("file://"+picPath,selectPicWaitIv);
        } else {
            ToastUtil.ToastCenter("选择图片文件不正确");
        }
        picPath=null;
        return;

    }
}
