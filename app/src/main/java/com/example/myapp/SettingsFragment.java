package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private SwitchCompat switchCompat;
    private TextView connectionText;
    private boolean switchState = true;
    int connectionType;
    private Button tryAgainBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        switchCompat = view.findViewById(R.id.menu_switch);
        switchCompat.setOnCheckedChangeListener(this);
        connectionText = view.findViewById(R.id.connectionText);
        tryAgainBtn = view.findViewById(R.id.tryAgainBtn);
        if (isOnline()){
            if(connectionType == ConnectivityManager.TYPE_MOBILE){
                connectionText.setText(R.string.mobileConnection);
                Button connectionButton = view.findViewById(R.id.connectButton);
                connectionButton.setText("Connect");
                connectionButton.setVisibility(View.VISIBLE);
                connectionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
            }

            if(connectionType == ConnectivityManager.TYPE_WIFI) {
                connectionText.setText(R.string.wifiConnection);
                tryAgainBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            connectionText.setText(R.string.notConnected);
            Button connectionButton = view.findViewById(R.id.connectButton);
            connectionButton.setText("Connect");
            connectionButton.setVisibility(View.VISIBLE);
            connectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
        }
        Fragment thisFragment = this;
        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(thisFragment);
                ft.attach(thisFragment);
                ft.commit();
            }
        });

        return view;
    }

    public SwitchCompat getSwitchCompat() {
        return switchCompat;
    }

    public boolean checkSwitch() {
        return switchState;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switchState = isChecked;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null){
            connectionType = networkInfo.getType();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }
}
