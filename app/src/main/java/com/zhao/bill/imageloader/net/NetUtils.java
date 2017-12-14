package com.zhao.bill.imageloader.net;

import android.content.Context;

import com.zhao.bill.imageloader.util.AppLog;
import com.zhao.bill.imageloader.util.Const;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;


/**
 * Created by Bill on 2017/12/14.
 */

public class NetUtils {

    private volatile static NetUtils instance;

    /**
     * Double Check 单例模式
     *
     * @return
     */
    public static NetUtils getInstance() {
        if (instance == null) {
            synchronized (NetUtils.class) {
                if (instance == null) {
                    instance = new NetUtils();
                }
            }
        }
        return instance;
    }

    public static final int REQUEST_METHOD_GET = 0;// get请求
    public static final int REQUEST_METHOD_POST = 1; // post请求

    /**
     * 普通异步网络请求  get或者post
     *
     * @param context
     * @param url
     * @param maps
     * @param requestCode
     * @param requestMethod
     * @param clazz
     * @param listener
     * @return
     */
    public Callback.Cancelable httpRequest(final Context context, String url, Map<String, Object> maps,
                                           final int requestCode, int requestMethod, final Class<? extends MResponse> clazz,
                                           final NetResponseListener listener) {

        HttpMethod method = null; // 请求方式

        if (requestMethod == REQUEST_METHOD_GET) {
            method = HttpMethod.GET;
        } else {
            method = HttpMethod.POST;
        }

        RequestParams params = new RequestParams(url);

        if (!maps.isEmpty()) {
            for (Map.Entry<String, Object> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        Callback.Cancelable request = x.http().request(method, params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                final MResponse mResponse = GsonUtil.processJS(result, clazz);//按正常响应解析
                listener.onSuccess(mResponse, requestCode, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();

                    AppLog.i(Const.HTTP_LOG_KEY, "=======---onError---网络错误======:"
                            + "返回码：" + responseCode
                            + "；---错误信息：" + responseMsg
                            + "；---错误结果：" + errorResult);

                } else { // 其他错误

                    listener.onError(ex, isOnCallback, requestCode);
                    AppLog.i(Const.HTTP_LOG_KEY, "=======---onError---其他错误======:" + ex.toString());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                listener.onCancelled(cex);
                AppLog.i(Const.HTTP_LOG_KEY, "=======---onCancelled---======:" + cex.toString());
            }

            @Override
            public void onFinished() {
                listener.onFinish();
                AppLog.i(Const.HTTP_LOG_KEY, "=======---onFinished---======:" + clazz);
            }
        });

        return request;
    }
}
