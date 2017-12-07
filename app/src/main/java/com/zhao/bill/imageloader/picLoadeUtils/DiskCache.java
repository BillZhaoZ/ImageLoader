package com.zhao.bill.imageloader.picLoadeUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 磁盘缓存
 * Created by Bill on 2017/12/7.
 */

public class DiskCache implements ImageCache {

    String cacheDir = Environment.getExternalStorageDirectory().toString() + File.separator + "MyCacheImageview";// 拼接需要下载的文件路径


    @Override
    public Bitmap get(String url) {
        return BitmapFactory.decodeFile(cacheDir + url); // 从本地文件获取图片
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        // 将bitmap写入文件中
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(cacheDir + url);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
