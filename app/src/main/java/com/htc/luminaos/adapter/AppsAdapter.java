package com.htc.luminaos.adapter;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Outline;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.MainActivity;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.fragment.NewFragment;
import com.htc.luminaos.fragment.OriginalFragment;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.ScrollUtils;
import com.htc.luminaos.utils.Utils;
import com.htc.luminaos.widget.AppDetailDialog;
import com.htc.luminaos.widget.SpacesItemDecoration2;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> implements View.OnKeyListener, View.OnHoverListener,View.OnLongClickListener {

    Context mContext;
    RecyclerView recyclerView;
    List<AppInfoBean> infoBeans;
    private PackageManager mPm;
    private String TAG = "AppsAdapter";

    private MainActivity activity=null;

    public AppsAdapter(Context mContext, List<AppInfoBean> infoBeans, RecyclerView recyclerView,MainActivity activity) {
        this.mContext = mContext;
        this.infoBeans = infoBeans;
        this.recyclerView = recyclerView;
        this.mPm = mContext.getPackageManager();
        this.activity = activity;
    }

    public AppsAdapter(Context mContext, List<AppInfoBean> infoBeans, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.infoBeans = infoBeans;
        this.recyclerView = recyclerView;
        this.mPm = mContext.getPackageManager();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_item2, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final AppInfoBean info = infoBeans.get(i);
        Drawable icon = getAdaptiveIcon(info.getApppackagename());
        if(icon == null) {
            myViewHolder.icon.setImageDrawable(info.getAppicon());
            icon = getRandomDrawable();
            myViewHolder.rl_apps.setBackground(icon);
        } else {
            myViewHolder.rl_apps.setBackground(icon);
        }
        myViewHolder.name.setText(info.getAppname());
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.startNewApp(mContext, info.getApppackagename());
            }
        });

        myViewHolder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
