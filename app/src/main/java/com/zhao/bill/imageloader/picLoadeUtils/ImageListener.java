package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 回调接口
 */
public interface ImageListener {
    void onComplete(ImageView imageView, Bitmap bitmap, String url);
}