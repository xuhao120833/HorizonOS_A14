package com.htc.horizonos.activity.settings;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.htc.horizonos.R;
import com.htc.horizonos.activity.BaseActivity;
import com.htc.horizonos.databinding.ActivityDisplaySettingsBinding;

public class DisplaySettingsActivity extends BaseActivity {

    ActivityDisplaySettingsBinding displaySettingsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displaySettingsBinding = ActivityDisplaySettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(displaySettingsBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){

    }

    private void initData(){

    }

}