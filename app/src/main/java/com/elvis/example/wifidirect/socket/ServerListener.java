package com.elvis.example.wifidirect.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener extends Thread {

  private static final String TAG = "ServerListener";

  @Override
  public void run() {
    try {
      Log.d(TAG, "Server start listening");
      ServerSocket serverSocket = new ServerSocket(16000);
      while (true) {
        Socket socket = serverSocket.accept();
        Log.d(TAG, "有客户端连接到了16000端口");
        ChatServerSocket chatServerSocket = new ChatServerSocket(socket);
        chatServerSocket.start();
        Log.d(TAG, "开始监听消息");
        ChatManager.getInstance().add(chatServerSocket);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
