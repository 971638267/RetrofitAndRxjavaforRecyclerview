package com.gan.base.util.image;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtil {
	
 	private static class LazyHolder {    
       private static final BitmapUtil INSTANCE = new BitmapUtil();    
    }    
    private BitmapUtil(){}
    
    /**
	 * 获取BitmapUtil实例
	 * @return
	 */
    public static final BitmapUtil init(){    
       return LazyHolder.INSTANCE;    
    }  
    
	private static List<Bitmap> bitmaps;
	
	
	/**
	 * 读取Asset文件夹中的图片，图片丢失引用，需自行释放
	 * @param context
	 * @param fileName
	 * @return
	 */
	/*
	public  Bitmap getImageFromAssetsFile(Context context ,String fileName) 
	{ 
		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}*/
	
	/**
	 * 根据宽度等比例缩放图片
	 * 
	 * @param defaultBitmap
	 * @param targetWidth
	 * @return
	 */
	public static Bitmap resizeImageByWidth(Bitmap defaultBitmap,
			int targetWidth) {
		int rawWidth = defaultBitmap.getWidth();
		int rawHeight = defaultBitmap.getHeight();
		float targetHeight = targetWidth * (float) rawHeight / (float) rawWidth;
		float scaleWidth = targetWidth / (float) rawWidth;
		float scaleHeight = targetHeight / (float) rawHeight;
		Matrix localMatrix = new Matrix();
		localMatrix.postScale(scaleHeight, scaleWidth);
		return Bitmap.createBitmap(defaultBitmap, 0, 0, rawWidth, rawHeight,
				localMatrix, true);
	}

	
}
