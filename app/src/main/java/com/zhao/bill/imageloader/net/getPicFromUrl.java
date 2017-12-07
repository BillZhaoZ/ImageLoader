package com.zhao.bill.imageloader.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 从网络获取图片
 * Created by Bill on 2017/12/7.
 */

public class getPicFromUrl {

    /**
     * HttpURLConnection方式：
     *
     * @param url
     * @return
     */
    public Bitmap getImageBitmapurl(String url) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * HttpClient
     * <p>
     * Android 4.0以后弃用
     *
     * @param url
     * @return
     *//*
    public Bitmap getImageBitmap(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse resp = httpclient.execute(httpget);
            // 判断是否正确执行
            if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
                // 将返回内容转换为bitmap
                HttpEntity entity = resp.getEntity();
                InputStream in = entity.getContent();
                Bitmap mBitmap = BitmapFactory.decodeStream(in);
                // 向handler发送消息，执行显示图片操作
                return mBitmap;
            }

        } catch (Exception e) {
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return null;
    }*/


    /**
     * XUtils方式
     *//*
    private void initView(ImageView imageView) {

        BitmapUtils bitmapUtils = new BitmapUtils(this);

        // 加载网络图片
        bitmapUtils.display(imageView,
                "http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg");

        // 加载本地图片(路径以/开头， 绝对路径)
        // bitmapUtils.display(imageView, "/sdcard/test.jpg");

        // 加载assets中的图片(路径以assets开头)
        // bitmapUtils.display(imageView, "assets/img/wallpaper.jpg");
    }*/


    /**
     * OkHttp方式
     */
    private void setIamge(final ImageView imageView) {
        String url = "http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg";

        OkHttpUtils.get().url(url).tag(this)
                .build()
                .connTimeOut(20000).readTimeOut(20000).writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }

                });
    }


   /* *//***
     * ImageRequest加载图片
     *//*
    public void setImg1() {

        ImageRequest request = new ImageRequest(VolleySingleton.imageThumbUrls[0],
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageview1.setImageBitmap(bitmap);
                    }
                }, 0, 0, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imageview1.setImageResource(R.mipmap.ic_launcher);
                    }
                });

        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(request);
    }*/


   /* *//***
     * 使用 ImageLoader 加载图片
     *//*

    public void setImg2() {
        com.android.volley.toolbox.ImageLoader mImageLoader;
        mImageLoader = VolleySingleton.getVolleySingleton(this.getApplicationContext()).getImageLoader();
        mImageLoader.get(VolleySingleton.imageThumbUrls[1],
                //mImageView是ImageView实例
                //第2个参数：默认图片
                //第2个参数：加载图片错误时的图片
                com.android.volley.toolbox.ImageLoader.getImageListener(imageview2, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
    }


    *//**
     * 使用NetworkImageView加载图片
     *//*
    public void setImg3() {
        com.android.volley.toolbox.ImageLoader mImageLoader;
        mImageLoader = VolleySingleton.getVolleySingleton(this.getApplicationContext()).getImageLoader();
        networkImageView.setImageUrl(VolleySingleton.imageThumbUrls[2], mImageLoader);
    }*/
}
