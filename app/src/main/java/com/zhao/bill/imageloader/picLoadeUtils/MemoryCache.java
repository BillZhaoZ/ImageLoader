package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;
import android.util.Log;
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
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return value.getRowBytes() * value.getHeight() / 1024;
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

        Log.e("cache", "成功写入内存缓存：" + mMemoryCache.maxSize());
    }

    @Override
    public void remove(String url) {
        if (url != null) {
            if (mMemoryCache != null) {
                Bitmap bm = mMemoryCache.remove(url);
                if (bm != null)
                    bm.recycle();
            }
        }
    }
}
