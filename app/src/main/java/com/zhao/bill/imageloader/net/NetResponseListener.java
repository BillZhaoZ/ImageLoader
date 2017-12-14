package com.zhao.bill.imageloader.net;

import org.xutils.common.Callback;

/**
 * 网络请求的接口回调监听器
 */
public interface NetResponseListener {

    void onSuccess(MResponse response, int requestCode, String result);

    void onError(Throwable ex, boolean isOnCallback, int requestCode);

    void onCancelled(Callback.CancelledException cex);

    void onFinish();
}