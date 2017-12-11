package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhao.bill.imageloader.PicApplication;

/**
 * 图片加载
 * Created by Bill on 2017/12/7.
 */
public class ImageLoaderUtil {

    private ImageCache mImageCache; // 图片缓存

    // 注入缓存类型
    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    // 展示图片
    public void displayImage(String url, ImageView imageView) {
        Bitmap bitmap = mImageCache.get(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);

            Log.e("cache", "缓存中获取的图片,显示完成：" + bitmap);
            return;
        }

        // 图片没缓存   下载图片
        Glide.with(PicApplication.getInstance())
                .load(Uri.parse(url))
                .into(imageView);
        Log.e("cache", "网络下载的图片，显示完成：");
    }
}
