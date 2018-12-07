package com.elvis.example.wifidirect;

import android.app.Application;
import android.content.Context;

public class ChatApplication extends Application {
  private Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
  }

  public Context getContext() {
    return context;
  }
}
