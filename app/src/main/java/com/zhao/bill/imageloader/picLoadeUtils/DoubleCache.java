package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;

/**
 * 双层缓存
 * Created by Bill on 2017/12/7.
 */
public class DoubleCache implements ImageCache {

    private ImageCache mMemoryCache = new MemoryCache();
    private ImageCache mDiskCache = new DiskCache();

    public DoubleCache() {
    }

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

    @Override
    public void remove(String url) {
        mDiskCache.remove(url);
        mMemoryCache.remove(url);
    }
}