//                if (recyclerView == null)
//                    return;
                AnimationSet animationSet = new AnimationSet(true);
                view.bringToFront();
                if (hasFocus) {

                    ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                            1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(150);
                    animationSet.addAnimation(scaleAnimation);
                    animationSet.setFillAfter(true);
                    view.startAnimation(animationSet);
                    myViewHolder.name.setVisibility(View.VISIBLE);
                    myViewHolder.name.setSelected(true);
                } else {
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                            1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    animationSet.addAnimation(scaleAnimation);
                    scaleAnimation.setDuration(150);
                    animationSet.setFillAfter(true);
                    view.startAnimation(animationSet);
                    myViewHolder.name.setVisibility(View.INVISIBLE);
                    myViewHolder.name.setSelected(false);
                }
            }
        });

        if(i==0) {
            myViewHolder.rl_item.requestFocus();
        }
        myViewHolder.rl_item.setOnKeyListener(this);
        myViewHolder.rl_item.setOnHoverListener(this);
        myViewHolder.rl_item.setOnLongClickListener(this);
    }

    @Override
    public int getItemCount() {
        return infoBeans.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && v.hasFocus() && position < 5
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(TAG, " 顶部焦点向上 "+position);
            enableFocus();
            ((MainActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_reverse, R.anim.slide_out_reverse)
                    .replace(R.id.fragment_container, MainActivity.originalFragment)
                    .commit();

            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(TAG, "卸载收到MENU按键");
//            int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
            final AppInfoBean info = infoBeans.get(position);

            boolean[] result = AppUtils.checkIfSystemAppAndCanUninstall(mContext, info.getApplicationInfo().packageName);
            if (result[0] && !result[1]) {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getString(R.string.hint)) // 对话框标题
                        .setMessage(mContext.getString(R.string.system_app_cannot_uninstalled)) // 对话框内容
                        .setPositiveButton(mContext.getString(R.string.enter), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // 点击“确定”按钮时，关闭对话框
                            }
                        })
                        .setCancelable(false) // 使对话框不能通过点击外部区域关闭
                        .create();

                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            dialog.dismiss(); // 按下返回键时关闭对话框
                            return true; // 表示已经处理了返回键事件
                        }
                        return false; // 没有处理返回键事件，继续传递事件
                    }
                });

                dialog.show();
                return true;
            }

            AppDetailDialog detailDialog = new AppDetailDialog(mContext, R.style.DialogTheme);
            detailDialog.setData(info.getApplicationInfo());
            detailDialog.setOnClickCallBack(new AppDetailDialog.OnAppDetailCallBack() {
                @Override
                public void onClear_cache(String packageName) {
                    ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                    activityManager.clearApplicationUserData(packageName, null);
                }

                @Override
                public void onUninstall(String packageName) {
                    try {
                        Intent intent = new Intent();
                        Uri uri = Uri.parse("package:" + packageName);
                        //获取删除包名的URI
                        intent.setAction(Intent.ACTION_DELETE);
                        //设置我们要执行的卸载动作
                        intent.setData(uri);
                        //设置获取到的
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            });
            detailDialog.show();
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG,"卸载收到MENU按键");
        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        final AppInfoBean info = infoBeans.get(position);

        boolean[] result = AppUtils.checkIfSystemAppAndCanUninstall(mContext, info.getApplicationInfo().packageName);
        if (result[0] && !result[1]) {
            AlertDialog dialog =new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.hint)) // 对话框标题
                    .setMessage(mContext.getString(R.string.system_app_cannot_uninstalled)) // 对话框内容
                    .setPositiveButton(mContext.getString(R.string.enter), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // 点击“确定”按钮时，关闭对话框
                        }
                    })
                    .setCancelable(false) // 使对话框不能通过点击外部区域关闭
                    .create();
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        dialog.dismiss(); // 按下返回键时关闭对话框
                        return true; // 表示已经处理了返回键事件
                    }
                    return false; // 没有处理返回键事件，继续传递事件
                }
            });
            dialog.show();
            return true;
        }
        AppDetailDialog detailDialog = new AppDetailDialog(mContext,R.style.DialogTheme);
        detailDialog.setData(info.getApplicationInfo());
        detailDialog.setOnClickCallBack(new AppDetailDialog.OnAppDetailCallBack() {
            @Override
            public void onClear_cache(String packageName) {
                ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.clearApplicationUserData(packageName,null);
            }
            @Override
            public void onUninstall(String packageName) {
                try {
                    Intent intent=new Intent();
                    Uri uri = Uri.parse("package:"+packageName);
                    //获取删除包名的URI
                    intent.setAction(Intent.ACTION_DELETE);
                    //设置我们要执行的卸载动作
                    intent.setData(uri);
                    //设置获取到的
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
        detailDialog.show();
        return true;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        RelativeLayout rl_item;

        RelativeLayout rl_apps;

        Context context;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.app_name);
            rl_item = itemView.findViewById(R.id.rl_item);
            rl_apps = itemView.findViewById(R.id.rl_apps);
            icon = itemView.findViewById(R.id.app_icon);

            //给rl_apps设置圆角
            float cornerRadius;
            cornerRadius = SpacesItemDecoration2.pxAdapter(context.getResources().getDimension(R.dimen.x_20));//优先考虑分辨率变化
//            float cornerRadius = SpacesItemDecoration2.px2dp(13.33F);//优先考虑dpi变化
            rl_apps.setClipToOutline(true);
            final float finA = cornerRadius;
            rl_apps.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    // 创建圆角矩形的轮廓
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), finA);
                }
            });

        }
    }

    // 获取 View 在适配器中的位置
    private int getAdapterPositionForView(View view) {
        // 查找父视图 RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.getParent();
        if (recyclerView != null) {
            return recyclerView.getChildAdapterPosition(view);
        }
        return -1; // 返回无效位置
    }

    private void enableFocus() {
        activity.htcosBinding.rlWifi.setFocusable(true);
        activity.htcosBinding.rlUsbConnect.setFocusable(true);
        activity.htcosBinding.rlBattery.setFocusable(true);
        activity.htcosBinding.rlBluetooth.setFocusable(true);
        activity.htcosBinding.rlSettings.setFocusable(true);
        activity.htcosBinding.rlWallpapers.setFocusable(true);
        activity.htcosBinding.rlSignalSource.setFocusable(true);
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        int what = event.getAction();
        switch (what) {
            case MotionEvent.ACTION_HOVER_ENTER: // 鼠标进入view
                v.requestFocus();
                break;
            case MotionEvent.ACTION_HOVER_MOVE: // 鼠标在view上
                break;
            case MotionEvent.ACTION_HOVER_EXIT: // 鼠标离开view
                break;
        }
        return false;
    }

    public Drawable getAdaptiveIcon(String packageName) {
        try {
            // 获取 PackageManager 实例
            PackageManager packageManager = mContext.getPackageManager();

            // 获取应用的 ApplicationInfo
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);

            // 获取应用的图标
            Drawable icon = packageManager.getApplicationBanner(appInfo);
            return icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null; // 处理找不到包名的异常
        }
    }

    private Drawable getRandomDrawable() {
        // 确保列表非空
        if (Utils.appsBgDrawables.isEmpty()) {
            return null; // 返回空值，避免出现越界错误
        }
        // 创建随机数生成器
        Random random = new Random();
        // 生成一个 0 到 appsBgDrawables.size() - 1 的随机索引
        int randomIndex = random.nextInt(Utils.appsBgDrawables.size());
        // 返回随机选择的 Drawable
        return Utils.appsBgDrawables.get(randomIndex);
    }
}