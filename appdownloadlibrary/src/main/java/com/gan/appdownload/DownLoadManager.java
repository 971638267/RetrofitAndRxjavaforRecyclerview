package com.gan.appdownload;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swain on 2016/6/23.
 */

public class DownLoadManager {

    private DownloadManager dManager;
    private Context context;


    public DownLoadManager(Context context) {
        this.context = context;
        dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }


    public void requestDownLoad(String title, String content, String datapath, String url) {
        String[] str = url.split("/");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(title);
        request.setDescription(content);
        request.setDestinationInExternalPublicDir(datapath, str[str.length - 1]);
        request.setMimeType("application/vnd.android.package-archive");
        List<String> addressArr = queryDownLoad();
        if (addressArr != null && addressArr.size() > 0) {
            for (int i = 0; i < addressArr.size(); i++) {
                String[] apkStr = addressArr.get(i).split("/");
                if (str[str.length - 1].equals(apkStr[apkStr.length - 1])) {
                    Toast.makeText(context, "当前版本已下载！", Toast.LENGTH_SHORT).show();
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    Uri downloadFileUri = Uri.parse(addressArr.get(i));
                    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
                    return;
                }
            }
        }
        Toast.makeText(context, "正在下载...！", Toast.LENGTH_SHORT).show();
        startDownLoad(request);
    }

    /**
     * 主动下载
     */
    public void requestDownLoads(String title, String content, String datapath, String url) {
        String[] str = url.split("/");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(title);
        request.setDescription(content);
        request.setDestinationInExternalPublicDir(datapath, str[str.length - 1]);
        request.setMimeType("application/vnd.android.package-archive");
        delFile(datapath);
        startDownLoad(request);
    }

    void startDownLoad(DownloadManager.Request request) {
        long refernece = dManager.enqueue(request);
        // 把当前下载的ID保存起来
        SharedPreferences sPreferences = context.getSharedPreferences("downloadplato", 0);
        sPreferences.edit().putLong("plato", refernece).commit();
    }


    List<String> queryDownLoad() {
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = dManager.query(query);
        List<String> addressArr = new ArrayList<>();
        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            addressArr.add(address);
        }
        cursor.close();
        return addressArr;
    }

    /**
     * 获取文件列表 暂时不用
     */
    private List<String> getFileList(String path, String fileType) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path);
        File[] array = file.listFiles();
        List<String> mList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            String filePath = array[i].getName();
            String filePaths = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
            if (filePaths.equals(fileType))
                mList.add(filePath);
        }
        return mList;
    }

    /**
     * 获取文件列表 判断某些文件存不存在 暂时不用
     */
    private Boolean getFileSame(String path, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path);
        File[] array = file.listFiles();
        List<String> mList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            String filePath = array[i].getName();
            if (fileName.equals(filePath))
                return true;
        }
        return false;
    }

    /**
     * 删除APP下的无用安装包
     */
    private void delFile(String path) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path);
        File[] array = file.listFiles();
        for (int i = 0; i < array.length; i++) {
            String name = array[i].getName();
            if (name.substring(name.lastIndexOf(".") + 1, name.length()).equals("apk"))
                array[i].delete();
        }

    }

}
