package com.gan.base.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.gan.base.R;

/**
 * Created by gan on 2017/5/22.
 */
public class MyDialogSimple {
    private  Context context;
    // Context context;
    private int check;
    private AlertDialog.Builder builder;
    private setSimpleDialog onClick;
    private AlertDialog alertDialog;

    public MyDialogSimple(Context context) {

        this.context=context;
    }

    public void setSimpleDialog(int icon, String title, String msg, String yes, String no) {
        builder = new AlertDialog.Builder(context);
        if (icon == 0)
            builder.setIcon(R.mipmap.ic_launcher);
        else
            builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (null != onClick)
                    onClick.setSimpleDialogNo(dialog, check);
            }
        });
        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != onClick)
                    onClick.setSimpleDialogYes(dialog, check);
            }
        });
        alertDialog = builder.create();
    }

    public void setSimpleDialogNoCancel(int icon, String title, String msg, String yes) {
        builder = new AlertDialog.Builder(context);
        if (icon == 0)
            builder.setIcon(R.mipmap.ic_launcher);
        else
            builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != onClick)
                    onClick.setSimpleDialogYes(dialog, check);
            }
        });
        alertDialog = builder.create();
    }

    public void setMessage(String msg) {
        builder.setMessage(msg);
    }

    public void setSimpleShow() {
        if (null != alertDialog)
            alertDialog.show();
    }

    public void setSimpleDialogLinstener(setSimpleDialog onClick) {
        this.onClick = onClick;
    }

    public interface setSimpleDialog {
        void setSimpleDialogYes(DialogInterface dialog, int which);

        void setSimpleDialogNo(DialogInterface dialog, int which);
    }

}
