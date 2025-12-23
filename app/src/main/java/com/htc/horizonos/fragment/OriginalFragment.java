package com.htc.horizonos.fragment;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htc.horizonos.utils.LogUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htc.horizonos.R;
import com.htc.horizonos.activity.MainActivity;
import com.htc.horizonos.databinding.ActivityMainHtcosBinding;
import com.htc.horizonos.databinding.Originalfragment2Binding;
import com.htc.horizonos.databinding.OriginalfragmentBinding;
import com.htc.horizonos.utils.AppUtils;
import com.htc.horizonos.utils.DBUtils;
import com.htc.horizonos.utils.LanguageUtil;
import com.htc.horizonos.utils.Utils;

import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OriginalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OriginalFragment extends Fragment implements View.OnKeyListener, View.OnHoverListener, View.OnClickListener, View.OnFocusChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Originalfragment2Binding binding;

//    private Originalfragment2Binding binding2;

    private static String TAG = "OriginalFragment";

    public OriginalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OriginalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OriginalFragment newInstance(String param1, String param2) {
        OriginalFragment fragment = new OriginalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(() -> {
            // 设置焦点的逻辑
            binding.rlScreenCast.requestFocus();
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = Originalfragment2Binding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //onClick
        binding.rlScreenCast.setOnClickListener(this);
        binding.rlSignalSource.setOnClickListener(this);
        binding.rlFileExplorer.setOnClickListener(this);
        binding.rlAppStore.setOnClickListener(this);
        binding.netflix.setOnClickListener(this);
        binding.youtube.setOnClickListener(this);
        binding.disney.setOnClickListener(this);
//        binding.max.setOnClickListener(this);
        binding.primeVideo.setOnClickListener(this);
        binding.hulu.setOnClickListener(this);

        //onHover
        binding.rlScreenCast.setOnHoverListener(this);
        binding.rlSignalSource.setOnHoverListener(this);
        binding.rlFileExplorer.setOnHoverListener(this);
        binding.rlAppStore.setOnHoverListener(this);
        binding.netflix.setOnHoverListener(this);
        binding.youtube.setOnHoverListener(this);
        binding.disney.setOnHoverListener(this);
//        binding.max.setOnHoverListener(this);
        binding.primeVideo.setOnHoverListener(this);
        binding.hulu.setOnHoverListener(this);

        //onFocus
        binding.rlScreenCast.setOnFocusChangeListener(this);
        binding.rlSignalSource.setOnFocusChangeListener(this);
        binding.rlFileExplorer.setOnFocusChangeListener(this);
        binding.rlAppStore.setOnFocusChangeListener(this);
        binding.netflix.setOnFocusChangeListener(this);
        binding.youtube.setOnFocusChangeListener(this);
        binding.disney.setOnFocusChangeListener(this);
//        binding.max.setOnFocusChangeListener(this);
        binding.primeVideo.setOnFocusChangeListener(this);
        binding.hulu.setOnFocusChangeListener(this);

        //onKey
        binding.netflix.setOnKeyListener(this);
        binding.youtube.setOnKeyListener(this);
        binding.disney.setOnKeyListener(this);
//        binding.max.setOnKeyListener(this);
        binding.primeVideo.setOnKeyListener(this);
        binding.hulu.setOnKeyListener(this);
//        binding.rlScreenCast.requestFocus();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, " 执行onResume");
        setIconOrText();
        binding.screenCastTxt.setSelected(true);
        binding.signalSourceTxt.setSelected(true);
        binding.fileExplorerTxt.setSelected(true);
        binding.appStoreTxt.setSelected(true);
//        binding.rlScreenCast.requestFocus();
//        enableFocus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, " 执行onHiddenChanged " + hidden);
        if (!hidden) {
            // Fragment 切换到显示状态时的操作
            // 设置焦点
            if (!binding.rlScreenCast.hasFocus()) {
                binding.rlScreenCast.requestFocus();
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        LogUtils.d(TAG, " onKey收到 " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && (binding.netflix.hasFocus()
                || binding.youtube.hasFocus() || binding.disney.hasFocus()
                || binding.primeVideo.hasFocus() || binding.hulu.hasFocus())
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            LogUtils.d(TAG, " 底部焦点向下");
            disableFocus();//防止焦点跳变
            MainActivity activity = (MainActivity) getActivity();
            if (activity.newFragment != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                        .hide(this)
                        .show(activity.newFragment)
                        .commit();
            } else {
                LogUtils.d(TAG, "newFragment 未初始化完成");
            }
            return true;
        } else if (getActivity().getCurrentFocus() == null) {
            binding.rlScreenCast.requestFocus();
        }
        return false;
    }

    private void disableFocus() {
        MainActivity activity = (MainActivity) getActivity();
        activity.htcosBinding.rlWifi.setFocusable(false);
        activity.htcosBinding.rlUsbConnect.setFocusable(false);
        activity.htcosBinding.rlBattery.setFocusable(false);
        activity.htcosBinding.rlBluetooth.setFocusable(false);
        activity.htcosBinding.rlSettings.setFocusable(false);
        activity.htcosBinding.rlWallpapers.setFocusable(false);
        activity.htcosBinding.rlSignalSource.setFocusable(false);
        activity.htcosBinding.rlClearMemory.setFocusable(false);
        activity.htcosBinding.rlEthernet.setFocusable(false);
    }

    private void enableFocus() {
        MainActivity activity = (MainActivity) getActivity();
        activity.htcosBinding.rlWifi.setFocusable(true);
        activity.htcosBinding.rlUsbConnect.setFocusable(true);
        activity.htcosBinding.rlBattery.setFocusable(true);
        activity.htcosBinding.rlBluetooth.setFocusable(true);
        activity.htcosBinding.rlSettings.setFocusable(true);
        activity.htcosBinding.rlWallpapers.setFocusable(true);
        activity.htcosBinding.rlSignalSource.setFocusable(true);
    }

    @Override
    public void onClick(View v) {
        String appname = null;
        String action = null;
        MainActivity activity = (MainActivity) getActivity();
        int id = v.getId();
        if (id == R.id.rl_screen_cast) {
            try {
                String listaction = DBUtils.getInstance(getActivity()).getActionFromListModules("list1");
                if (listaction != null && !listaction.equals("")) { //读取配置
                    activity.goAction(listaction);
                } else {// 默认跳转
                    AppUtils.startNewApp(getActivity(), "com.ecloud.eshare.server");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.rl_signal_source) {
            try {
                String listaction = DBUtils.getInstance(getActivity()).getActionFromListModules("list2");
                if (listaction != null && !listaction.equals("")) { //读取配置
                    activity.goAction(listaction);
                } else {// 默认跳转
                    if (Utils.sourceList.length > 1) { //支持多信源
                        showSourceDialog();
                    } else {
                        // 默认跳转
                        startSource("HDMI1");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.rl_file_explorer) {
            try {
                AppUtils.startNewApp(getActivity(), "com.hisilicon.explorer");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.rl_app_store) {
            try {
                AppUtils.startNewApp(getActivity(), "com.htc.storeos");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.netflix) {
            LogUtils.d("xuhao", "打开奈飞");
            appname = DBUtils.getInstance(activity).getAppNameByTag("icon1");
            action = DBUtils.getInstance(activity).getActionByTag("icon1");
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(activity, action)) {
                    LogUtils.d("xuhao", "打开奈飞 第一个坑位不为空 " + appname + "2" + action + "3");
                    activity.appName = appname;
                    activity.requestChannelData();
                }
            } else if (!AppUtils.startNewApp(activity, "com.netflix.mediaclient")) {
                if (!AppUtils.startNewApp(activity, "com.netflix.ninja")) {
                    LogUtils.d("xuhao", "打开奈飞 第一个坑位为空");
                    activity.appName = "Netflix";
                    activity.requestChannelData();
                }
            }
        } else if (id == R.id.youtube) {
            LogUtils.d("xuhao", "打开YOUtube");
            appname = DBUtils.getInstance(activity).getAppNameByTag("icon2");
            action = DBUtils.getInstance(activity).getActionByTag("icon2");
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(activity, action)) {
                    activity.appName = appname;
                    activity.requestChannelData();
                }
            } else if (!AppUtils.startNewApp(activity, "com.google.android.youtube.tv")) {
                activity.appName = "Youtube";
                activity.requestChannelData();
            }
        } else if (id == R.id.disney) {
            LogUtils.d("xuhao", "打开迪士尼");
            appname = DBUtils.getInstance(activity).getAppNameByTag("icon3");
            action = DBUtils.getInstance(activity).getActionByTag("icon3");
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(activity, action)) {
                    activity.appName = appname;
                    activity.requestChannelData();
                }
            } else if (!AppUtils.startNewApp(activity, "com.disney.disneyplus")) {
                activity.appName = "Disney+";
                activity.requestChannelData();
            }
            //            case R.id.max:
//                LogUtils.d("xuhao", "打开max");
//                appname = DBUtils.getInstance(activity).getAppNameByTag("icon4");
//                action = DBUtils.getInstance(activity).getActionByTag("icon4");
//                if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
//                    if (!AppUtils.startNewApp(activity, action)) {
//                        activity.appName = appname;
//                        activity.requestChannelData();
//                    }
//                } else if (!AppUtils.startNewApp(activity, "com.wbd.stream")) {
//                    activity.appName = "Max";
//                    activity.requestChannelData();
//                }
//                break;
        } else if (id == R.id.prime_video) {
            LogUtils.d("xuhao", "打开prime_video");
            appname = DBUtils.getInstance(activity).getAppNameByTag("icon4");
            action = DBUtils.getInstance(activity).getActionByTag("icon4");
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(activity, action)) {
                    activity.appName = appname;
                    activity.requestChannelData();
                }
            } else if (!AppUtils.startNewApp(activity, "com.amazon.avod.thirdpartyclient")) {
                activity.appName = "prime video";
                activity.requestChannelData();
            }
        } else if (id == R.id.hulu) {
            LogUtils.d("xuhao", "打开hulu");
            appname = DBUtils.getInstance(activity).getAppNameByTag("icon5");
            action = DBUtils.getInstance(activity).getActionByTag("icon5");
            if (appname != null && action != null && !appname.equals("") && !action.equals("")) {
                if (!AppUtils.startNewApp(activity, action)) {
                    activity.appName = appname;
                    activity.requestChannelData();
                }
            } else if (!AppUtils.startNewApp(activity, "com.hulu.plus")) {
                LogUtils.d("xuhao", "打开hulu失败");
                activity.appName = "Hulu";
                activity.requestChannelData();
            }
        }
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        AnimationSet animationSet = new AnimationSet(true);
        v.bringToFront();
        if (hasFocus) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                    1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(150);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        } else {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                    1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animationSet.addAnimation(scaleAnimation);
            scaleAnimation.setDuration(150);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        }
    }

    public void setIconOrText() {
        //第一行
        setListModules();
        //第二行
        setMainApp();
    }

    private void setListModules() {
//        getActivity().runOnUiThread(() -> {
        Drawable drawable = DBUtils.getInstance(getContext()).getDrawableFromListModules("list1");
        if (drawable != null) {
            binding.screenCast.setImageDrawable(drawable);
            drawable = null;
        }
        drawable = DBUtils.getInstance(getContext()).getDrawableFromListModules("list2");
        if (drawable != null) {
            binding.signalSource.setImageDrawable(drawable);
            drawable = null;
        }
        Hashtable<String, String> mHashtable1 = DBUtils.getInstance(getContext()).getHashtableFromListModules("list1");
        Hashtable<String, String> mHashtable2 = DBUtils.getInstance(getContext()).getHashtableFromListModules("list2");
        LogUtils.d(TAG, "xu当前语言" + LanguageUtil.getCurrentLanguage());
        if (mHashtable1 != null) {
            String text = mHashtable1.get(LanguageUtil.getCurrentLanguage());
            LogUtils.d(TAG, "xu当前语言 text eshareText" + text);
            if (text != null && !text.isEmpty()) {
                binding.screenCastTxt.setText(text);
            }
        }
        if (mHashtable2 != null) {
            String text = mHashtable2.get(LanguageUtil.getCurrentLanguage());
            LogUtils.d(TAG, "xu当前语言 text hdmiText" + text);
            if (text != null && !text.isEmpty()) {
                binding.signalSourceTxt.setText(text);
            }
        }
//        });
    }

    private void setMainApp() {
        Drawable drawable = DBUtils.getInstance(getContext()).getIconDataByTag("icon1");
        if (drawable != null) {
            binding.netflix.setImageDrawable(drawable);
        }
        drawable = DBUtils.getInstance(getContext()).getIconDataByTag("icon2");
        if (drawable != null) {
            binding.youtube.setImageDrawable(drawable);
        }
        drawable = DBUtils.getInstance(getContext()).getIconDataByTag("icon3");
        if (drawable != null) {
            binding.disney.setImageDrawable(drawable);
        }
        drawable = DBUtils.getInstance(getContext()).getIconDataByTag("icon4");
        if (drawable != null) {
            binding.primeVideo.setImageDrawable(drawable);
        }
        drawable = DBUtils.getInstance(getContext()).getIconDataByTag("icon5");
        if (drawable != null) {
            binding.hulu.setImageDrawable(drawable);
        }
    }

    //正常背景
    public void showSourceDialog() {
        // 创建一个 Dialog 对象
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_source2); // 使用自定义布局
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); // 宽度为屏幕宽度，高度自适应内容
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // 设置黑色背景
        dialog.getWindow().setGravity(Gravity.CENTER); // 设置在屏幕中央显示
        // 获取 LinearLayout 来动态添加选项
        LinearLayout layout = dialog.findViewById(R.id.source_layout);
//        // 设置 Lottie 动画视图
//        LottieAnimationView lottieBackground = dialog.findViewById(R.id.lottie_background);
        for (int i = 0; i < Utils.sourceListTitle.length + 1; i++) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            // 将 XML 布局文件转换为 View 对象
            LinearLayout source_item = (LinearLayout) inflater.inflate(R.layout.source_item, null);
            TextView source_title = (TextView) source_item.findViewById(R.id.source_title);
            if (i == 0) {
                source_title.setText(getResources().getString(R.string.choose_source));
                source_title.setTextColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) getResources().getDimension(R.dimen.x_400),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, (int) getResources().getDimension(R.dimen.x_30),
                        0, (int) getResources().getDimension(R.dimen.x_30));
                source_item.setLayoutParams(params);
                source_item.setFocusable(false);
                source_item.setFocusableInTouchMode(false);
                layout.addView(source_item);
                source_title.setSelected(true);
            } else {
                String title = Utils.sourceListTitle[i-1];
                // 获取 LayoutInflater 对象
                source_title.setText(title);
                // 设置上下外边距
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) getResources().getDimension(R.dimen.x_400),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0,
                        0, (int) getResources().getDimension(R.dimen.x_30));
                source_item.setLayoutParams(params);
                source_item.setBackgroundResource(R.drawable.source_bg_custom);
                int finalI = i;
                source_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSource(Utils.sourceList[finalI-1]);
                    }
                });
                source_item.setOnHoverListener(this);
                // 将每一行的 LinearLayout 加入到主布局
                layout.addView(source_item);
            }
        }
        // 显示 Dialog
        dialog.show();
    }

    private void startSource(String sourceName) {
        LogUtils.d(TAG, " startSource启动信源 " + sourceName);
        Intent intent_hdmi = new Intent();
        intent_hdmi.setComponent(new ComponentName("com.softwinner.awlivetv", "com.softwinner.awlivetv.MainActivity"));
        intent_hdmi.putExtra("input_source", sourceName);
        intent_hdmi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent_hdmi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent_hdmi);
    }
}