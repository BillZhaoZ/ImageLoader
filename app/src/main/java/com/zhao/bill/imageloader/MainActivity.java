package com.zhao.bill.imageloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhao.bill.imageloader.picLoadeUtils.DiskCache;
import com.zhao.bill.imageloader.picLoadeUtils.DoubleCache;
import com.zhao.bill.imageloader.picLoadeUtils.ImageLoaderUtil;
import com.zhao.bill.imageloader.picLoadeUtils.MemoryCache;
import com.zhao.bill.imageloader.util.PreferenceUtil;

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
    public static final int NULL = 4;
    private TextView mTvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.iv_image);
        mTvShow = findViewById(R.id.tv_show);

        findViewById(R.id.disk).setOnClickListener(this);
        findViewById(R.id.memory).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);

        int type = (int) PreferenceUtil.getObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", 0);

        if (type == 1) {
            mTvShow.setText("展示方式为：磁盘缓存");
            Log.e("cache", "type为：磁盘缓存");

            // 获取实例
            mUtil = new ImageLoaderUtil();
            mUtil.setImageCache(new DiskCache(PicApplication.getInstance(), url));
            mUtil.displayImage(url, mImageView);

        } else if (type == 2) {
            mTvShow.setText("展示方式为：内存缓存");
            Log.e("cache", "type为：内存缓存");

            // 获取实例
            mUtil = new ImageLoaderUtil();
            mUtil.setImageCache(new MemoryCache());
            mUtil.displayImage(url, mImageView);

        } else if (type == 3) {
            mTvShow.setText("展示方式为：双重缓存");
            Log.e("cache", "type为：双重缓存");

            // 获取实例
            mUtil = new ImageLoaderUtil();
            mUtil.setImageCache(new DoubleCache());
            mUtil.displayImage(url, mImageView);
        } else {
            mTvShow.setText("展示方式为：未设置");
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

        // 获取实例
        mUtil = new ImageLoaderUtil();

        switch (view.getId()) {

            case R.id.disk:  // 磁盘缓存
                Log.e("cache", "点击了磁盘缓存：");
                PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", DISK);
                mTvShow.setText("展示方式为：磁盘缓存");

                mUtil.setImageCache(new DiskCache(PicApplication.getInstance(), url));
                mUtil.displayImage(url, mImageView);
                break;

            case R.id.memory: // 内存缓存
                Log.e("cache", "点击了内存缓存：");
                PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", MEMORY);
                mTvShow.setText("展示方式为：内存缓存");

                mUtil.setImageCache(new MemoryCache());
                mUtil.displayImage(url, mImageView);
                break;

            case R.id.two: // 双缓存
                Log.e("cache", "点击了双重缓存：");
                PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", BOTH);
                mTvShow.setText("展示方式为：双重缓存");

                mUtil.setImageCache(new DoubleCache());
                mUtil.displayImage(url, mImageView);
                break;

            case R.id.reset: // 重置缓存
                Log.e("cache", "点击了重置缓存：");
                PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", NULL);
                mTvShow.setText("展示方式为：暂未设置");

                mUtil.setImageCache(null);
                mUtil.removeCache(url);
                break;
        }
    }
}
