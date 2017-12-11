package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片加载
 * Created by Bill on 2017/12/7.
 */
public class ImageLoaderUtil {

    private ImageCache mImageCache; // 图片缓存

    ExecutorService mExecutorService = Executors.newFixedThreadPool
            (Runtime.getRuntime().availableProcessors()); // 线程池，线程数量为CPU的数量

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
       /* Glide.with(PicApplication.getInstance())
                .load(Uri.parse(url))
                .into(imageView);
        Log.e("cache", "网络下载的图片，显示完成：");*/


        // 如果使用Glide  此后的代码 都可以不用执行
        loadPic(url, imageView);
    }

    /**
     * 线程池下载
     *
     * @param url
     * @param view
     */
    private void loadPic(final String url, final ImageView view) {
        view.setTag(url);

        mExecutorService.submit(() -> {
            Bitmap bitmap = downloadImage(url);

            if (bitmap == null) {
                return;
            }

            if (view.getTag().equals(url)) {
                view.setImageBitmap(bitmap);

                // 显示完成后  放入缓存
                mImageCache.put(url, bitmap);

                Log.e("cache", "网络下载的图片，显示完成：");
            }
        });
    }

    /**
     * 下载图片
     *
     * @param url
     * @return
     */
    private Bitmap downloadImage(String url) {
        Bitmap bitmap = null;

        try {
            URL url1 = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setDoInput(true);

            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
