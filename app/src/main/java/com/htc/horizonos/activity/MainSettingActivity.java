package com.htc.horizonos.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.horizonos.R;
import com.htc.horizonos.databinding.ActivityMainSettingBinding;
import com.htc.horizonos.databinding.ActivitySettingsCustomBinding;
import com.htc.horizonos.databinding.MainSettingsCustomBinding;
import com.htc.horizonos.databinding.SettingsCustomBinding;

public class MainSettingActivity extends BaseActivity {

    ActivityMainSettingBinding mainSettingBinding;

    SettingsCustomBinding settingsCustomBinding;

    MainSettingsCustomBinding mainSettingsCustomBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainSettingsCustomBinding = MainSettingsCustomBinding.inflate(LayoutInflater.from(this));
        setContentView(mainSettingsCustomBinding.getRoot());
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(settingsCustomBinding != null) {
            mainSettingsCustomBinding.wifiTxt.setSelected(true);
            mainSettingsCustomBinding.btTxt.setSelected(true);
            mainSettingsCustomBinding.projectTxt.setSelected(true);
            mainSettingsCustomBinding.languageTxt.setSelected(true);
            mainSettingsCustomBinding.appsTxt.setSelected(true);
            mainSettingsCustomBinding.timeTxt.setSelected(true);
            mainSettingsCustomBinding.otherTxt.setSelected(true);
            mainSettingsCustomBinding.aboutTxt.setSelected(true);
        }
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

        mainSettingsCustomBinding.rlWifi.requestFocus();
        mainSettingsCustomBinding.rlWifi.requestFocusFromTouch();

        mainSettingsCustomBinding.wifiTxt.setSelected(true);
        mainSettingsCustomBinding.btTxt.setSelected(true);
        mainSettingsCustomBinding.projectTxt.setSelected(true);
        mainSettingsCustomBinding.languageTxt.setSelected(true);
        mainSettingsCustomBinding.appsTxt.setSelected(true);
        mainSettingsCustomBinding.timeTxt.setSelected(true);
        mainSettingsCustomBinding.otherTxt.setSelected(true);
        mainSettingsCustomBinding.aboutTxt.setSelected(true);
    }

    private void initData() {

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
}