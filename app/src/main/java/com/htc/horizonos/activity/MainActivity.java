package com.htc.horizonos.activity;

import static com.htc.horizonos.utils.BlurImageView.MAX_BITMAP_SIZE;
import static com.htc.horizonos.utils.BlurImageView.narrowBitmap;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.htc.horizonos.MyApplication;
import com.htc.horizonos.databinding.ActivityMainHtcosBinding;
import com.htc.horizonos.fragment.NewFragment;
import com.htc.horizonos.fragment.OriginalFragment;
import com.htc.horizonos.receiver.AppCallBack;
import com.htc.horizonos.receiver.AppReceiver;
import com.htc.horizonos.receiver.BatteryReceiver;
import com.htc.horizonos.receiver.UsbDeviceCallBack;
import com.htc.horizonos.utils.BatteryCallBack;
import com.htc.horizonos.utils.BlurImageView;
import com.htc.horizonos.utils.FileUtils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Base64;
import com.htc.horizonos.utils.LogUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.horizonos.R;
import com.google.gson.Gson;
import com.htc.horizonos.adapter.ShortcutsAdapter;
import com.htc.horizonos.adapter.ShortcutsAdapterCustom;
import com.htc.horizonos.databinding.ActivityMainBinding;
import com.htc.horizonos.databinding.ActivityMainCustomBinding;
import com.htc.horizonos.entry.AppInfoBean;
import com.htc.horizonos.entry.AppSimpleBean;
import com.htc.horizonos.entry.AppsData;
import com.htc.horizonos.entry.ChannelData;
import com.htc.horizonos.entry.ShortInfoBean;
import com.htc.horizonos.manager.RequestManager;
import com.htc.horizonos.receiver.BluetoothCallBcak;
import com.htc.horizonos.receiver.BluetoothReceiver;
import com.htc.horizonos.receiver.MyTimeCallBack;
import com.htc.horizonos.receiver.MyTimeReceiver;
import com.htc.horizonos.receiver.MyWifiCallBack;
import com.htc.horizonos.receiver.MyWifiReceiver;
import com.htc.horizonos.receiver.NetWorkCallBack;
import com.htc.horizonos.receiver.NetworkReceiver;
import com.htc.horizonos.receiver.UsbDeviceReceiver;
import com.htc.horizonos.utils.AppUtils;
import com.htc.horizonos.utils.BluetoothUtils;
import com.htc.horizonos.utils.Constants;
import com.htc.horizonos.utils.Contants;
import com.htc.horizonos.utils.DBUtils;
import com.htc.horizonos.utils.ImageUtils;
import com.htc.horizonos.utils.LanguageUtil;
import com.htc.horizonos.utils.LogUtils;
import com.htc.horizonos.utils.NetWorkUtils;
import com.htc.horizonos.utils.ShareUtil;
import com.htc.horizonos.utils.SystemPropertiesUtil;
import com.htc.horizonos.utils.TimeUtils;
import com.htc.horizonos.utils.ToastUtil;
import com.htc.horizonos.utils.Uri;
import com.htc.horizonos.utils.Utils;
import com.htc.horizonos.utils.VerifyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseMainActivity implements BluetoothCallBcak, MyWifiCallBack, MyTimeCallBack, NetWorkCallBack, UsbDeviceCallBack, AppCallBack, BatteryCallBack {

    private ActivityMainBinding mainBinding;

    private ActivityMainCustomBinding customBinding;

    public ActivityMainHtcosBinding htcosBinding;
    private ArrayList<ShortInfoBean> short_list = new ArrayList<>();

    boolean get_default_url = false;
    private boolean isFrist = true;
    private ChannelData channelData;
    private List<AppsData> appsDataList;
    /**
     * receiver
     */

    private NetworkReceiver networkReceiver = null;
    // 时间
    private IntentFilter timeFilter = new IntentFilter();
    private MyTimeReceiver timeReceiver = null;
    // wifi
    private IntentFilter wifiFilter = new IntentFilter();
    private MyWifiReceiver wifiReceiver = null;
    // 蓝牙
    private IntentFilter blueFilter = new IntentFilter();
    //usbDevice
    private IntentFilter usbDeviceFilter = new IntentFilter();
    private BluetoothReceiver blueReceiver = null;
    //Usb 设备
    private UsbDeviceReceiver usbDeviceReceiver = null;

    //电池
    private BatteryReceiver batteryReceiver = null;

    private static String TAG = "MainActivity";

    public String appName = "";

    private boolean requestFlag = false;

    private final int DATA_ERROR = 102;
    private final int DATA_FINISH = 103;

    private Hashtable<String, String> hashtable = new Hashtable<>();

    private AppReceiver appReceiver = null;
    private WifiManager wifiManager = null;
    private StorageManager storageManager = null;

    ExecutorService threadExecutor = Executors.newFixedThreadPool(5);
    private List<StorageVolume> localDevicesList;
    private ConnectivityManager connectivityManager;

    public OriginalFragment originalFragment = null;

    public NewFragment newFragment = null;

    private List<AppInfoBean> appInfoBeans = null;

    public FragmentManager fragmentManager = getSupportFragmentManager();
    public FragmentTransaction transaction = fragmentManager.beginTransaction();

    Bundle savedInstanceState =null;
    private ConnectivityManager.NetworkCallback networkCallback;

    private boolean isEther = false;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 202:
                    ShortcutsAdapter shortcutsAdapter = new ShortcutsAdapter(MainActivity.this, short_list);
                    shortcutsAdapter.setItemCallBack(itemCallBack);
                    mainBinding.shortcutsRv.setAdapter(shortcutsAdapter);
                    break;
                case 204:
                    ShortcutsAdapterCustom shortcutsAdapterCustom = new ShortcutsAdapterCustom(MainActivity.this, short_list);
                    shortcutsAdapterCustom.setItemCallBack(itemCallBackCustom);
                    customBinding.shortcutsRv.setAdapter(shortcutsAdapterCustom);
                    break;
                case DATA_ERROR:
                    requestFlag = false;
                    ToastUtil.showShortToast(MainActivity.this, getString(R.string.data_err));
                    break;
                case DATA_FINISH:
                    requestFlag = false;
                    if (channelData != null && channelData.getData().size() > 0) {
                        startAppFormChannel();
                    }
                    break;
                case 0:
                    if (htcosBinding.rlEthernet.getVisibility() == View.VISIBLE) {
                        htcosBinding.rlEthernet.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if (htcosBinding.rlEthernet.getVisibility() == View.GONE) {
                        htcosBinding.rlEthernet.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        try {
            htcosBinding = ActivityMainHtcosBinding.inflate(LayoutInflater.from(this));
            setContentView(htcosBinding.getRoot());
            setDefaultBackgroundById();
            initViewCustom();
            initDataCustom();
            initReceiver();
            wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);
            storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            localDevicesList = new ArrayList<StorageVolume>();
            devicesPathAdd();
            LogUtils.d(TAG, " onCreate ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            updateTime();
            updateBle();
            if ((boolean) ShareUtil.get(this, Contants.MODIFY, false)) {
//                short_list = loadHomeAppData();
//            handler.sendEmptyMessage(202);
//                handler.sendEmptyMessage(204);
                ShareUtil.put(this, Contants.MODIFY, false);
            }
            LogUtils.d(TAG, " onResume快捷图标 short_list " + short_list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initViewCustom() {
        //1、状态栏
        // Wifi
        htcosBinding.rlWifi.setOnClickListener(this);
        htcosBinding.rlWifi.setOnHoverListener(this);
        htcosBinding.rlWifi.setOnFocusChangeListener(this);
        // 蓝牙
        htcosBinding.rlBluetooth.setOnClickListener(this);
        htcosBinding.rlBluetooth.setOnHoverListener(this);
        htcosBinding.rlBluetooth.setOnFocusChangeListener(this);
        // 切换背景
        htcosBinding.rlWallpapers.setOnClickListener(this);
        htcosBinding.rlWallpapers.setOnHoverListener(this);
        htcosBinding.rlWallpapers.setOnFocusChangeListener(this);
        // 电池状态
        initBattery();
        // U盘插入
        htcosBinding.rlUsbConnect.setOnClickListener(this);
        htcosBinding.rlUsbConnect.setOnHoverListener(this);
        htcosBinding.rlUsbConnect.setOnFocusChangeListener(this);
        // 设置
        htcosBinding.rlSettings.setOnClickListener(this);
        htcosBinding.rlSettings.setOnHoverListener(this);
        htcosBinding.rlSettings.setOnFocusChangeListener(this);
        // 信源
        htcosBinding.rlSignalSource.setOnClickListener(this);
        htcosBinding.rlSignalSource.setOnHoverListener(this);
        htcosBinding.rlSignalSource.setOnFocusChangeListener(this);
        //清除缓存
        htcosBinding.rlClearMemory.setOnClickListener(this);
        htcosBinding.rlClearMemory.setOnHoverListener(this);
        htcosBinding.rlClearMemory.setOnFocusChangeListener(this);

    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initDataApp();
                short_list = loadHomeAppData();
                handler.sendEmptyMessage(202);
            }
        }).start();
    }

    public void initBattery() {
        LogUtils.d(TAG, "电池状态 初始化");
        htcosBinding.rlBattery.setOnHoverListener(this);
        if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryEnable).equals("1")) {//是否有电池
            LogUtils.d(TAG, "电池状态 初始化 有电池");
            htcosBinding.rlBattery.setVisibility(View.VISIBLE);
            if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                LogUtils.d(TAG, "电池状态 初始化 正在充电");
                switch (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryLevel)) {
                    case "0":
                        htcosBinding.battery.setImageResource(R.drawable.battery_charging_1);
                        break;
                    case "1":
                        htcosBinding.battery.setImageResource(R.drawable.battery_charging_2);
                        break;
                    case "2":
                        htcosBinding.battery.setImageResource(R.drawable.battery_charging_3);
                        break;
                    case "3":
                        htcosBinding.battery.setImageResource(R.drawable.battery_charging_4);
                        break;
                    case "4":
                        htcosBinding.battery.setImageResource(R.drawable.battery_charging_5);
                        break;
                }
            } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                LogUtils.d(TAG, "电池状态 初始化 没充电");
                switch (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryLevel)) {
                    case "0":
                        htcosBinding.battery.setImageResource(R.drawable.battery_1);
                        break;
                    case "1":
                        htcosBinding.battery.setImageResource(R.drawable.battery_2);
                        break;
                    case "2":
                        htcosBinding.battery.setImageResource(R.drawable.battery_3);
                        break;
                    case "3":
                        htcosBinding.battery.setImageResource(R.drawable.battery_4);
                        break;
                    case "4":
                        htcosBinding.battery.setImageResource(R.drawable.battery_5);
                        break;
                }
            }
        } else {
            LogUtils.d(TAG, "电池状态 初始化 没有电池");
        }

    }

    @Override
    public void setBatteryLevel(String level) {
        LogUtils.d(TAG, "电池状态 setBatteryLevel");
        switch (level) {
            case "0":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_charging_1);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_1);
                }
                break;
            case "1":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_charging_2);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_2);
                }
                break;
            case "2":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_charging_3);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_3);
                }
                break;
            case "3":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_charging_4);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_4);
                }
                break;
            case "4":
                if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("1")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_charging_5);
                } else if (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryDc).equals("0")) {
                    htcosBinding.battery.setImageResource(R.drawable.battery_5);
                }
                break;
        }

    }

    @Override
    public void Plug_in_charger() {
        LogUtils.d(TAG, "电池状态 Plug_in_charger");
        switch (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryLevel)) {
            case "0":
                htcosBinding.battery.setImageResource(R.drawable.battery_charging_1);
                break;
            case "1":
                htcosBinding.battery.setImageResource(R.drawable.battery_charging_2);
                break;
            case "2":
                htcosBinding.battery.setImageResource(R.drawable.battery_charging_3);
                break;
            case "3":
                htcosBinding.battery.setImageResource(R.drawable.battery_charging_4);
                break;
            case "4":
                htcosBinding.battery.setImageResource(R.drawable.battery_charging_5);
                break;
        }
    }

    @Override
    public void Unplug_the_charger() {
        LogUtils.d(TAG, "电池状态 Unplug_the_charger");
        switch (SystemPropertiesUtil.getSystemProperty(SystemPropertiesUtil.batteryLevel)) {
            case "0":
                htcosBinding.battery.setImageResource(R.drawable.battery_1);
                break;
            case "1":
                htcosBinding.battery.setImageResource(R.drawable.battery_2);
                break;
            case "2":
                htcosBinding.battery.setImageResource(R.drawable.battery_3);
                break;
            case "3":
                htcosBinding.battery.setImageResource(R.drawable.battery_4);
                break;
            case "4":
                htcosBinding.battery.setImageResource(R.drawable.battery_5);
                break;
        }
    }

    private void initDataCustom() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //读取首页的配置文件，优先读取网络服务器配置，其次读本地配置。只读取一次，清除应用缓存可触发再次读取。
                initDataApp();
                LogUtils.d(TAG, " initDataCustom ");
            }
        }).start();
    }

    private void initReceiver() {
        IntentFilter networkFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        networkReceiver.setNetWorkCallBack(this);
        registerReceiver(networkReceiver, networkFilter);

        // 时间变化 分为单位
        timeReceiver = new MyTimeReceiver(this);
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        timeFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        timeFilter.addAction(Intent.ACTION_USER_SWITCHED);
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, timeFilter);

        // wifi
        wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiReceiver = new MyWifiReceiver(this);
        registerReceiver(wifiReceiver, wifiFilter);

        // 蓝牙
        blueFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        blueFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        blueReceiver = new BluetoothReceiver(this);
        registerReceiver(blueReceiver, blueFilter);

        //Usb设备插入、拔出
        usbDeviceReceiver = new UsbDeviceReceiver(this);
        usbDeviceFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbDeviceFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbDeviceFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        usbDeviceFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbDeviceFilter.addDataScheme("file");
        registerReceiver(usbDeviceReceiver, usbDeviceFilter);

        //APP安装、改变、卸载
        appReceiver = new AppReceiver(this);
        IntentFilter appFilter = new IntentFilter();
        appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appFilter.addDataScheme("package");
        registerReceiver(appReceiver, appFilter);

        //电量变化
        batteryReceiver = new BatteryReceiver(this, this);
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction("action.projector.dcin");
        batteryFilter.addAction("action.projector.batterylevel");
        registerReceiver(batteryReceiver, batteryFilter);
    }

    ShortcutsAdapter.ItemCallBack itemCallBack = new ShortcutsAdapter.ItemCallBack() {
        @Override
        public void onItemClick(int i) {
//            if (i < short_list.size()) {
//                if (short_list.get(i).getAppname() != null) {
//                    AppUtils.startNewApp(MainActivity.this, short_list.get(i).getPackageName());
//                } else if (appsDataList != null) {
//                    AppsData appsData = findAppsData(short_list.get(i).getPackageName());
//                    if (appsData != null) {
//                        Intent intent = new Intent();
//                        intent.setComponent(new ComponentName("com.htc.storeos", "com.htc.storeos.AppDetailActivity"));
//                        intent.putExtra("appData", new Gson().toJson(appsData));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    } else {
//                        ToastUtil.showShortToast(getBaseContext(), getString(R.string.data_none));
//                    }
//                } else {
//                    ToastUtil.showShortToast(getBaseContext(), getString(R.string.data_none));
//                }
//            } else {
//                AppUtils.startNewActivity(MainActivity.this, AppFavoritesActivity.class);
//            }
        }
    };

    ShortcutsAdapterCustom.ItemCallBack itemCallBackCustom = new ShortcutsAdapterCustom.ItemCallBack() {
        @Override
        public void onItemClick(int i, String name) {
            if (i < short_list.size()) {

                LogUtils.d(TAG, " xuhao执行点击前 " + i);
                if (i == 0) {
                    LogUtils.d(TAG, " 打开APP详情页");
                    startNewActivity(AppsActivity.class);
                    return;
                }
                LogUtils.d(TAG, " short_list.get(i).getPackageName() " + short_list.get(i).getPackageName());
                if (!AppUtils.startNewApp(MainActivity.this, short_list.get(i).getPackageName())) {
                    appName = name;
                    requestChannelData();
                }

            } else {
                AppUtils.startNewActivity(MainActivity.this, AppFavoritesActivity.class);
            }
        }
    };


