package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 内存缓存
 * Created by Bill on 2017/12/7.
 */
public class MemoryCache implements ImageCache {

    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCache() {
        // 初始化LRU缓存
        intiMemoryCache();
    }

    private void intiMemoryCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {

                int i = value.getRowBytes() * value.getHeight() / 1024;

                return i;
            }
        };
    }

    @Override
    public Bitmap get(String url) {
        return mMemoryCache.get(url);
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        mMemoryCache.put(url, bitmap);
    }
}
