package com.zhao.bill.imageloader;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import org.xutils.x;

/**
 * Created by Bill on 2017/12/7.
 */

public class PicApplication extends Application {

    private static PicApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);

        x.Ext.init(this);
        x.Ext.setDebug(org.xutils.BuildConfig.DEBUG); // 开启debug会影响性能
    }

    /**
     * 获取Application实例
     *
     * @return
     */
    public static PicApplication getInstance() {
        return instance;
    }
}