//    public AppsData findAppsData(String pkg) {
//        for (AppsData appsData : appsDataList) {
//            if (appsData.getApp_id().equals(pkg))
//                return appsData;
//        }
//        return null;
//    }


    private void requestAppData() {
        String channel = SystemProperties.get("persist.sys.Channel", "project");
        String url = Uri.BASE_URL + channel + "/channel_apps_" + Locale.getDefault().getLanguage() + ".xml";
        RequestManager.getInstance().getData(url, channelCallback);
    }

    Callback channelCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
            LogUtils.d("onFailure()");
            handler.sendEmptyMessage(DATA_ERROR);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            try {
                String content = response.body().string();
                LogUtils.d("content " + content);
                if (RequestManager.isOne(Uri.complexType, 3)) {
                    byte[] bytes = Base64.decode(content, Base64.NO_WRAP);
                    content = new String(VerifyUtil.gzipDecompress(bytes), StandardCharsets.UTF_8);
                    LogUtils.d("content " + content);
                }
                channelData = new Gson().fromJson(content, ChannelData.class);
                if (channelData.getCode() != 0) {
                    handler.sendEmptyMessage(DATA_ERROR);
                } else {
                    handler.sendEmptyMessage(DATA_FINISH);
                }

            } catch (Exception e) {
                handler.sendEmptyMessage(DATA_ERROR);
            }
        }
    };

    private void responseErrorRedirect() {
        if (!get_default_url) {
            get_default_url = true;
            String channel = SystemProperties.get("persist.sys.Channel", "project");
            String url = Uri.BASE_URL + channel + "/channel_apps_global.xml";
            RequestManager.getInstance().getData(url, channelCallback);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_wallpapers) {
            startNewActivity(WallPaperActivity.class);
        } else if (id == R.id.rl_settings) {
            startNewActivity(MainSettingActivity.class);
        } else if (id == R.id.rl_wifi) {
            startNewActivity(WifiActivity.class);
        } else if (id == R.id.rl_bluetooth) {
            startNewActivity(BluetoothActivity.class);
        } else if (id == R.id.rl_usb_connect) {
            AppUtils.startNewApp(MainActivity.this, "com.hisilicon.explorer");
        } else if (id == R.id.rl_signal_source) {
            try {
                String listaction = DBUtils.getInstance(getApplicationContext()).getActionFromListModules("list2");
                if (listaction != null && !listaction.equals("")) { //读取配置
                    goAction(listaction);
                } else {// 默认跳转
                    startSource("HDMI1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.rl_clear_memory) {
            goAction("com.htc.clearmemory/com.htc.clearmemory.MainActivity");
        }
    }

    public void goAction(String listaction) {
        LogUtils.d(TAG, " goAction list配置跳转 " + listaction);
        if (listaction.contains("/")) {
            String[] parts = listaction.split("/", 2);
            String packageName = parts[0];
            String activityName = parts[1];
            LogUtils.d(TAG, " goAction 包名活动名 " + packageName + " " + activityName);
            startNewActivity(packageName, activityName);
        } else if (listaction.equals("HDMI1") || listaction.equals("HDMI2") || listaction.equals("VGA") || listaction.equals("CVBS1")) {
            startSource(listaction);
        } else {
            AppUtils.startNewApp(MainActivity.this, listaction);
        }
    }

    public void startSource(String sourceName) {
        Intent intent_hdmi = new Intent();
        intent_hdmi.setComponent(new ComponentName("com.softwinner.awlivetv", "com.softwinner.awlivetv.MainActivity"));
        intent_hdmi.putExtra("input_source", sourceName);
        intent_hdmi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent_hdmi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent_hdmi);
    }

    /**
     * 第一次初始化默认快捷栏app数据
     */
    private boolean initDataApp() {
        boolean isLoad = true;
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int code = sharedPreferences.getInt("code", 0);
        LogUtils.d(TAG, " initDataApp读code值 " + code);
        if (code == 0) {  //保证配置文件只在最初读一次
            //1、优先连接服务器读取配置

            //2、服务器没有，就读本地
            LogUtils.d(TAG, " MainActivity开始读取配置文件 ");

            // 读取文件,优先读取oem分区
            File file = new File("/oem/shortcuts.config");
            if (!file.exists()) {
                file = new File("/system/shortcuts.config");
            }
            if (!file.exists()) {
                LogUtils.d(TAG, " 配置文件不存在 ");
                DBUtils.getInstance(this).deleteTable();
                editor.putInt("code", 1);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 设置首页的配置图标
                        try {
                            LogUtils.d(TAG, " readListModules originalFragment信息 " + originalFragment + " newFragment " + newFragment + " transaction " + transaction);
                            if (savedInstanceState == null) {
                                originalFragment = new OriginalFragment();
                                // 添加初始 Fragment
                                transaction.add(R.id.fragment_container, originalFragment, "ORIGINAL_FRAGMENT_TAG");
                                initNewFragment();
                            } else {
                                originalFragment = (OriginalFragment) getSupportFragmentManager().findFragmentByTag("ORIGINAL_FRAGMENT_TAG");
                                newFragment = (NewFragment) getSupportFragmentManager().findFragmentByTag("NEW_FRAGMENT_TAG");
                            }
                            transaction.show(originalFragment).hide(newFragment)
                                    .commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
            try {
                FileInputStream is = new FileInputStream(file);
                byte[] b = new byte[is.available()];
                is.read(b);
                String result = new String(b);
                LogUtils.d(TAG, " MainActivity读取到的配置文件 " + result); //这里把配置文件原封不动的读取出来，不做一整行处理
                List<String> residentList = new ArrayList<>();
                JSONObject obj = new JSONObject(result);

                //读取默认背景配置 这块提前放到MyApplication中
//                readDefaultBackground(obj);

                //读取filterApps屏蔽显示的APP
                readFilterApps(obj);

                //读取首页底部6个APP图标
                readMain(obj);

                //读取APP快捷图标 HtcOs暂时不需要
//                readShortcuts(obj, residentList, sharedPreferences);

                //读取首页第一行四个功能区
                readListModules(obj);
                LogUtils.d(TAG, " 当前的语言环境是： " + LanguageUtil.getCurrentLanguage());

                //读取品牌图标 HtcOs暂时不需要
//                readBrand(obj);

                //是否显示时间 HtcOs暂时不需要
                //readTime();

                editor.putString("resident", residentList.toString());
                editor.putInt("code", 1);
                editor.apply();
                is.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isLoad = false;
            }
        }

        //设置首页的配置图标
        // 在主线程中更新 UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 设置首页的配置图标
                try {
                    LogUtils.d(TAG, " readListModules originalFragment信息 " + originalFragment + " newFragment " + newFragment + " transaction " + transaction);
                    if (savedInstanceState == null) {
                        originalFragment = new OriginalFragment();
                        // 添加初始 Fragment
                        transaction.add(R.id.fragment_container, originalFragment, "ORIGINAL_FRAGMENT_TAG");
                        initNewFragment();
                    } else {
                        originalFragment = (OriginalFragment) getSupportFragmentManager().findFragmentByTag("ORIGINAL_FRAGMENT_TAG");
                        newFragment = (NewFragment) getSupportFragmentManager().findFragmentByTag("NEW_FRAGMENT_TAG");
                    }
                    transaction.show(originalFragment).hide(newFragment)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return isLoad;
    }

//    private void readDefaultBackground(JSONObject obj) {
//        try {
//            if (obj.has("defaultbackground")) {
//                String DefaultBackground = obj.getString("defaultbackground").trim();
//                LogUtils.d(TAG, " readDefaultBackground " + DefaultBackground);
//                // 将字符串存入数据库；
//                SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(Contants.DefaultBg, DefaultBackground);
//                editor.apply();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void readMain(JSONObject obj) {
        try {
            if (obj.has("mainApp")) {
                JSONArray jsonarrray = obj.getJSONArray("mainApp");

                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String tag = jsonobject.getString("tag");
                    String appName = jsonobject.getString("appName");
                    String iconPath = jsonobject.getString("iconPath");
                    String action = jsonobject.getString("action");

                    LogUtils.d(TAG, " 读取到的mainApp " + tag + appName + iconPath + action);

                    //从iconPath中把png读出来赋值给drawable
                    Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);

                    //把读到的数据放入db数据库
                    DBUtils.getInstance(this).insertMainAppData(tag, appName, drawable, action);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

//    private void readShortcuts(JSONObject obj, List<String> residentList, SharedPreferences sharedPreferences) {
//        try {
//            if (obj.has("apps")) {
//                JSONArray jsonarrray = obj.getJSONArray("apps");
//                //xuhao
//                //用户每次更新配置，必须把原来数据库中保存的上一次失效的数据清除掉
//                ArrayList<AppSimpleBean> mylist = DBUtils.getInstance(this).getFavorites();
//                for (int i = 0; i < jsonarrray.length(); i++) {
//                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
//                    String packageName = jsonobject.getString("packageName");
//
//                    for (int d = 0; d < mylist.size(); d++) {
//                        LogUtils.d(TAG, " 对比 " + mylist.get(d).getPackagename() + " " + packageName);
//                        if (mylist.get(d).getPackagename().equals(packageName)) { //去除掉两个队列中相同的部分
//                            LogUtils.d(TAG, " 移除两个队列中的相同部分 " + packageName + mylist.size());
//                            mylist.remove(d);
//                            LogUtils.d(TAG, " mylist.size " + mylist.size());
//                            break;
//                        }
//                    }
//                }
//                for (int d = 0; d < mylist.size(); d++) { //剩余的不同的就是无效的，把无效的delet，保证每次修改配置之后都正确生效
//                    if (sharedPreferences.getString("resident", "").contains(mylist.get(d).getPackagename())) {
//                        LogUtils.d(TAG, " 移除APP快捷图标栏废弃的配置 ");
//                        DBUtils.getInstance(this).deleteFavorites(mylist.get(d).getPackagename());
//                    }
//                }
//                //xuhao
//                for (int i = 0; i < jsonarrray.length(); i++) {
//                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
//                    String appName = jsonobject.getString("appName");
//                    String packageName = jsonobject.getString("packageName");
//                    String iconPath = jsonobject.getString("iconPath");
//                    boolean resident = jsonobject.getBoolean("resident"); //用于标志移除上一轮配置文件和这一轮配置文件不需要的App
//                    if (resident) {
//                        residentList.add(packageName);
//                    }
//                    Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
//                    if (!DBUtils.getInstance(this).isExistData(
//                            packageName)) {
//                        long addCode = DBUtils.getInstance(this)
//                                .addFavorites(appName, packageName, drawable);
//                        LogUtils.d(TAG, " Shortcuts 添加快捷数据库成功 " + appName + " " + packageName);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void readFilterApps(JSONObject obj) {
        try {
            if (obj.has("filterApps")) {
                String filterApps = obj.getString("filterApps");
                LogUtils.d(TAG, " readFilterApps " + filterApps);
                // 将字符串按分号拆分成数组
                String[] packageNames = filterApps.split(";");
                DBUtils.getInstance(this).insertFilterApps(packageNames);
            }
//            initNewFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readListModules(JSONObject obj) {
        try {
            if (obj.has("listModules")) {
                JSONArray jsonarrray = obj.getJSONArray("listModules");
                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String tag = jsonobject.getString("tag");
                    String iconPath = jsonobject.getString("iconPath");
                    String action = jsonobject.getString("action");
                    JSONObject textObject = jsonobject.getJSONObject("text");
                    JSONArray keys = textObject.names();
                    LogUtils.d(TAG, " 读取到的listModules keys " + keys);
                    if (keys != null) {
                        for (int b = 0; b < keys.length(); b++) {
                            String key = keys.getString(b);
                            String value = textObject.getString(key);
                            LogUtils.d(TAG, " 读取到的listModules " + tag + iconPath + key + value);
                            hashtable.put(key, value);
                        }
                    }
                    //从iconPath中把png读出来赋值给drawable
                    Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
                    //将读取到的数据写入数据库
                    DBUtils.getInstance(this).insertListModulesData(tag, drawable, hashtable, action);
                    hashtable.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void readBrand(JSONObject obj) {
//        try {
//            if (obj.has("brandLogo")) {
//                JSONObject jsonobject = obj.getJSONObject("brandLogo");
//                String iconPath = jsonobject.getString("iconPath");
//                Drawable drawable = FileUtils.loadImageAsDrawable(this, iconPath);
//                DBUtils.getInstance(this).insertBrandLogoData(drawable);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private ArrayList<ShortInfoBean> loadHomeAppData() {
        ArrayList<AppSimpleBean> appSimpleBeans = DBUtils.getInstance(this).getFavorites(); //获取配置文件中设置的首页显示App
        ArrayList<ShortInfoBean> shortInfoBeans = new ArrayList<>();
        ArrayList<AppInfoBean> appList = AppUtils.getApplicationMsg(this);//获取所有的应用(排除了配置文件中拉黑的App)
        //xuhao add 默认添加我的应用按钮
        ShortInfoBean mshortInfoBean = new ShortInfoBean();
        mshortInfoBean.setAppicon(ContextCompat.getDrawable(this, R.drawable.home_app_manager));
        shortInfoBeans.add(mshortInfoBean);
        //xuhao
        LogUtils.d(TAG, " loadHomeAppData快捷图标 appList " + appList.size());
        LogUtils.d(TAG, " loadHomeAppData快捷图标 appSimpleBeans " + appSimpleBeans.size());
        for (int i = 0; i < appSimpleBeans.size(); i++) {
            ShortInfoBean shortInfoBean = new ShortInfoBean();
            shortInfoBean.setPackageName(appSimpleBeans.get(i).getPackagename());
            for (int j = 0; j < appList.size(); j++) {
                if (appSimpleBeans.get(i).getPackagename()
                        .equals(appList.get(j).getApppackagename())) {
                    shortInfoBean.setAppicon(appList.get(j).getAppicon());
                    shortInfoBean.setAppname(appList.get(j).getAppname());
                    break;
                }
            }
            shortInfoBeans.add(shortInfoBean);
        }
        return shortInfoBeans;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK) { //NewFragment BACK键返回 OriginalFragment
            if(newFragment != null) {
                LogUtils.d(TAG, " dispatchKeyEvent测试 KEYCODE_BACK" + event.getKeyCode() + " newFragment.isVisible " + newFragment.isVisible());
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && newFragment.isVisible()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_reverse, R.anim.slide_out_reverse)
                            .show(originalFragment).hide(newFragment)
                            .commit();
                    enableFocus();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        LogUtils.d(TAG," 用户按下Home键 ");
//        if(newFragment == null) {
//            return;
//        }
//        if (newFragment.isVisible()) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .setReorderingAllowed(true)
//                    .setCustomAnimations(R.anim.slide_in_reverse, R.anim.slide_out_reverse)
//                    .show(originalFragment).hide(newFragment)
//                    .commit();
//            enableFocus();
//        }
//    }

    private void updateBle() {
        boolean isConnected = BluetoothUtils.getInstance(this)
                .isBluetoothConnected();
        if (isConnected) {
            htcosBinding.homeBluetooth.setImageResource(R.drawable.bt_custom_green);
        } else {
            htcosBinding.homeBluetooth.setImageResource(R.drawable.bt_custom2);
        }
    }

    private void updateTime() {
        String builder = TimeUtils.getCurrentTime(this) +
                "|";
//        mainBinding.timeTv.setText(builder);
        LogUtils.d(TAG," updateTime时间更新 "+TimeUtils.getCurrentTime(this)+" builder "+builder);
        htcosBinding.time.setText(builder);
        htcosBinding.calendar.setText(TimeUtils.getCurrentDate());
        htcosBinding.week.setText(TimeUtils.getShortWeekDay());
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(networkReceiver);
        unregisterReceiver(timeReceiver);
        unregisterReceiver(blueReceiver);
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(usbDeviceReceiver);
        unregisterReceiver(appReceiver);
        unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }

    @Override
    public void bluetoothChange() {
        updateBle();
    }

    @Override
    public void UsbDeviceChange() {
        LogUtils.d("UsbDeviceChange ", String.valueOf(Utils.hasUsbDevice));
        if (Utils.hasUsbDevice) {
            LogUtils.d("UsbDeviceChange ", "usbConnect设为VISIBLE");
            htcosBinding.rlUsbConnect.setVisibility(View.VISIBLE);
        } else {
            htcosBinding.rlUsbConnect.clearFocus();
            htcosBinding.rlUsbConnect.clearAnimation();
            htcosBinding.rlUsbConnect.setVisibility(View.GONE);
            LogUtils.d("UsbDeviceChange ", "usbConnect设为GONE");
        }
    }

    @Override
    public void changeTime() {
        updateTime();
    }

    @Override
    public void getWifiState(int state) {
        if (state == 1) {
//            mainBinding.homeWifi.setBackgroundResource(R.drawable.wifi_not);
            htcosBinding.homeWifi.setImageResource(R.drawable.wifi_custom_4);
        }
    }


    @Override
    public void getWifiNumber(int count) {
        List<ScanResult> wifiList = wifiManager.getScanResults();
        LogUtils.d(TAG, "getWifiNumber " + count);
        if (count == 1) {
            htcosBinding.homeWifi.setImageResource(R.drawable.wifi_custom_4);
            return;
        } else if (count == 3) {
            htcosBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_4);
            return;
        }
        LogUtils.d(TAG, " level数据" + count);
        if (count < -85) {
            htcosBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_1);
        } else if (count < -70) {
            htcosBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_2);
        } else if (count < -60) {
            htcosBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_3);
        } else if (count < -50) {
            htcosBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_4);
        } else {
            htcosBinding.homeWifi.setImageResource(R.drawable.wifi_custom_green_4);
        }
    }

    @Override
    public void connect() {
    }

    @Override
    public void disConnect() {
    }

    public void requestChannelData() {
        if (requestFlag)
            return;
        if (!NetWorkUtils.isNetworkConnected(this)) {
            ToastUtil.showShortToast(this, getString(R.string.network_disconnect_tip));
            return;
        }
        requestFlag = true;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        String time = String.valueOf(System.currentTimeMillis());
        String chan = Constants.getChannel();
        LogUtils.d("chanId " + chan);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("chanId", chan);
        requestBuilder.addHeader("timestamp", time);
        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("chanId", chan);
        String deviceId = Constants.getWan0Mac();
        if (Constants.isOne(Uri.complexType, 1)) {
            String aesKey = VerifyUtil.initKey();
            LogUtils.d("aesKey " + aesKey);
            deviceId = VerifyUtil.encrypt(deviceId, aesKey, aesKey, VerifyUtil.AES_CBC);
            LogUtils.d("deviceId " + deviceId);
        }
        requestData.put("deviceId", deviceId);
        requestData.put("model", SystemProperties.get("persist.sys.modelName", "project"));
        requestData.put("sysVersion", Constants.getHtcDisplay());
        try {
            requestData.put("verCode", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            requestData.put("verCode", 10);
            throw new RuntimeException(e);
        }
        requestData.put("complexType", Uri.complexType);//
        Gson gson = new Gson();
        String json = gson.toJson(requestData);
        requestBuilder.url(Uri.SIGN_APP_LIST_URL)
                .post(RequestBody.create(json, MediaType.parse("application/json;charset=UTF-8")));
        String sign = RequestManager.getInstance().getSign(json, chan, time);
        LogUtils.d("sign " + sign);
        requestBuilder.addHeader("sign", sign);
        Request request = requestBuilder.build();
        okHttpClient.newCall(request).enqueue(channelCallback);
    }

    private void startAppFormChannel() {
        for (AppsData appsData : channelData.getData()) {
            if (appName.equals(appsData.getName())) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.htc.storeos", "com.htc.storeos.AppDetailActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("appData", new Gson().toJson(appsData));
                startActivity(intent);
                return;
            }
        }
        ToastUtil.showShortToast(this, getString(R.string.data_none));
    }

//    private void setIconOrText() {
//        setDefaultBackground();
//    }

//    private void setMainApp() {
//        Drawable drawable = DBUtils.getInstance(this).getIconDataByTag("icon1");
//        if (drawable != null) {
//            customBinding.icon1.setImageDrawable(drawable);
//        }
//
//        drawable = DBUtils.getInstance(this).getIconDataByTag("icon2");
//        if (drawable != null) {
//            customBinding.icon2.setImageDrawable(drawable);
//        }
//
//        drawable = DBUtils.getInstance(this).getIconDataByTag("icon3");
//        if (drawable != null) {
//            customBinding.icon3.setImageDrawable(drawable);
//        }
//
//        drawable = DBUtils.getInstance(this).getIconDataByTag("icon4");
//        if (drawable != null) {
//            customBinding.icon4.setImageDrawable(drawable);
//        }
//    }

//    private void setListModules() {
//        Drawable drawable = DBUtils.getInstance(this).getDrawableFromListModules("list1");
//        if (drawable != null) {
//            customBinding.eshareIcon.setImageDrawable(drawable);
//            drawable = null;
//        }
//        drawable = DBUtils.getInstance(this).getDrawableFromListModules("list3");
//        if (drawable != null) {
//            customBinding.hdmiIcon.setImageDrawable(drawable);
//            drawable = null;
//        }
//        Hashtable<String, String> mHashtable1 = DBUtils.getInstance(this).getHashtableFromListModules("list1");
//        Hashtable<String, String> mHashtable2 = DBUtils.getInstance(this).getHashtableFromListModules("list3");
//        LogUtils.d(TAG, "xu当前语言" + LanguageUtil.getCurrentLanguage());
//        if (mHashtable1 != null) {
//            String text = null;
//            switch (LanguageUtil.getCurrentLanguage()) {
//                case "zh-CN":
//                    LogUtils.d(TAG, "中文设置eshareText和hdmiText");
//                    text = mHashtable1.get("zh-CN");
//                    if (text != null && !text.equals("")) {
//                        customBinding.eshareText.setText(mHashtable1.get("zh-CN"));
//                    }
//                    break;
//                case "zh-TW":
//                    text = mHashtable1.get("zh-TW");
//                    if (text != null && !text.equals("")) {
//                        customBinding.eshareText.setText(mHashtable1.get("zh-TW"));
//                    }
//                    break;
//                case "zh-HK":
//                    text = mHashtable1.get("zh-HK");
//                    if (text != null && !text.equals("")) {
//                        customBinding.eshareText.setText(mHashtable1.get("zh-HK"));
//                    }
//                    break;
//                case "ko-KR":
//                    text = mHashtable1.get("ko-KR");
//                    if (text != null && !text.equals("")) {
//                        customBinding.eshareText.setText(mHashtable1.get("ko-KR"));
//                    }
//                    break;
//                case "ja-JP":
//                    text = mHashtable1.get("ja-JP");
//                    if (text != null && !text.equals("")) {
//                        customBinding.eshareText.setText(mHashtable1.get("ja-JP"));
//                    }
//                    break;
//                case "en-US":
//                    text = mHashtable1.get("en-US");
//                    if (text != null && !text.equals("")) {
//                        customBinding.eshareText.setText(mHashtable1.get("en-US"));
//                    }
//                    break;
//                case "ru-RU":
//                    text = mHashtable1.get("ru-RU");
//                    if (text != null && !text.equals("")) {
//                        customBinding.eshareText.setText(mHashtable1.get("ru-RU"));
//                    }
//                    break;
//                case "ar-EG":
//                    text = mHashtable1.get("ar-EG");
//                    if (text != null && !text.equals("")) {
//                        customBinding.eshareText.setText(mHashtable1.get("ar-EG"));
//                    }
//                    break;
//            }
//        }
//        if (mHashtable2 != null) {
//            String text = null;
//            switch (LanguageUtil.getCurrentLanguage()) {
//                case "zh-CN":
//                    LogUtils.d(TAG, "中文设置eshareText和hdmiText");
//                    text = mHashtable2.get("zh-CN");
//                    if (text != null && !text.equals("")) {
//                        customBinding.hdmiText.setText(mHashtable2.get("zh-CN"));
//                    }
//                    break;
//                case "zh-TW":
//                    text = mHashtable2.get("zh-TW");
//                    if (text != null && !text.equals("")) {
//                        customBinding.hdmiText.setText(mHashtable2.get("zh-TW"));
//                    }
//                    break;
//                case "zh-HK":
//                    text = mHashtable2.get("zh-HK");
//                    if (text != null && !text.equals("")) {
//                        customBinding.hdmiText.setText(mHashtable2.get("zh-HK"));
//                    }
//                    break;
//                case "ko-KR":
//                    text = mHashtable2.get("ko-KR");
//                    if (text != null && !text.equals("")) {
//                        customBinding.hdmiText.setText(mHashtable2.get("ko-KR"));
//                    }
//                    break;
//                case "ja-JP":
//                    text = mHashtable2.get("ja-JP");
//                    if (text != null && !text.equals("")) {
//                        customBinding.hdmiText.setText(mHashtable2.get("ja-JP"));
//                    }
//                    break;
//                case "en-US":
//                    text = mHashtable2.get("en-US");
//                    if (text != null && !text.equals("")) {
//                        customBinding.hdmiText.setText(mHashtable2.get("en-US"));
//                    }
//                    break;
//                case "ru-RU":
//                    text = mHashtable2.get("ru-RU");
//                    if (text != null && !text.equals("")) {
//                        customBinding.hdmiText.setText(mHashtable2.get("ru-RU"));
//                    }
//                    break;
//                case "ar-EG":
//                    text = mHashtable2.get("ar-EG");
//                    if (text != null && !text.equals("")) {
//                        customBinding.hdmiText.setText(mHashtable2.get("ar-EG"));
//                    }
//                    break;
//            }
//        }
//    }

//    private void setbrandLogo() {
//        Drawable drawable = DBUtils.getInstance(this).getDrawableFromBrandLogo(1);
//        if (drawable != null) {
//            customBinding.brand.setImageDrawable(drawable);
//        }
//    }

//    private void setDefaultBackground() {
//        //如果用户自主修改了背景，那么重启之后不再设置默认背景start
//        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
//        int selectBg = sharedPreferences.getInt(Contants.SelectWallpaperLocal, -1);
//        if (selectBg != -1) {
//            LogUtils.d(TAG, " setDefaultBackground 用户已经自主修改了背景");
//            return;
//        }
//        //背景控制end
//        String defaultbg = sharedPreferences.getString(Contants.DefaultBg, "1");
//        LogUtils.d(TAG, " setDefaultBackground defaultbg " + defaultbg);
//        int number = Integer.parseInt(defaultbg);
//        LogUtils.d(TAG, " setDefaultBackground number " + number);
//        if (number > Utils.drawables.size()) {
//            LogUtils.d(TAG, " setDefaultBackground 用户设置的默认背景，超出了范围");
//            return;
//        }
//        MyApplication.mainDrawable = (BitmapDrawable) Utils.drawables.get(number - 1);
//        setWallPaper(Utils.drawables.get(number - 1));
//        setDefaultBg(Utils.drawables.get(number - 1));
//    }

    private void setDefaultBg(Drawable drawable) {
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CopyDrawableToSd(drawable);
            }
        });
    }

    @Override
    public void appChange(String packageName) {
        LogUtils.d(TAG, "MainActivity 收到Change广播");
    }

    @Override
    public void appUnInstall(String packageName) {
        LogUtils.d(TAG, "MainActivity 收到卸载广播 " + packageName);
        SharedPreferences sp = ShareUtil.getInstans(this);
        SharedPreferences.Editor ed = sp.edit();
        String resident = sp.getString("resident", "");
        if (resident.contains(packageName)) {
            LogUtils.d(TAG, " 配置文件中apps：\"resident\":true 常驻首页前台，应用删除了，也不能从首页APP快捷栏移除");
            return;
        }
        DBUtils.getInstance(this).deleteFavorites(packageName);
    }

    @Override
    public void appInstall(String packageName) {
        LogUtils.d(TAG, "MainActivity 收到安装广播");
    }

//    private void CopyResIdToSd(int resId) {
//        File file = new File(Contants.WALLPAPER_DIR);
//        if (!file.exists())
//            file.mkdir();
//        InputStream inputStream = getResources().openRawResource(resId);
//        try {
//            File file1 = new File(Contants.WALLPAPER_MAIN);
//            if (file1.exists())
//                file1.delete();
//
//            FileOutputStream fileOutputStream = new FileOutputStream(file1);
//
//            byte[] buf = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buf)) != -1) {
//                fileOutputStream.write(buf, 0, bytesRead);
//            }
//            fileOutputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void CopyResIdToSd(Bitmap bitmap) {
//        File file1 = new File(Contants.WALLPAPER_DIR);
//        if (!file1.exists())
//            file1.mkdir();
//
//        File file = new File(Contants.WALLPAPER_OTHER);//将要保存图片的路径
//        if (file.exists())
//            file.delete();
//        try {
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//            bos.flush();
//            bos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 开机检测是否有USB插入
     */
//    private void checkUsb() {
//        Intent usbStateIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_MEDIA_MOUNTED));
//        if (usbStateIntent == null) {
//            return false;
//        }
//        final String usbAction = usbStateIntent.getAction();
//        if (Intent.ACTION_MEDIA_MOUNTED.equals(usbAction)) {
//            Bundle extras = usbStateIntent.getExtras();
//            boolean connected = extras.getBoolean(UsbManager.USB_CONNECTED);
//            if (connected) {
//                LogUtils.d(TAG, " 开机检测到U盘插入 ");
//                customBinding.rlUsbConnect.setVisibility(View.VISIBLE);
//            } else {
//                LogUtils.d(TAG, " 开机没有检测到U盘插入 ");
//            }
//        }
//        return true;

//        int usbCount = countUsbDevices(getApplicationContext());
//        if (usbCount != 0) {
//            LogUtils.d(TAG, "checkUsb  开机检测到U盘 " + Utils.usbDevicesNumber);
//            customBinding.rlUsbConnect.setVisibility(View.VISIBLE);
//            Utils.usbDevicesNumber = usbCount * 2;
//            LogUtils.d(TAG, "checkUsb  开机检测到U盘 usbCount*2 " + Utils.usbDevicesNumber);
//        } else {
//            LogUtils.d(TAG, "checkUsb  开机没有检测到U盘");
//        }
//    }


//    public boolean isUsbMounted() {
//        String state = Environment.getExternalStorageState();
//        return Environment.MEDIA_MOUNTED.equals(state);
//    }
//
//    public int countUsbDevices(Context context) {
//        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//        List<StorageVolume> volumes = storageManager.getStorageVolumes();
//        int usbCount = 0;
//
//        for (StorageVolume volume : volumes) {
//            if (volume.isRemovable()) {
//                usbCount++;
//            }
//        }
//        LogUtils.d(TAG, "checkUsb  开机检测到 " + usbCount + " 个U盘");
//        return usbCount;
//    }

    private void CopyDrawableToSd(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        //判断图片大小，如果超过限制就做缩小处理
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width * height * 4 >= MAX_BITMAP_SIZE) {
            bitmap = narrowBitmap(bitmap);
        }
        //缩小完毕
        MyApplication.mainDrawable = new BitmapDrawable(bitmap);
        File dir = new File(Contants.WALLPAPER_DIR);
        if (!dir.exists()) dir.mkdirs();
        File file1 = new File(Contants.WALLPAPER_MAIN);
//        if (file1.exists()) file1.delete();
        try (FileOutputStream fileOutputStream = new FileOutputStream(file1)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream); // 可根据需要更改格式
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAppsBg() {
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_1));
        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_2));
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_3));
        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_4));
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_5));
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_6));
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_7));
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_8));
        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_9));
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_10));
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_11));
//        Utils.appsBgDrawables.add(getResources().getDrawable(R.drawable.apps_rectangle_12));
    }

    private void initNewFragment() {
        appInfoBeans = AppUtils.getApplicationMsg(getApplicationContext());
        initAppsBg();
        newFragment = new NewFragment(appInfoBeans);
        transaction.add(R.id.fragment_container, newFragment, "NEW_FRAGMENT_TAG");
    }

    private void enableFocus() {
        htcosBinding.rlWifi.setFocusable(true);
        htcosBinding.rlUsbConnect.setFocusable(true);
        htcosBinding.rlBattery.setFocusable(true);
        htcosBinding.rlBluetooth.setFocusable(true);
        htcosBinding.rlSettings.setFocusable(true);
        htcosBinding.rlWallpapers.setFocusable(true);
        htcosBinding.rlSignalSource.setFocusable(true);
    }

    private void devicesPathAdd() {
        if (storageManager == null) {
            LogUtils.e(TAG, "devicesPathAdd manager is null return error!");
            return;
        }
        localDevicesList = storageManager.getStorageVolumes();
        LogUtils.d(TAG, " 检测到devicesPathAdd " + localDevicesList.size());
        StorageVolume storageVolume;
        for (int i = 0; i < localDevicesList.size(); i++) {
            storageVolume = localDevicesList.get(i);
//            LogUtils.d(TAG," 检测到storageVolume.getPath() "+storageVolume.getPath()+" "+Environment.getExternalStorageDirectory().getPath());
            if (!storageVolume.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                if (storageVolume.getId().startsWith("public:179")) {
                    /* 获取SD卡设备路径列表 */
                    LogUtils.d(TAG, " 检测到SD卡 " + storageVolume.getPath());
                } else if (storageVolume.getId().startsWith("public:8")) {
                    /* 获取USB设备路径列表 */
                    Utils.hasUsbDevice = true;
                    Utils.usbDevicesNumber += 2;
                    if (customBinding.rlUsbConnect.getVisibility() == View.GONE) {
                        customBinding.rlUsbConnect.setVisibility(View.VISIBLE);
                    }
                    LogUtils.d(TAG, " 检测到USB设备 " + storageVolume.getPath() + " Utils.hasUsbDevice " + Utils.hasUsbDevice
                            + " Utils.usbDevicesNumber " + Utils.usbDevicesNumber);
                } else if (storageVolume.getPath().contains("sata")) {
                    /* 获取sata设备路径列表 */
                    LogUtils.d(TAG, " 检测到sata设备 " + storageVolume.getPath());
                }
            }
        }
    }

    /**
     * android 11 检测以太网连接
     *
     * @param context
     * @return
     */
