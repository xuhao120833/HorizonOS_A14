package com.htc.luminaos.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.AppsActivity;
import com.htc.luminaos.activity.MainActivity;
import com.htc.luminaos.adapter.AppsAdapter;
import com.htc.luminaos.databinding.FragmentNewBinding;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.widget.SpacesItemDecoration;

import java.util.List;

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

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what==1){
                List<AppInfoBean> infoBeans =(List<AppInfoBean>)  msg.obj;
                AppsAdapter appsAdapter = new AppsAdapter(getContext(),infoBeans,binding.appsRv,(MainActivity) getActivity());
                binding.appsRv.setAdapter(appsAdapter);
            }
            return false;
        }
    });

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
//        enableFocus();

        return view;
    }

    private void init() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),8);
        binding.appsRv.setLayoutManager(layoutManager);
        binding.appsRv.addItemDecoration(new SpacesItemDecoration(0,
                0,getResources().getDimensionPixelSize(R.dimen.y_7),getResources().getDimensionPixelSize(R.dimen.y_7)));
        initData();
    }

    private void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(appInfoBeans == null) {
                    appInfoBeans = AppUtils.getApplicationMsg(getContext());
                }
                Message message = handler.obtainMessage();
                message.what=1;
                message.obj =appInfoBeans;
                handler.sendMessage(message);
            }
        }).start();
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

}