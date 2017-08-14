package com.gan.base.util.dialog;

import java.io.Serializable;

/**
 * Created by gan on 2017/4/18.
 */

public class FileInfo implements Serializable {
    public static final long serialVersionUID = 1L;
    private String path;
    //0:本地照片 1.本地视频 2.网上照片 3.网上视频,4.默认添加的图片
    private int type;
    //0.上传失败  1.上传成功 2.未上传 3.正在上传
    private String type2 = "2";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }
}