//    @SuppressLint("MissingPermission")
//    public boolean isEthernetConnect(Context context) {
//        try {
//            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (connectivityManager != null) {
//                // 以太网已连接
//                // 以太网已断开
//                networkCallback = new ConnectivityManager.NetworkCallback() {
//                    @Override
//                    public void onAvailable(Network network) {
//                        super.onAvailable(network);
//                        // 以太网已连接
//                        isEther = true;
//                        handler.sendEmptyMessage(1);
////                    Message msg = new Message();
////                    msg.what = ETHERNET_HANDLE;
////                    msg.arg1 = 1;
////                    mHandler.sendMessage(msg);
//                    }
//
//                    @Override
//                    public void onLost(Network network) {
//                        super.onLost(network);
//                        // 以太网已断开
//                        isEther = false;
//                        handler.sendEmptyMessage(0);
////                    Message msg2 = new Message();
////                    msg2.what = ETHERNET_HANDLE;
////                    msg2.arg1 = 0;
////                    mHandler.sendMessage(msg2);
//                    }
//                };
//                NetworkRequest request = new NetworkRequest.Builder()
//                        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
//                        .build();
//                connectivityManager.registerNetworkCallback(request, networkCallback);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isEther;
//    }

//    @SuppressLint("UseCompatLoadingForDrawables")
//    private void setDefaultBackgroundById() {
//        //如果用户自主修改了背景，那么重启之后不再设置默认背景start
//        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
//        int selectBg = sharedPreferences.getInt(Contants.SelectWallpaperLocal, -1);
//        if (selectBg != -1) {
//            LogUtils.d(TAG, " setDefaultBackground 用户已经自主修改了背景");
//            return;
//        }
//        //背景控制end
//        String defaultbg = sharedPreferences.getString(Contants.DefaultBg, "1");
//        LogUtils.d(TAG, " setDefaultBackground defaultbg " + defaultbg);
//        int number = Integer.parseInt(defaultbg);
//        LogUtils.d(TAG, " setDefaultBackground number " + number);
//        if (number > Utils.drawablesId.length) {
//            LogUtils.d(TAG, " setDefaultBackground 用户设置的默认背景，超出了范围");
//            return;
//        }
//        setWallPaper(Utils.drawablesId[number - 1]);
//        Drawable drawable = getResources().getDrawable(Utils.drawablesId[number - 1]);
//        MyApplication.mainDrawable = (BitmapDrawable) drawable;
//        setDefaultBg(drawable);
//    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setDefaultBackgroundById() {
        //如果用户自主修改了背景，那么重启之后不再设置默认背景start
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        int selectBg = sharedPreferences.getInt(Contants.SelectWallpaperLocal, -1);
        if (selectBg != -1) {
            LogUtils.d(TAG, " setDefaultBackground 用户已经自主修改了背景");
            return;
        }
        //背景控制end
        String defaultbg = sharedPreferences.getString(Contants.DefaultBg, "1");
