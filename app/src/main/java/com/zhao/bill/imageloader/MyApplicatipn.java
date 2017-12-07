package com.zhao.bill.imageloader;

import android.app.Application;

import org.xutils.*;

/**
 * Created by Bill on 2017/12/7.
 */

public class MyApplicatipn extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(org.xutils.BuildConfig.DEBUG); // 开启debug会影响性能
    }
}
