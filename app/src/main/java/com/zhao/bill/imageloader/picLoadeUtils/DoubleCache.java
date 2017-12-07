package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;


/**
 * 双层缓存
 * Created by Bill on 2017/12/7.
 */
public class DoubleCache implements ImageCache {

    ImageCache mMemoryCache = new MemoryCache();
    ImageCache mDiskCache = new DiskCache();

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = mMemoryCache.get(url);

        if (bitmap == null) {
            bitmap = mDiskCache.get(url);
        }

        return bitmap;
    }

    @Override
    public void put(String url, Bitmap bitmap) {

        mMemoryCache.put(url, bitmap);
        mDiskCache.put(url, bitmap);
    }
}
