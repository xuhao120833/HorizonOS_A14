package com.htc.luminaos.widget;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.htc.luminaos.activity.MainActivity;

/**
 * Author:
 * Date:
 * Description:
 */
public class SpacesItemDecoration2 extends RecyclerView.ItemDecoration {

    private static String TAG = "SpacesItemDecoration";

    private int left_space, right_space, top_space, bottom_space;

    private int first_list_left_space;

    private int first_row_top_space;

    public SpacesItemDecoration2(int left_space, int right_space, int top_space, int bottom_space) {
        this.left_space = left_space;
        this.right_space = right_space;
        this.top_space = top_space;
        this.bottom_space = bottom_space;
    }

    public SpacesItemDecoration2(int left_space, int right_space, int top_space, int bottom_space, int first_row_top_space) {
        this.left_space = left_space;
        this.right_space = right_space;
        this.top_space = top_space;
        this.bottom_space = bottom_space;
        this.first_row_top_space = first_row_top_space;
        Log.d(TAG, "SpacesItemDecoration2 right_space " + right_space + " left_space " + left_space
                + " top_space " + top_space + " bottom_space " + bottom_space + " " + MainActivity.newFragment);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        Log.d(TAG, "getItemOffsets right_space " + right_space + " left_space " + left_space + " top_space " + top_space + " bottom_space " + bottom_space);
        outRect.right = right_space;
        outRect.left = left_space;
        outRect.top = top_space;
        outRect.bottom = bottom_space;
        if (position < 5) {//第一行的上边距额外处理
            outRect.top = first_row_top_space;
        }
        if (position + 5 > parent.getAdapter().getItemCount()) { //最后一行解决底部文字放大后被遮挡问题
            outRect.bottom = SpacesItemDecoration2.pxAdapter(11.25F);
        }
        Log.d(TAG, "getItemOffsets outRect.right " + outRect.right + " outRect.left " + outRect.left + " outRect.top " + outRect.top + " outRect.bottom " + outRect.bottom);
    }

    public static int dp2px(float dpValue) { //考虑dpi，dp转化为px
        Log.d(TAG, "dp2px density" + Resources.getSystem().getDisplayMetrics().density);
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

//    public static int pxAdapter(float px) { //只考虑分辨率大小变化，等比例放大
//        Log.d(TAG, "pxAdapter widthPixels" + Resources.getSystem().getDisplayMetrics().widthPixels + " " + (int) (px * (Resources.getSystem().getDisplayMetrics().widthPixels / 1920)));
//        return (int) (px * (float) (Resources.getSystem().getDisplayMetrics().widthPixels) / 1920F);
//    }

    public static int pxAdapter(float px) {
        // 获取设备的屏幕宽度
        float widthPixels = (float) Resources.getSystem().getDisplayMetrics().widthPixels;
        // 计算比例，避免除法结果过小
        float scaleFactor = widthPixels / 1920f;
        Log.d(TAG, "pxAdapter widthPixels" + widthPixels + " " + px * scaleFactor);
        // 返回计算后的 px 值
        return (int)(px * scaleFactor);
    }
}