package com.alttech.admaxx;

public interface CallBack {
  void onSuccess(Ad ad);

  void onFailure(Throwable t);
}
