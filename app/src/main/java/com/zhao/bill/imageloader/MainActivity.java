package com.zhao.bill.imageloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.zhao.bill.imageloader.picLoadeUtils.DiskCache;
import com.zhao.bill.imageloader.picLoadeUtils.DoubleCache;
import com.zhao.bill.imageloader.picLoadeUtils.ImageLoaderUtil;
import com.zhao.bill.imageloader.picLoadeUtils.MemoryCache;

/**
 * 主页设置
 */
public class MainActivity extends AppCompatActivity implements ImageView.OnClickListener {

    private ImageLoaderUtil mUtil;
    private ImageView mImageView;
    private String url = "http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg";

    public static final int DISK = 1;
    public static final int MEMORY = 2;
    public static final int BOTH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.iv_image);

        findViewById(R.id.disk).setOnClickListener(this);
        findViewById(R.id.memory).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);

        // 获取实例
        mUtil = new ImageLoaderUtil();

        int type = (int) PreferenceUtil.getObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", 0);

        if (type != 0) {
            mUtil.displayImage(url, mImageView);
        }

       /* // 自定义
        util.setImageCache(new ImageCache() {
            @Override
            public Bitmap get(String url) {
                return null;
            }

            @Override
            public void put(String url, Bitmap bitmap) {

            }
        });*/
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.disk:  // 磁盘缓存
                Log.e("cache", "点击了磁盘缓存：");

                PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", DISK);

                mUtil.setImageCache(new DiskCache());
                mUtil.displayImage(url, mImageView);
                break;

            case R.id.memory: // 内存缓存
                Log.e("cache", "点击了内存缓存：");
                PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", MEMORY);


                mUtil.setImageCache(new MemoryCache());
                mUtil.displayImage(url, mImageView);
                break;

            case R.id.two: // 双缓存
                Log.e("cache", "点击了双重缓存：");
                PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", BOTH);

                mUtil.setImageCache(new DoubleCache());
                mUtil.displayImage(url, mImageView);
                break;
        }
    }
}
