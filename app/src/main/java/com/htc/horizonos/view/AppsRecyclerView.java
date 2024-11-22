package com.htc.horizonos.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class AppsRecyclerView extends RecyclerView {

    private static String TAG = "AppsRecyclerView";


    public AppsRecyclerView(Context context) {
        super(context);
    }

    public AppsRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            View focusedView = getFocusedChild();  // 获取当前获得焦点的view
//            View nextFocusView = null;
//
//            try {
//                // 使用 switch 处理方向键，确保水平和垂直方向都能控制
//                switch (keyCode) {
//                    case KeyEvent.KEYCODE_DPAD_DOWN:
//                        nextFocusView = FocusFinder.getInstance().findNextFocus(this, focusedView, View.FOCUS_DOWN);
//                        break;
//                    case KeyEvent.KEYCODE_DPAD_UP:
//                        nextFocusView = FocusFinder.getInstance().findNextFocus(this, focusedView, View.FOCUS_UP);
//                        break;
//                    case KeyEvent.KEYCODE_DPAD_LEFT:
//                        nextFocusView = FocusFinder.getInstance().findNextFocus(this, focusedView, View.FOCUS_LEFT);
//                        break;
//                    case KeyEvent.KEYCODE_DPAD_RIGHT:
//                        nextFocusView = FocusFinder.getInstance().findNextFocus(this, focusedView, View.FOCUS_RIGHT);
//                        break;
//                }
//            } catch (Exception e) {
//                // 捕获异常，并打印日志，便于调试
//                Log.d(TAG, "dispatchKeyEvent nextFocusView Exception");
//                nextFocusView = null;
//            }

            // 如果下一个焦点为空，消耗事件，不让系统处理，并保持当前焦点
            if (focusedView != null && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                Log.d(TAG, "dispatchKeyEvent nextFocusView == null && focusedView != null");
                focusedView.requestFocus();
                return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}