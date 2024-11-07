package com.htc.luminaos.widget;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private static String TAG = "SpacesItemDecoration";

    private int left_space, right_space, top_space, bottom_space;

    public SpacesItemDecoration(int left_space, int right_space, int top_space, int bottom_space) {
        this.left_space = left_space;
        this.right_space = right_space;
        this.top_space = top_space;
        this.bottom_space = bottom_space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = right_space;
        outRect.left = left_space;
        outRect.bottom = bottom_space;
        Log.d(TAG, "getItemOffsets " + outRect.right + " " + outRect.left);
        outRect.top = top_space;
    }

    public static int px2dp(float dpValue) {
        Log.d(TAG, "px2dp density" + Resources.getSystem().getDisplayMetrics().density);
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxAdapter(float px) {
        return (int) (px * (Resources.getSystem().getDisplayMetrics().widthPixels / 1280));
    }

}