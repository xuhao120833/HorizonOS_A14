package com.htc.luminaos.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.MainActivity;
import com.htc.luminaos.databinding.ActivityMainHtcosBinding;
import com.htc.luminaos.databinding.OriginalfragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OriginalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OriginalFragment extends Fragment implements View.OnKeyListener , View.OnHoverListener , View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OriginalfragmentBinding binding;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OriginalfragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //onClick
        binding.rlScreenCast.setOnKeyListener(this);
        binding.rlSignalSource.setOnKeyListener(this);
        binding.rlFileExplorer.setOnKeyListener(this);
        binding.rlAppStore.setOnKeyListener(this);
        binding.netflix.setOnKeyListener(this);
        binding.youtube.setOnKeyListener(this);
        binding.disney.setOnKeyListener(this);
        binding.max.setOnKeyListener(this);
        binding.primeVideo.setOnKeyListener(this);
        binding.hulu.setOnKeyListener(this);

        //onHover
        binding.rlScreenCast.setOnHoverListener(this);
        binding.rlSignalSource.setOnHoverListener(this);
        binding.rlFileExplorer.setOnHoverListener(this);
        binding.rlAppStore.setOnHoverListener(this);
        binding.netflix.setOnHoverListener(this);
        binding.youtube.setOnHoverListener(this);
        binding.disney.setOnHoverListener(this);
        binding.max.setOnHoverListener(this);
        binding.primeVideo.setOnHoverListener(this);
        binding.hulu.setOnHoverListener(this);

        //onKey
        binding.netflix.setOnKeyListener(this);
        binding.youtube.setOnKeyListener(this);
        binding.disney.setOnKeyListener(this);
        binding.max.setOnKeyListener(this);
        binding.primeVideo.setOnKeyListener(this);
        binding.hulu.setOnKeyListener(this);
        binding.rlScreenCast.requestFocus();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        Log.d(TAG," onKey收到");

        if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN && (binding.netflix.hasFocus() || binding.youtube.hasFocus() || binding.disney.hasFocus()
                                                    || binding.max.hasFocus() || binding.primeVideo.hasFocus() || binding.hulu.hasFocus())
                                                    && event.getAction()==KeyEvent.ACTION_DOWN) {
            Log.d(TAG," 底部焦点向下");
            disableFocus();//防止焦点跳变
            ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                    .replace(R.id.fragment_container, MainActivity.newFragment)
                    .commit();

            return true;

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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_screen_cast:
                break;
            case R.id.rl_signal_source:
                break;
            case R.id.rl_file_explorer:
                break;
            case R.id.rl_app_store:
                break;
            case R.id.netflix:
                break;
            case R.id.youtube:
                break;
            case R.id.disney:
                break;
            case R.id.max:
                break;
            case R.id.prime_video:
                break;
            case R.id.hulu:
                break;
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

}