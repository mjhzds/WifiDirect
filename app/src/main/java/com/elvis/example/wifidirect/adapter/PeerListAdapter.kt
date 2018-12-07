package com.elvis.example.wifidirect.adapter

import android.net.wifi.p2p.WifiP2pDevice
import android.support.v7.widget.RecyclerView
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.example.android.wifidirect.R


class PeerListAdapter(private val peerList: MutableList<WifiP2pDevice>) : RecyclerView.Adapter<PeerListAdapter.ViewHolder>(), View.OnClickListener {

  private val map = HashMap<Int, Boolean>()
  private lateinit var onItemClickListener: RecyclerViewOnItemClickListener

  init {
    for (i in 0 until peerList.size) {
      map[i] = false
    }
  }

  inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view){
    val deviceName = view.findViewById<TextView>(R.id.device_name)!!
    val deviceStatus = view.findViewById<TextView>(R.id.device_details)!!
    val checkBox = view.findViewById<CheckBox>(R.id.select_box)!!
  }
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false)
    return ViewHolder(view)
  }

  override fun getItemCount(): Int {
    return peerList.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val device = peerList[position]
    holder.view.tag = position
    holder.deviceName.text = device.deviceName
    holder.deviceStatus.text = getDeviceStatus(device.status)
    holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
      map[position] = isChecked
    }
    if (map[position] == null) {
      map[position] = false
    }
    holder.checkBox.isChecked = map[position]!!
  }

  private fun getDeviceStatus(deviceStatus: Int): String {
    d("ChatActivity", "Peer status :$deviceStatus")
    return when (deviceStatus) {
      WifiP2pDevice.AVAILABLE -> "Available"
      WifiP2pDevice.INVITED -> "Invited"
      WifiP2pDevice.CONNECTED -> "Connected"
      WifiP2pDevice.FAILED -> "Failed"
      WifiP2pDevice.UNAVAILABLE -> "Unavailable"
      else -> "Unknown"
    }
  }

  override fun onClick(v: View?) {
    //注意这里使用getTag方法获取数据
    onItemClickListener.onItemClickListener(v!!, v.tag as Int)
  }

  //设置点击事件
  fun setRecyclerViewOnItemClickListener(onItemClickListener: RecyclerViewOnItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }


  //点击item选中CheckBox
  fun setSelectItem(position: Int) {
    //对当前状态取反
    map[position] = !map[position]!!
    notifyItemChanged(position)
  }

  //返回集合给MainActivity
  fun getMap(): HashMap<Int, Boolean> {
    return map
  }

  interface RecyclerViewOnItemClickListener {
    fun onItemClickListener(view: View, position: Int)
  }
}