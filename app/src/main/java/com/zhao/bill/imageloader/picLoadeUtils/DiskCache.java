package com.zhao.bill.imageloader.picLoadeUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.zhao.bill.imageloader.diskLrucache.DiskLruCache;
import com.zhao.bill.imageloader.util.MD5Encoder;
import com.zhao.bill.imageloader.util.loadUtil;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存
 * <p>
 * 一、普通的磁盘缓存
 * 二、disklrucache
 * <p>
 * Created by Bill on 2017/12/7.
 */
public class DiskCache implements ImageCache {

    private Context mContext;
    private String url;
    private String cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Image/picsCache";
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;// 50MB
    private DiskLruCache mDiskLruCache;

    public DiskCache() {
    }

    public DiskCache(Context mContext, String url) {
        this.mContext = mContext;
        this.url = url;
        init();
    }

    /**
     * 初始化disklrucache
     */
    private void init() {

        try {
            String fileName = MD5Encoder.encode(url);
            //  File diskCacheDir = new File(cacheDir, fileName);
            File diskCacheDir = loadUtil.getDiskCacheDir(mContext, fileName);

            if (!diskCacheDir.exists()) {
                diskCacheDir.mkdirs();
            }

            mDiskLruCache = DiskLruCache.open(diskCacheDir, loadUtil.getAppVersion(mContext), 1, DISK_CACHE_SIZE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Bitmap get(String url) {
        // 从本地文件获取图片

        // 第一种
        /* try {
            String fileName = MD5Encoder.encode(url);
            File file = new File(cacheDir, fileName);

            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                Log.e("cache", "获取图片：来自于磁盘缓存： " + cacheDir);
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;*/


        // 第二种
        Bitmap bitmap = null;
        String key = null;

        try {
            key = MD5Encoder.encode(url);
            DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);

            if (snapShot != null) {
                InputStream is = snapShot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(is);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            Log.e("cache", "获取图片：来自于磁盘缓存 === " + cacheDir + "===" + bitmap);
        } else {
            Log.e("cache", "获取图片为空 === 位置：" + cacheDir);
        }

        return bitmap;
    }

    @Override
    public void put(String url, Bitmap bitmap) {

        // 第二种
        try {
            String key = MD5Encoder.encode(url);
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);

            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (loadUtil.downloadUrlToStream(url, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }

            Log.e("cache", "成功写入磁盘缓存 === " + cacheDir);

            mDiskLruCache.flush();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("cache", "写入磁盘缓存失败 === " + e.toString());
        }


        // 第一种
       /* // 将bitmap写入文件中
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

            Log.e("cache", "成功写入磁盘缓存 === " + cacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void remove(String url) {
        String key = null;

        try {
            key = MD5Encoder.encode(url);
            mDiskLruCache.remove(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
