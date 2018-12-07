package com.elvis.example.wifidirect.socket;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServerSocket extends Thread {

  private Socket socket;
  private Handler mHandler;
  public ChatServerSocket(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      Looper.prepare();
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      mHandler = new Handler();
      String line;
      while ((line = reader.readLine()) != null) {
        Message message = mHandler.obtainMessage();
        message.what = 0x123;
        message.obj = line;
        mHandler.sendMessage(message);
        ChatManager.getInstance().publish(this,line);
      }
      ChatManager.getInstance().remove(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //用于得到输出流，写数据
  public void out(final String out) {
    new AsyncTask<Void, Void, Void>() {

      @Override
      protected Void doInBackground(Void... voids) {
        try {
          PrintWriter writer = new PrintWriter(socket.getOutputStream());
          writer.write(out + "\n");
          writer.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    }.execute();

  }
}