//        String defaultbg = MyApplication.config.defaultbackground;
        if (defaultbg.isEmpty()) {
            defaultbg = "1";
        }
        int number = Integer.parseInt(defaultbg);
        LogUtils.d(TAG, " setDefaultBackground number " + number);
        LogUtils.d(TAG, " setDefaultBackground defaultbg " + defaultbg);
        if(Utils.customBackground) {
            String path = (String) Utils.drawables.get(number-1);
            LogUtils.d(TAG, " loadImageFromPath path " + path);
            Drawable drawable = ImageUtils.loadImageFromPath(path,getApplicationContext());
            MyApplication.mainDrawable = (BitmapDrawable) drawable;
            setDefaultBg(drawable);
        }else {
            if (number > Utils.drawablesId.length) {
                LogUtils.d(TAG, " setDefaultBackground 用户设置的默认背景，超出了范围");
                return;
            }
            if(number == 1) {
                Drawable drawable = (Drawable) Utils.drawables.get(0);
                MyApplication.mainDrawable = (BitmapDrawable) drawable;
                setDefaultBg(drawable);
            }else if(number>1) {
                setWallPaper(Utils.drawablesId[number - 1]);
                Drawable drawable = getResources().getDrawable(Utils.drawablesId[number - 1]);
                MyApplication.mainDrawable = (BitmapDrawable) drawable;
                setDefaultBg(drawable);
            }
        }
    }


}
