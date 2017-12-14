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

                // 写入图片到本地文件
                if (loadUtil.downloadUrlToStream(url, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }

            Log.e("cache", "成功写入磁盘缓存 === " + cacheDir);

            // 这个方法用于将内存中的操作记录同步到日志文件（也就是journal文件）当中。这个方法非常重要，
            // 因为DiskLruCache能够正常工作的前提就是要依赖于journal文件中的内容。
            // 前面在讲解写入缓存操作的时候我有调用过一次这个方法，但其实并不是每次写入缓存都要调用一次flush()方法的，
            // 频繁地调用并不会带来任何好处，只会额外增加同步journal文件的时间。
            // 比较标准的做法就是在Activity的onPause()方法中去调用一次flush()方法就可以了。

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

    /**
     * DiskLruCache会根据我们在调用open()方法时设定的缓存最大值来自动删除多余的缓存。
     * 只有你确定某个key对应的缓存内容已经过期，需要从网络获取最新数据的时候才应该调用remove()方法来移除缓存。
     *
     * @param url
     */
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
