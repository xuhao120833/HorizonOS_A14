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
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        outRect.right = right_space;
        outRect.left = left_space;
        outRect.top = top_space;
        outRect.bottom = bottom_space;
        Log.d(TAG,"getItemOffsets "+outRect.right+ " "+outRect.left);
        if (position<5) {
            outRect.top=first_row_top_space;
        }

    }

    public static int px2dp(float dpValue) {
        Log.d(TAG,"px2dp density" +Resources.getSystem().getDisplayMetrics().density);
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxAdapter(float px) {
        return (int) (px * (Resources.getSystem().getDisplayMetrics().widthPixels / 1280));
    }

}