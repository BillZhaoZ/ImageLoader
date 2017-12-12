package com.zhao.bill.imageloader.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * 流 关闭工具
 * 依赖倒置原则，依赖于抽象类closeable 并不是具体的流对象
 * 接口隔离原则
 * Created by Bill on 2017/12/12.
 */

public class CloseUtils {

    public static void closeQuitely(Closeable closeable) {

        if (null != closeable) {

            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
