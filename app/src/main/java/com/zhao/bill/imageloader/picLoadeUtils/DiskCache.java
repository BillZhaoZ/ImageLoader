package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.zhao.bill.imageloader.util.MD5Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 磁盘缓存
 * Created by Bill on 2017/12/7.
 */
public class DiskCache implements ImageCache {

    private String cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache/pics";

    @Override
    public Bitmap get(String url) {
        // 从本地文件获取图片
        try {
            String fileName = MD5Encoder.encode(url);
            File file = new File(cacheDir, fileName);

            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                Log.e("cache", "获取图片：磁盘缓存" + bitmap);
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        // 将bitmap写入文件中

        try {
            // 文件的名字
            String fileName = MD5Encoder.encode(url);

            // 创建文件流，指向该路径，文件名叫做fileName
            File file = new File(cacheDir, fileName);

            // file其实是图片，它的父级File是文件夹，判断一下文件夹是否存在，如果不存在，创建文件夹
            File fileParent = file.getParentFile();

            if (!fileParent.exists()) {
                // 文件夹不存在
                fileParent.mkdirs();// 创建文件夹
            }

            // 将图片保存到本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));

            Log.e("cache", "成功写入磁盘缓存：" + cacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(String url) {

    }
}
