package com.gan.appdownload;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swain on 2016/8/1.
 * 版本更新的实现类
 */

public class VersionUpdateManager {

    private isDownloadSuccess onDownListener;
    private Context context;
    private ProgressDialog mDialog;
    private DownloadManager dowanloadmanager;
    private long lastDownloadId = 0;
    //"content://downloads/my_downloads"必须这样写不可更改
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private String title, content, data_path, url;
    private DownloadChangeObserver downloadObserver;
    private String[] str;
    private DownloadManager.Request request;

    public VersionUpdateManager(Context context, String title, String content, String datapath, String url) {
        this.context = context;
        this.title = title;
        this.content = content;
        this.data_path = datapath;
        this.url = url;
        if (url == null || url.length() <= 0) {
            Toast.makeText(context, "下载错误", Toast.LENGTH_SHORT).show();
            return;
        }
        createDialog();
        init();
    }

    private void createDialog() {
        mDialog = new ProgressDialog(context);
        mDialog.setProgress(0);
        mDialog.setTitle("版本升级");
        mDialog.setMessage("正在下载安装包，请稍候");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    void init() {
        //1.得到下载对象
        dowanloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //2.创建下载请求对象，并且把下载的地址放进去
        request = new DownloadManager.Request(Uri.parse(url));
        //3.给下载的文件指定路径
        str = url.split("/");
        request.setDestinationInExternalFilesDir(context, data_path, str[str.length - 1]);
        //4.设置显示在文件下载Notification（通知栏）中显示的文字。6.0的手机Description不显示
        request.setTitle(title);
        request.setDescription(content);
        //5更改服务器返回的minetype为android包类型
        request.setMimeType("application/vnd.android.package-archive");
        //6.设置在什么连接状态下执行下载操作
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //7. 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        //8. 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
    }

    public void DownloadStart() {
        delFile(data_path);
       /* //判断是否下载
        List<String> addressArr = queryDownLoad();
        if (addressArr != null && addressArr.size() > 0) {
            for (int i = 1; i < addressArr.size(); i++) {
                if (addressArr.get(i) != null && addressArr.get(i).length() > 0) {
                    String[] apkStr = addressArr.get(i).split("/");
                    if (str[str.length - 1].equals(apkStr[apkStr.length - 1])) {
                        Toast.makeText(context, "当前版本已下载！", Toast.LENGTH_SHORT).show();
                        if (null != onDownListener)
                            onDownListener.setDownloadDown();
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        Uri downloadFileUri = Uri.parse(addressArr.get(i));
                        install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(install);
                        System.exit(0);//退出程序
                        return;
                    }
                }

            }
        }*/
        mDialog.show();
        lastDownloadId = dowanloadmanager.enqueue(request);
        //9.保存id到缓存
        SharedPreferences sPreferences = context.getSharedPreferences("downloadplato", 0);
        sPreferences.edit().putLong("plato", lastDownloadId).commit();
        //10.采用内容观察者模式实现进度
        downloadObserver = new DownloadChangeObserver(null);
        context.getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
    }
    /**
     * 删除APP下的无用安装包
     */
    private void delFile(String path) {
        File file= context.getExternalFilesDir(data_path);
        if (file.exists()) {
            File[] array = file.listFiles();
            for (int i = 0; i < array.length; i++) {
                String name = array[i].getName();
                if (name.substring(name.lastIndexOf(".") + 1, name.length()).equals("apk"))
                    array[i].delete();

            }
        }
    }

    //用于显示下载进度
    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(boolean selfChange) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(lastDownloadId);
            DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final Cursor cursor = dManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                final int totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int totalSize = cursor.getInt(totalColumn);
                int currentSize = cursor.getInt(currentColumn);
                float percent = (float) currentSize / (float) totalSize;
                int progress = Math.round(percent * 100);
                mDialog.setProgress(progress);
                if (progress >= 100) {
                    mDialog.dismiss();
                    context=null;
                    if (null != onDownListener)
                        onDownListener.setDownloadDown();
                }
            }
        }


    }

    List<String> queryDownLoad() {
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = dowanloadmanager.query(query);
        List<String> addressArr = new ArrayList<>();
        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            addressArr.add(address);
        }
        cursor.close();
        return addressArr;
    }


    public void setDownLoadDown(isDownloadSuccess onDownListener) {
        this.onDownListener = onDownListener;
    }

    public interface isDownloadSuccess {
        void setDownloadDown();
    }
}
