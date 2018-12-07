package com.elvis.example.wifidirect;

import com.elvis.example.wifidirect.bean.KeyPair;
import com.elvis.example.wifidirect.bean.Msk;
import com.elvis.example.wifidirect.bean.Params;

public class ChatHelper {

  private static ChatHelper instance;

  private ChatHelper(){}

  public static synchronized ChatHelper getInstance() {
    if (instance == null) {
      instance = new ChatHelper();
    }
    return instance;
  }
  private KeyPair keyPair;
  private Params params;
  private Msk msk;
  private String currentUser;

  public KeyPair getKeyPair() {
    return keyPair;
  }

  public void setKeyPair(KeyPair keyPair) {
    this.keyPair = keyPair;
  }

  public Params getParams() {
    return params;
  }

  public void setParams(Params params) {
    this.params = params;
  }

  public Msk getMsk() {
    return msk;
  }

  public void setMsk(Msk msk) {
    this.msk = msk;
  }

  public String getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(String currentUser) {
    this.currentUser = currentUser;
  }
}
