package com.zhao.bill.imageloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.zhao.bill.imageloader.picLoadeUtils.DiskCache;
import com.zhao.bill.imageloader.picLoadeUtils.ImageLoaderUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.iv_image);


        ImageLoaderUtil util = new ImageLoaderUtil();

        // 磁盘缓存
        util.setImageCache(new DiskCache());

       /* // 内存缓存
        util.setImageCache(new MemoryCache());

        // 双缓存
        util.setImageCache(new DoubleCache());

        // 自定义
        util.setImageCache(new ImageCache() {
            @Override
            public Bitmap get(String url) {
                return null;
            }

            @Override
            public void put(String url, Bitmap bitmap) {

            }
        });*/

        String url = "http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg";

        util.displayImage(url, imageView);
    }
}
