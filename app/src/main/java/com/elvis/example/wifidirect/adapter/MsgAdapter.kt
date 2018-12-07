package com.elvis.example.wifidirect.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.elvis.example.wifidirect.bean.Msg
import com.example.android.wifidirect.R

class MsgAdapter(val msgList: MutableList<Msg>): RecyclerView.Adapter<MsgAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view: View = LayoutInflater.from(parent.context).inflate(R.layout.msg_item, parent, false)
    return ViewHolder(view)
  }

  override fun getItemCount(): Int {
    return msgList.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val msg = msgList[position]
    if (msg.type == Msg.TYPE_RECEIVED) {
      //收到的消息排列到左边
      holder.leftLayout.visibility = View.VISIBLE
      holder.rightLayout.visibility = View.GONE
      holder.leftMsg.text = msg.content
    } else {
      holder.leftLayout.visibility = View.GONE
      holder.rightLayout.visibility = View.VISIBLE
      holder.rightMsg.text = msg.content
    }
  }

  inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view){
    val leftLayout: LinearLayout = view.findViewById(R.id.left_layout)
    val rightLayout: LinearLayout = view.findViewById(R.id.right_layout)
    val leftMsg: TextView = view.findViewById(R.id.left_msg)
    val rightMsg: TextView = view.findViewById(R.id.right_msg)
  }
}