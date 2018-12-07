package com.elvis.example.wifidirect.activity;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elvis.example.wifidirect.Constant;
import com.example.android.wifidirect.R;

public class DetailFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {
  private static final String TAG = "DetailFragment";
  private TextView myName;
  private TextView myStatus;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail, container, false);
    myName = view.findViewById(R.id.my_name);
    myStatus = view.findViewById(R.id.my_status);
    return view;
  }

  @Override
  public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
    if (wifiP2pInfo == null) {
      Toast.makeText(getContext(), "连接不可用", Toast.LENGTH_SHORT).show();
    } else {
      String groupOwner = wifiP2pInfo.groupOwnerAddress.getHostAddress();
      Log.d(TAG, "Group Owner IP - " + groupOwner);
      if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
        Intent intent = new Intent(getActivity(), ChatActivity.class).putExtra(Constant.HOST_ADDRESS, wifiP2pInfo.groupOwnerAddress.getHostAddress());
        startActivity(intent);
      }
    }
  }


  public void updateThisDevice(WifiP2pDevice device) {
    myName.setText(device.deviceName);
    myStatus.setText(getDeviceStatus(device.status));
  }

  private String getDeviceStatus(int deviceStatus) {
    String status;
    switch (deviceStatus) {
      case WifiP2pDevice.AVAILABLE:
        status = "Available";
        break;
      case WifiP2pDevice.CONNECTED:
        status = "Connected";
        break;
      case WifiP2pDevice.INVITED:
        status = "Invited";
        break;
      case WifiP2pDevice.FAILED:
        status = "Failed";
        break;
      case WifiP2pDevice.UNAVAILABLE:
        status = "Unavailable";
        break;
      default:
          status = "Unknown";
    }
    return status;
  }
}
