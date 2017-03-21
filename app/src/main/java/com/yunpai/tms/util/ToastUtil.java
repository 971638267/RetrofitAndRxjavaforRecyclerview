package com.yunpai.tms.util;import android.app.ProgressDialog;import android.content.Context;import android.view.Gravity;import android.widget.Toast;import com.yunpai.tms.application.MyApplication;import com.yunpai.tms.constant.Constant;import java.lang.reflect.Method;/** * Toast *  * @author Swain *  */public class ToastUtil {	public static Method showMethod;	public static Method hideMethod;	public static Object obj;	public static ProgressDialog pd;	private static Toast toast;	/**	 * 屏幕中间弹窗	 *	 * @param msg	 */	public static void ToastCenter(String msg) {		if (!Constant.TOAST)			return;		if (null == toast)		{			toast = Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);			toast.setGravity(Gravity.CENTER, 0, 0);		}		else			toast.setText(msg);		toast.show();	}	/**	 * 屏幕底端	 *	 * @param msg	 */	public static void ToastBottow( String msg) {		if (!Constant.TOAST)			return;		if (null == toast)		{			toast = Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);			toast.setGravity(Gravity.BOTTOM, 0, 0);		}		else			toast.setText(msg);		toast.show();	}	/**	 * 默认位置	 *	 * @param msg	 */	public static void ToastDefult( String msg) {		if (!Constant.TOAST)			return;		if (null == toast)		{			toast = Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);		}		else			toast.setText(msg);		toast.show();	}	/**	 * 等待	 * 	 * @param context	 * @param msg	 */	public static void DialogHttp(Context context, String msg) {		pd = new ProgressDialog(context);		pd.setCanceledOnTouchOutside(false);		pd.setMessage(msg);		pd.show();	}}