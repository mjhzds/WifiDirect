package com.elvis.example.wifidirect.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.elvis.example.wifidirect.adapter.MsgAdapter;
import com.elvis.example.wifidirect.Constant;
import com.elvis.example.wifidirect.bean.Msg;
import com.example.android.wifidirect.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
  private static final String TAG = "ChatActivity";

  private String host;
  private int port = 16000;
  private BufferedReader reader;
  private PrintWriter writer;
  private ArrayList<Msg> msgList = new ArrayList<>();
  private MsgAdapter adapter;
  private RecyclerView recyclerView;
  private Button btn_send;
  private EditText input;
  private Socket socket;
  private Handler mHandler;

  class MessageHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      if (msg.what == 0x123) {
        String line = msg.obj.toString();
        Log.d(TAG, "客户端收到消息: " + line);
        Msg message = new Gson().fromJson(line, Msg.class);
        message.setType(Msg.TYPE_RECEIVED);
        msgList.add(message);
        adapter.notifyDataSetChanged();
      }
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    host = getIntent().getStringExtra(Constant.HOST_ADDRESS);
    initView();
    mHandler = new MessageHandler();
    btn_send.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });
    connect();
  }

  private void connect() {
    ChatClientSocket clientSocket = new ChatClientSocket();
    clientSocket.start();
  }

  private void sendMessage() {
    Log.d(TAG, "sendMessage");
    String message = input.getText().toString();
    if (!"".equals(message)) {
      final Msg msg = new Msg(message, Msg.TYPE_SEND);
      msgList.add(msg);
      adapter.notifyItemInserted(msgList.size() - 1);
      recyclerView.scrollToPosition(msgList.size() - 1);
      input.setText("");
      new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... voids) {
          writer.write(new Gson().toJson(msg) + "\n");
          writer.flush();
          return null;
        }
      }.execute();
    }
  }

  private void initView() {
    input = findViewById(R.id.input_text);
    btn_send = findViewById(R.id.btn_send);
    recyclerView = findViewById(R.id.msg_recyclerview);
    adapter = new MsgAdapter(msgList);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
  }

class ChatClientSocket extends Thread {
  @Override
  public void run() {
    try {
      socket = new Socket(host, port);
      Log.d(TAG, "socket创建成功!");
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"), 1024 * 1024);
      writer = new PrintWriter(socket.getOutputStream());
      String line;
      while ((line = reader.readLine()) != null) {
        Message msg = mHandler.obtainMessage();
        msg.what = 0x123;
        msg.obj = line;
        mHandler.sendMessage(msg);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
