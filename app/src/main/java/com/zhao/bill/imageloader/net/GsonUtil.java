package com.zhao.bill.imageloader.net;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * 解析json数据
 * Created by Bill on 2017/6/13
 */
public class GsonUtil {

    /**
     * 用于解析json的类
     */
    private static Gson GSON = new Gson();

    /**
     * 把json字符串转换为JavaBean
     *
     * @param json      json字符串
     * @param beanClass JavaBean的Class
     * @return
     */
    public static <T> T processJS(String json, Class<T> beanClass) {
        T bean = null;

        try {
            bean = GSON.fromJson(json, beanClass);
        } catch (Exception e) {
            Log.i("GsonUtil", "解析json数据时出现异常\njson = " + json, e);
        }

        return bean;
    }

    /**
     * 把json字符串转换为JavaBean。如果json的根节点就是一个集合，则使用此方法<p>
     * type参数的获取方式为：Type type = new TypeToken<集合泛型>(){}.getType();
     *
     * @param json json字符串
     * @return type 指定要解析成的数据类型
     */
    public static <T> T processJS(String json, Type type) {
        T bean = null;

        try {
            bean = GSON.fromJson(json, type);
        } catch (Exception e) {
            Log.i("GsonUtil", "解析json数据时出现异常\njson = " + json, e);
        }

        return bean;
    }
}