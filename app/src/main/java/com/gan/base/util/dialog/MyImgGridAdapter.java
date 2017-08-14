package com.gan.base.util.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.gan.base.R;
import com.gan.base.util.DensityUtils;
import com.gan.base.util.Utils;

import java.util.List;

/**
 * Created by gan on 2017/5/5.
 * 图片选择grid的适配器
 */
public class MyImgGridAdapter extends BaseAdapter {

    private final int maxPhptoNum;
    private Activity context;
    private List<String> path;
    private DelListener delListener;

    public MyImgGridAdapter(Activity context, List<String> path, int max) {
        this.path = path;
        this.context = context;
        this.maxPhptoNum = max;
    }

    @Override
    public int getCount() {
        return path == null ? 0 : (path.size() > maxPhptoNum ? maxPhptoNum : path.size());
    }

    @Override
    public Object getItem(int arg0) {
        return path == null ? null : path.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setDelListener(DelListener listener) {
        this.delListener = listener;
    }

    @Override
    public View getView(final int position, View view, ViewGroup group) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(
                    R.layout.img_pick_grid_item, null);
            holder.img = (ImageView) view.findViewById(R.id.img);
            holder.del = (ImageView) view.findViewById(R.id.del);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delListener != null) {
                    delListener.del(position);
                }
            }
        });
        String info = path.get(position);

        if (info != null) {
            if (info.equals("default")) {
                ImageLoader.getInstance().displayImage(
                        "drawable://" + R.drawable.ic_add, holder.img);
                holder.del.setVisibility(View.INVISIBLE);
            } else {
                ImageLoader.getInstance().displayImage(
                        "file://" + info, holder.img);
                holder.del.setVisibility(View.VISIBLE);
            }
            setViewHeight2(
                    holder.img,
                    (Utils.getScreenW(context) - DensityUtils.dp2px(context, 60)) / 3);

        }
        return view;
    }

    public void refresh(List<String> path) {
        this.path = path;
        notifyDataSetChanged();
    }

    class Holder {
        ImageView img;
        ImageView del;
    }

    /**
     * 设置griditem布局高度
     *
     * @param v
     * @param height
     */
    public static void setViewHeight2(View v, int height) {
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) v.getLayoutParams(); // 取控件mGrid当前的布局参数
        linearParams.height = height;// 当控件的高强制设成height
        v.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件myGrid
    }

    public interface DelListener {
        public void del(int postion);
    }
}
