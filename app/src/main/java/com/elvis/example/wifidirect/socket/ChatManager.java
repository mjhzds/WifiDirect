package com.elvis.example.wifidirect.socket;

import android.util.Log;

import java.util.Vector;

public class ChatManager {
  //单例化处理
  private ChatManager() {}
  private static final ChatManager chatManager = new ChatManager();
  public static ChatManager getInstance() {
    return chatManager;
  }
  //定义一个集合，用来存放不同的客户端
  Vector<ChatServerSocket> vector = new Vector<>();
  //实现往Vector中添加个体
  public void add(ChatServerSocket chatServerSocket) {
    vector.add(chatServerSocket);
  }
  //实现删除vector中断开连接的线程
  public void remove(ChatServerSocket chatServerSocket) {
    vector.remove(chatServerSocket);
  }
  //把获取的消息发布给除自己以外的其他客户端
  public void publish(ChatServerSocket chatServerSocket,String out) {
    for (int i = 0; i < vector.size(); i++) {
      ChatServerSocket csChatSocket = vector.get(i);
      //把vector中的每一个个体与传进来的线程进行比较，如果不是自己则发送
      if (!chatServerSocket.equals(csChatSocket)) {
        Log.d("Server", "publish to " + csChatSocket.getName() + out);
        csChatSocket.out(out);
      }
    }
  }
}
