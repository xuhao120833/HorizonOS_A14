package com.htc.horizonos.activity.settings;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.horizonos.R;
import com.htc.horizonos.activity.BaseActivity;
import com.htc.horizonos.activity.MainActivity;
import com.htc.horizonos.databinding.MainSettingsCustomBinding;
import com.htc.horizonos.receiver.DisplaySettingsReceiver;
import com.htc.horizonos.utils.LogUtils;

public class MainSettingActivity extends BaseActivity {

    MainSettingsCustomBinding mainSettingsCustomBinding;
    private static String TAG = "MainSettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainSettingsCustomBinding = MainSettingsCustomBinding.inflate(LayoutInflater.from(this));
        setContentView(mainSettingsCustomBinding.getRoot());
        initView();
        initData();
        handleDisplayBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mainSettingsCustomBinding.rlAbout.setOnClickListener(this);
        mainSettingsCustomBinding.rlAppsManager.setOnClickListener(this);
        mainSettingsCustomBinding.rlBluetooth.setOnClickListener(this);
        mainSettingsCustomBinding.rlDateTime.setOnClickListener(this);
        mainSettingsCustomBinding.rlLanguage.setOnClickListener(this);
        mainSettingsCustomBinding.rlOther.setOnClickListener(this);
        mainSettingsCustomBinding.rlProject.setOnClickListener(this);
        mainSettingsCustomBinding.rlWifi.setOnClickListener(this);

        mainSettingsCustomBinding.rlAbout.setOnHoverListener(this);
        mainSettingsCustomBinding.rlAppsManager.setOnHoverListener(this);
        mainSettingsCustomBinding.rlBluetooth.setOnHoverListener(this);
        mainSettingsCustomBinding.rlDateTime.setOnHoverListener(this);
        mainSettingsCustomBinding.rlLanguage.setOnHoverListener(this);
        mainSettingsCustomBinding.rlOther.setOnHoverListener(this);
        mainSettingsCustomBinding.rlProject.setOnHoverListener(this);
        mainSettingsCustomBinding.rlWifi.setOnHoverListener(this);

        mainSettingsCustomBinding.rlAbout.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlAppsManager.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlBluetooth.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlDateTime.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlLanguage.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlOther.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlProject.setOnFocusChangeListener(this);
        mainSettingsCustomBinding.rlWifi.setOnFocusChangeListener(this);

        mainSettingsCustomBinding.rlWifi.requestFocus();
        mainSettingsCustomBinding.rlWifi.requestFocusFromTouch();
    }

    private void initData() {

    }

    private void handleDisplayBroadcast() {
        LogUtils.d(TAG," handleDisplayBroadcast ");
        Context applicationContext = getApplicationContext();

        if (MainActivity.displaySettingsReceiver != null) {
            try {
                applicationContext.unregisterReceiver(MainActivity.displaySettingsReceiver);
            } catch (Exception e) {
                // 避免重复反注册时报错
                LogUtils.d(TAG, "Receiver not registered: " + e.getMessage());
            }
            MainActivity.displaySettingsReceiver = null;
        } else {
            // 如果之前没注册过，直接返回，不需要重复注册
            LogUtils.d(TAG, "Receiver not registered yet, skipping unregister.");
        }

        MainActivity.displaySettingsReceiver = new DisplaySettingsReceiver(applicationContext);
        IntentFilter displayFilter = new IntentFilter();
        displayFilter.addAction(DisplaySettingsReceiver.DisplayAction);
        applicationContext.registerReceiver(MainActivity.displaySettingsReceiver, displayFilter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_wifi) {
            startNewActivity(NetworkActivity.class);
        } else if (id == R.id.rl_bluetooth) {
            startNewActivity(BluetoothActivity.class);
        } else if (id == R.id.rl_project) {
            startNewActivity(ProjectActivity.class);
        } else if (id == R.id.rl_apps_manager) {
            startNewActivity(AppsManagerActivity.class);
        } else if (id == R.id.rl_language) {
            startNewActivity(LanguageAndKeyboardActivity.class);
        } else if (id == R.id.rl_date_time) {
            startNewActivity(DateTimeActivity.class);
        } else if (id == R.id.rl_other) {
            startNewActivity(OtherSettingsActivity.class);
        } else if (id == R.id.rl_about) {
            startNewActivity(AboutActivity.class);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (hasFocus) {
            if (id == R.id.rl_about) {
                mainSettingsCustomBinding.aboutTxt.setSelected(true);
            } else if (id == R.id.rl_apps_manager) {
                mainSettingsCustomBinding.appsTxt.setSelected(true);
            } else if (id == R.id.rl_bluetooth) {
                mainSettingsCustomBinding.btTxt.setSelected(true);
            } else if (id == R.id.rl_date_time) {
                mainSettingsCustomBinding.timeTxt.setSelected(true);
            } else if (id == R.id.rl_language) {
                mainSettingsCustomBinding.languageTxt.setSelected(true);
            } else if (id == R.id.rl_other) {
                mainSettingsCustomBinding.otherTxt.setSelected(true);
            } else if (id == R.id.rl_project) {
                mainSettingsCustomBinding.projectTxt.setSelected(true);
            } else if (id == R.id.rl_wifi) {
                mainSettingsCustomBinding.wifiTxt.setSelected(true);
            }
        } else {
            if (id == R.id.rl_about) {
                mainSettingsCustomBinding.aboutTxt.setSelected(false);
            } else if (id == R.id.rl_apps_manager) {
                mainSettingsCustomBinding.appsTxt.setSelected(false);
            } else if (id == R.id.rl_bluetooth) {
                mainSettingsCustomBinding.btTxt.setSelected(false);
            } else if (id == R.id.rl_date_time) {
                mainSettingsCustomBinding.timeTxt.setSelected(false);
            } else if (id == R.id.rl_language) {
                mainSettingsCustomBinding.languageTxt.setSelected(false);
            } else if (id == R.id.rl_other) {
                mainSettingsCustomBinding.otherTxt.setSelected(false);
            } else if (id == R.id.rl_project) {
                mainSettingsCustomBinding.projectTxt.setSelected(false);
            } else if (id == R.id.rl_wifi) {
                mainSettingsCustomBinding.wifiTxt.setSelected(false);
            }
        }
    }
}