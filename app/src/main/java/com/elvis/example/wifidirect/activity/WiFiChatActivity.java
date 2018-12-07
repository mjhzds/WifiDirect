package com.elvis.example.wifidirect.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.elvis.example.wifidirect.utils.PermissionHelper;
import com.elvis.example.wifidirect.utils.PermissionInterface;
import com.elvis.example.wifidirect.receiver.WiFiDirectBroadcastReceiver;
import com.example.android.wifidirect.R;

public class WiFiChatActivity extends AppCompatActivity implements PermissionInterface {
  public static final String TAG = "WifiChatActivity";
  private final IntentFilter intentFilter = new IntentFilter();
  private BroadcastReceiver receiver = null;
  private Fragment[] fragments = {new ChatFragment(), new SearchFragment(), new DetailFragment()};

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // add necessary intent values to be matched.
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    WifiP2pManager.Channel channel = manager.initialize(this, getMainLooper(), null);
    receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
    getSupportFragmentManager().beginTransaction()
        .add(R.id.container, fragments[0], "f1")
        .add(R.id.container, fragments[1], "f2")
        .add(R.id.container, fragments[2], "f3").commit();
    chatFragment(null);
    PermissionHelper helper = new PermissionHelper(this, this);
    helper.requestPermissions();
  }

  public void chatFragment(View view) {
    getSupportFragmentManager().beginTransaction()
        .hide(fragments[1])
        .hide(fragments[2])
        .show(fragments[0]).commit();
  }

  public void searchFragment(View view) {
    getSupportFragmentManager().beginTransaction()
        .hide(fragments[0])
        .hide(fragments[2])
        .show(fragments[1]).commit();
  }

  public void detailFragment(View view) {
    getSupportFragmentManager().beginTransaction()
        .hide(fragments[0])
        .hide(fragments[1])
        .show(fragments[2]).commit();
  }

  public int getPermissionsRequestCode() {
    //设置权限代码，只需要不冲突即可
    return 10000;
  }

  public void requestPermissionsSuccess() {
    Log.d(TAG, "requestPermissionsSuccess");
  }

  public void requestPermissionsFail() {
    Log.d(TAG, "requestPermissionsFailed");
  }

  @NonNull
  public String[] getPermissions() {
    return new String[]{
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.ACCESS_COARSE_LOCATION"};
  }

  /**
   * register the BroadcastReceiver with the intent values to be matched
   */
  @Override
  public void onResume() {
    super.onResume();
    registerReceiver(receiver, intentFilter);
  }

  @Override
  public void onPause() {
    super.onPause();
    unregisterReceiver(receiver);
  }

  public void resetData() {
    SearchFragment searchFragment = (SearchFragment)fragments[1];
    searchFragment.clearPeers();
  }
}