package com.elvis.example.wifidirect.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.elvis.example.wifidirect.activity.DetailFragment;
import com.elvis.example.wifidirect.activity.SearchFragment;
import com.elvis.example.wifidirect.activity.WiFiChatActivity;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = "WiFiBroadcastReceiver";
  private WifiP2pManager manager;
  private WifiP2pManager.Channel channel;
  private WiFiChatActivity activity;

  /**
   * @param manager  WifiP2pManager system service
   * @param channel  Wifi p2p channel
   * @param activity activity associated with the receiver
   */
  public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                     WiFiChatActivity activity) {
    super();
    this.manager = manager;
    this.channel = channel;
    this.activity = activity;
  }

  /*
   * (non-Javadoc)
   * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
   * android.content.Intent)
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
      // UI update to indicate wifi p2p status.
      int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
      SearchFragment searchFragment = (SearchFragment) activity.getSupportFragmentManager().findFragmentByTag("f2");
      if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
        // Wifi Direct mode is enabled
        assert searchFragment != null;
        searchFragment.setWifiEnabled(true);
      } else {
        assert searchFragment != null;
        searchFragment.setWifiEnabled(false);
      }
      Log.d(WiFiChatActivity.TAG, "P2P state changed - " + state);
    } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
      // request available peers from the wifi p2p manager. This is an
      // asynchronous call and the calling activity is notified with a
      // callback on PeerListListener.onPeersAvailable()
      if (manager != null) {
        WifiP2pManager.PeerListListener frag = (WifiP2pManager.PeerListListener) activity.getSupportFragmentManager().
            findFragmentByTag("f2");
        manager.requestPeers(channel, frag);
      }
      Log.d(WiFiChatActivity.TAG, "P2P peers changed");
    } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
      if (manager == null) {
        return;
      }
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
      DetailFragment detailFragment = (DetailFragment) activity.getSupportFragmentManager().findFragmentByTag("f3");
      if (networkInfo.isConnected()) {
        // we are connected with the other device, request connection
        // info to find group owner IP
        manager.requestConnectionInfo(channel, detailFragment);
      } else {
        // It's a disconnect
        activity.resetData();
      }
    } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
      DetailFragment fragment = (DetailFragment) activity.getSupportFragmentManager()
          .findFragmentByTag("f3");
      if (fragment != null) {
        fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
            WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
      }
    }
  }
}