package com.zhao.bill.imageloader.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载图片
 * Created by Bill on 2017/12/12.
 */

public class loadUtil {

    /**
     * 下载图片转换为流对象
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    public static boolean downloadUrlToStream(String urlString, OutputStream outputStream) {

        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);

            int b;

            while ((b = in.read()) != -1) {
                out.write(b);
            }

            return true;

        } catch (final IOException e) {
            e.printStackTrace();
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 1;
    }

    /**
     * 获取缓存路径
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }
}
