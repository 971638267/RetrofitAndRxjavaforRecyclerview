package com.gan.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.gan.base.viewhelper.Pullable;

/**
 *可以下拉刷新的ScrollView
* @ClassName: PullableScrollView  
* @Description: TODO  
* @author 甘玉飞
* @date 2016年7月6日 下午2:16:08  
*
 */
public class PullableScrollView extends ScrollView implements Pullable
{

	public PullableScrollView(Context context)
	{
		super(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}


}
