package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;

/**
 * 存取接口
 * 缓存抽象类
 * 接口隔离原则  缓存的具体实现对ImageLoader隐藏 庞大的接口拆分到具体的接口实现当中
 * <p>
 * Created by Bill on 2017/12/7.
 */
public interface ImageCache {

    Bitmap get(String url);

    void put(String url, Bitmap bitmap);

    void remove(String url);
}
