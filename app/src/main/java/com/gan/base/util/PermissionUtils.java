package com.gan.base.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by gan on 2017/4/18.
 */

public class PermissionUtils {

    private Context mContext;

    private PermissionUtils(Context context) {
        this.mContext = context;
    }

    public static PermissionUtils getInstance(Context context) {
        return new PermissionUtils(context);
    }
    public void rebind(Context context) {
        mContext = context;
    }


    /**
     * 打电话权限
     *
     * @return true是没有权限去申请权限
     */
    public boolean CallPhone() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, PermisssionConstant.REQUEST_CODE_ASK_CALL_PHONE);
                return true;
            }
        }
        return false;
    }

    /**
     * 摄像头权限
     */
    public boolean Camer() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, PermisssionConstant.REQUEST_CODE_ASK_CANER);
                return true;
            }
        }
        return false;
    }

    /**
     * 录音权限
     */
    public boolean Audio() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions((Activity) mContext, new String[]{Manifest.permission.RECORD_AUDIO}, PermisssionConstant.REQUEST_CODE_ADK_AUDIO);
                return true;
            }
        }
        return false;
    }

    /**
     * 读取联系人权限
     */
    public boolean WriteContact() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CONTACTS);
            int checkCallPhonePermissions = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED || checkCallPhonePermissions != PackageManager.PERMISSION_GRANTED) {
                requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, PermisssionConstant.REQUEST_CODE_WRITE_CONTACT);
                return true;
            }
        }
        return false;
    }

    /**
     * 本地GPS权限
     */
    public boolean Location() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermisssionConstant.REQUEST_CODE_READ_LOCATION);
                return true;
            }
        }
        return false;
    }

    /**
     * 读取内存卡权限
     */
    public boolean Storage() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
            int checkCallPhonePermissions = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED || checkCallPhonePermissions != PackageManager.PERMISSION_GRANTED) {
                requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermisssionConstant.REQUEST_CODE_EXTERNAL_STORAGE);
                return true;
            }
        }
        return false;
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermisssionConstant.REQUEST_CODE_ASK_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "打电话权限授予成功", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    Toast.makeText(mContext, "打电话权限被拒", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case PermisssionConstant.REQUEST_CODE_ASK_CANER:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "开启摄像头权限授予成功", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    Toast.makeText(mContext, "开启摄像头权限被拒", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case PermisssionConstant.REQUEST_CODE_WRITE_CONTACT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "读取联系人权限授予成功", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(mContext, "读取联系人权限被拒", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            case PermisssionConstant.REQUEST_CODE_READ_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "GPS权限打开成功", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(mContext, "GPS权限被拒", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case PermisssionConstant.REQUEST_CODE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "开店读取内存卡权限", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(mContext, "开店读取内存卡权限被拒", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:

        }
    }
}
