package com.gan.base.util.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.gan.base.R;
import com.gan.base.constant.Constant;
import com.gan.base.util.PermissionUtils;

import java.io.File;
import java.util.List;

/**
 * Created by gan on 2017/4/18.
 */

public class CameraDialog extends Dialog implements View.OnClickListener {
    private Activity context;
    private Button btPhoto, btCarema,btnCancel;
    private String capturePath = null;
    private int MAX_CHOOSE = 9;
    private int NEED_CHOOSE = 9;
    private boolean addShow;
    private List<FileInfo> mListInfo;
    private OnUrlBackLinstener onClick;
    //private PermissionUtils pUtils;


    public CameraDialog(Context context) {
        this(context, R.style.MyDialogStyleBottom);
        this.context = (Activity) context;

    }

    public CameraDialog(Context context, boolean carema, boolean video, boolean packUp, boolean photo, Activity mActivity) {
        this(context, R.style.MyDialogStyleBottom);
        this.context = mActivity;

    }

    public CameraDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = (Activity) context;
    }

    protected CameraDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_img);
        btPhoto = (Button) findViewById(R.id.choose_by_local);
        btCarema = (Button) findViewById(R.id.choose_by_camera);
        btnCancel = (Button) findViewById(R.id.dialog_cancel);
        btPhoto.setOnClickListener(this);
        btCarema.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }



    /**
     * 设置数据传递给下一个
     */
    public void setDate(List<FileInfo> mListInfo, int MAX_CHOOSE, int NEED_CHOOSE, boolean addShow) {
        this.mListInfo = mListInfo;
        this.MAX_CHOOSE = MAX_CHOOSE;
        this.NEED_CHOOSE = NEED_CHOOSE;
        this.addShow = addShow;
    }

    @Override
    public void onClick(View v) {


        if (NEED_CHOOSE < 1) {
            Toast.makeText(context, "已经达到选择的最大上限", Toast.LENGTH_SHORT).show();
            this.dismiss();
            return;
        }
        if (v == btPhoto) {
            /*Intent intent = new Intent(context, PhotoAndVideoActivity_.class);
            intent.putExtra("type", "0");
            intent.putExtra("maxchoose", MAX_CHOOSE);
            intent.putExtra("addshow", addShow);
            intent.putExtra("urlList", ListSheft.SceneList2String(mListInfo));
            context.startActivityForResult(intent, 1001);*/
            if (PermissionUtils.getInstance(context).Storage())
                return;
            pickImage(context, 1001);
        }  else if (v == btCarema) {
            if (PermissionUtils.getInstance(context).Camer()){
                return;
            }
            if (PermissionUtils.getInstance(context).Storage()){
                return;
            }
            getImageFromCamera(1003);
        }else if (v==btnCancel) {
            cancel();
            dismiss();
        }

    }

    public void pickImage(Activity activity, int requestCode) {
        activity.startActivityForResult(getImagePicker(), requestCode);
    }

    private Intent getImagePicker() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    /**
     * 开启相机
     *
     * @param actionCode 请求码
     */
    protected void getImageFromCamera(int actionCode) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            //获取保存的路径
            String out_file_path = Constant.IMG_PATH;
            File dir = new File(out_file_path);
            //给成员变量赋值
            capturePath = out_file_path+System.currentTimeMillis() + ".jpg";

            if (!dir.exists()) {
                dir.mkdirs();
            }

            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
            getImageByCamera.putExtra(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            if (null != onClick)
                onClick.setOnPhotoBack(capturePath);

            context.startActivityForResult(getImageByCamera, actionCode);
        } else {
            Toast.makeText(context, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取存储路径，可以写在FileUtils中
     */
 /*   public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }*/


    /**
     * 设置窗口背景颜色
     */
    private void setWindowBackground(float f) {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = f;
        context.getWindow().setAttributes(lp);
    }

    @Override
    public void show() {
        super.show();
        setWindowBackground(0.3f);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setWindowBackground(1f);
    }

    public void setOnUrlBackLinstener(OnUrlBackLinstener onClick) {
        this.onClick = onClick;
    }

    public interface OnUrlBackLinstener {
        void setOnPhotoBack(String path);

        void setOnVideoBack(String path);
    }
}

