package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;

/**
 * 存取接口
 * Created by Bill on 2017/12/7.
 */
public interface ImageCache {

    Bitmap get(String url);

    void put(String url, Bitmap bitmap);

    void remove(String url);
}
