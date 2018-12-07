package com.elvis.example.wifidirect.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elvis.example.wifidirect.adapter.PeerListAdapter;
import com.elvis.example.wifidirect.socket.ServerListener;
import com.example.android.wifidirect.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFragment extends Fragment implements WifiP2pManager.PeerListListener,
    View.OnClickListener, WifiP2pManager.ChannelListener {

  private static final String TAG = "SearchFragment";
  private ArrayList<WifiP2pDevice> peers = new ArrayList<>();
  private PeerListAdapter adapter;
  private boolean isWifiEnabled = false;
  private WifiP2pManager manager;
  private WifiP2pManager.Channel channel;
  private Button btn_search;
  private Button btn_connect;
  private Button btn_cancel;
  private Button btn_reverse;
  private Button btn_group;
  private RecyclerView recyclerView;
  private ProgressBar progressBar;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search, container, false);
    btn_search = view.findViewById(R.id.btn_search);
    btn_connect = view.findViewById(R.id.btn_connect);
    btn_cancel = view.findViewById(R.id.btn_concel);
    btn_reverse = view.findViewById(R.id.btn_reverse);
    btn_group = view.findViewById(R.id.btn_createGroup);
    recyclerView = view.findViewById(R.id.device_list);
    progressBar = view.findViewById(R.id.progressBar);

    manager = (WifiP2pManager) getContext().getSystemService(Context.WIFI_P2P_SERVICE);
    channel = manager.initialize(getContext(), Looper.getMainLooper(), null);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    btn_search.setOnClickListener(this);
    btn_connect.setOnClickListener(this);
    btn_cancel.setOnClickListener(this);
    btn_reverse.setOnClickListener(this);
    btn_group.setOnClickListener(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter = new PeerListAdapter(peers);
    adapter.setRecyclerViewOnItemClickListener(new PeerListAdapter.RecyclerViewOnItemClickListener() {
      @Override
      public void onItemClickListener(@NotNull View view, int position) {
        adapter.setSelectItem(position);
      }
    });
    recyclerView.setAdapter(adapter);
  }

  public boolean getWifiEnabled() {
    return isWifiEnabled;
  }

  public void setWifiEnabled(boolean wifiEnabled) {
    isWifiEnabled = wifiEnabled;
  }

  @Override
  public void onChannelDisconnected() {
    Toast.makeText(getContext(), "WIFI失去连接...", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onPeersAvailable(WifiP2pDeviceList peerList) {
    if (progressBar != null && progressBar.isShown()) {
      progressBar.setVisibility(View.GONE);
    }
    peers.clear();
    if (peerList.getDeviceList().isEmpty()) {
      Toast.makeText(getContext(), "未发现设备", Toast.LENGTH_SHORT).show();
      return;
    }
    peers.addAll(peerList.getDeviceList());
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onClick(View view) {
    final HashMap<Integer,Boolean> map = adapter.getMap();
    switch (view.getId()) {
      case R.id.btn_search:
        if (!isWifiEnabled) {
          Toast.makeText(getContext(), "请打开WIFI", Toast.LENGTH_SHORT).show();
          startActivity(new Intent(Settings.ACTION_SETTINGS));
        } else {
          progressBar.setVisibility(View.VISIBLE);
          manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
              Toast.makeText(getContext(), "寻找设备中...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
              Toast.makeText(getContext(), "寻找设备失败: " + reason, Toast.LENGTH_SHORT).show();
            }
          });
        }
        break;
      case R.id.btn_concel:
        for (int i = 0; i < map.size(); i++) {
          map.put(i, false);
          adapter.notifyDataSetChanged();
        }
        break;
      case R.id.btn_reverse:
        for (int i = 0; i < map.size(); i++) {
          map.put(i, !map.get(i));
          adapter.notifyDataSetChanged();
        }
        break;
      case R.id.btn_createGroup:
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
          @Override
          public void onSuccess() {
            Log.d(TAG, "created a group");
            new ServerListener().start();
          }

          @Override
          public void onFailure(int reason) {
            Log.d(TAG, "failed to created a group: " + reason);
          }
        });
        break;
      case R.id.btn_connect:
        for (int i = 0; i < map.size(); i++) {
          if (map.get(i)) {
            WifiP2pDevice device = peers.get(i);
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
              @Override
              public void onSuccess() {
                Toast.makeText(getContext(), "邀请成功", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < map.size(); i++) {
                  map.put(i, false);
                }
              }

              @Override
              public void onFailure(int reason) {
                Toast.makeText(getContext(), "邀请失败: " + reason, Toast.LENGTH_SHORT).show();
              }
            });
          }
        }
        startActivity(new Intent(getActivity(), ChatActivity.class));
        break;

    }
  }

  public void clearPeers() {
    peers.clear();
    adapter.notifyDataSetChanged();
  }
}
