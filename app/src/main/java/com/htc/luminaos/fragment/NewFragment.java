package com.htc.luminaos.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.AppsActivity;
import com.htc.luminaos.activity.MainActivity;
import com.htc.luminaos.adapter.AppsAdapter;
import com.htc.luminaos.databinding.FragmentNewBinding;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.receiver.AppCallBack;
import com.htc.luminaos.receiver.AppReceiver;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.Utils;
import com.htc.luminaos.widget.SpacesItemDecoration;
import com.htc.luminaos.widget.SpacesItemDecoration2;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static List<AppInfoBean> appInfoBeans = null;

    private FragmentNewBinding binding;

    private static String TAG = "NewFragment";

    //app
    private IntentFilter appFilter = new IntentFilter();
    private AppReceiver appReceiver = null;

    AppCallBack appCallBack = new AppCallBack() {
        @Override
        public void appChange(String packageName) {
            appInfoBeans = AppUtils.getApplicationMsg(getContext());
            initData();
        }

        @Override
        public void appUnInstall(String packageName) {
            appInfoBeans = AppUtils.getApplicationMsg(getContext());
            initData();
        }

        @Override
        public void appInstall(String packageName) {
            appInfoBeans = AppUtils.getApplicationMsg(getContext());
            initData();
        }
    };


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                List<AppInfoBean> infoBeans = (List<AppInfoBean>) msg.obj;
                AppsAdapter appsAdapter = new AppsAdapter(getContext(), infoBeans, binding.appsRv, (MainActivity) getActivity());
//                binding.appsRv.addItemDecoration(new SpacesItemDecoration2(SpacesItemDecoration2.pxAdapter(22.8F), SpacesItemDecoration2.pxAdapter(22.6F),
//                        SpacesItemDecoration2.pxAdapter(22.5F), 0, SpacesItemDecoration2.pxAdapter(60F)));
                binding.appsRv.setAdapter(appsAdapter);
                binding.appsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (dy > 0) {
                            // RecyclerView 向下滚动
                            Log.d(TAG, "RecyclerView is scrolling down");
//                            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                @Override
//                                public void onGlobalLayout() {
//                                    // 设置焦点到倒数第二个 item
//                                    setInitialFocus();
//                                    // 移除监听器，避免多次调用
//                                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                }
//                            });
                        } else if (dy < 0) {
                            // RecyclerView 向上滚动
                            Log.d(TAG, "RecyclerView is scrolling up");
                        }
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                judageFocus();
                                Log.d(TAG, "RecyclerView is not scrolling");
                                break;
                            case RecyclerView.SCROLL_STATE_DRAGGING:
                                Log.d(TAG, "RecyclerView is actively scrolling");
                                break;
                            case RecyclerView.SCROLL_STATE_SETTLING:
                                Log.d(TAG, "RecyclerView is settling after a fling");
                                break;
                        }
                    }
                });
            }
            return false;
        }
    });

//    private void setInitialFocus() {
//        // 确保 RecyclerView 已经完成布局
//        RecyclerView.LayoutManager layoutManager = binding.appsRv.getLayoutManager();
//        Log.d(TAG, " setInitialFocus layoutManager" + layoutManager);
//        if (layoutManager != null && layoutManager.getChildCount() > 1) {
//            // 获取倒数第二个可见的 item
//            View secondLastVisibleItem = layoutManager.getChildAt(layoutManager.getChildCount() - 2);
//            Log.d(TAG, " setInitialFocus secondLastVisibleItem"
//                    + secondLastVisibleItem + " " + layoutManager.getPosition(secondLastVisibleItem));
//            if (secondLastVisibleItem != null && layoutManager.getPosition(secondLastVisibleItem) > 14) {
//                secondLastVisibleItem.requestFocus(); // 设置焦点
//            }
//        }
//    }

    public NewFragment(List<AppInfoBean> appInfoBeans) {
        // Required empty public constructor
        this.appInfoBeans = appInfoBeans;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewFragment newInstance(String param1, String param2) {
        NewFragment fragment = new NewFragment(appInfoBeans);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        init();
        initReceiver();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (appReceiver != null) {
            getContext().unregisterReceiver(appReceiver);
        }
    }

    private void init() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        binding.appsRv.setLayoutManager(layoutManager);
        Log.d(TAG, "binding.appsRv.addItemDecoration " + this);
        binding.appsRv.addItemDecoration(new SpacesItemDecoration2(SpacesItemDecoration2.pxAdapter(22.8F), SpacesItemDecoration2.pxAdapter(22.6F),
                SpacesItemDecoration2.pxAdapter(22.5F), 0, SpacesItemDecoration2.pxAdapter(60F)));
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                appInfoBeans = AppUtils.getApplicationMsg(getContext());
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = appInfoBeans;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void initReceiver() {
        //app
        appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appFilter.addDataScheme("package");
        appReceiver = new AppReceiver(appCallBack);
        getContext().registerReceiver(appReceiver, appFilter);
    }

    private void enableFocus() {
        MainActivity activity = (MainActivity) getActivity();
        activity.htcosBinding.rlWifi.setFocusable(true);
        activity.htcosBinding.rlUsbConnect.setFocusable(true);
        activity.htcosBinding.rlBattery.setFocusable(true);
        activity.htcosBinding.rlBluetooth.setFocusable(true);
        activity.htcosBinding.rlSettings.setFocusable(true);
        activity.htcosBinding.rlWallpapers.setFocusable(true);
    }

    private void judageFocus() {
        RecyclerView.LayoutManager layoutManager = binding.appsRv.getLayoutManager();
        if (layoutManager != null) {
            boolean hasFocus = false;
            int childCount = layoutManager.getChildCount();
            // 检查 RecyclerView 的所有子项是否有焦点
            for (int i = 0; i < childCount; i++) {
                View child = layoutManager.getChildAt(i);
                if (child != null && child.hasFocus()) {
                    hasFocus = true;
                    break;
                }
            }
            // 如果没有子项有焦点，让倒数第二个子项获取焦点
            if (!hasFocus && childCount >= 2) {
                View secondLastVisibleItem = layoutManager.getChildAt(childCount - 2);
                if (secondLastVisibleItem != null) {
                    secondLastVisibleItem.requestFocus();
                }
            }
        }

    }
}