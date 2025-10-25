package com.test.oschina.utils;

import okhttp3.*;
import java.io.IOException;

public class OkHttpUtils {
    private static final OkHttpClient client = new OkHttpClient();

    // 同步GET请求，直接返回结果（会阻塞当前线程）
    public static String getRequestSync(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 同步执行请求（阻塞当前线程）
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful() && body != null) {
                return body.string(); // 直接返回结果给外层
            } else {
                throw new IOException("请求失败，响应码：" + response.code());
            }
        }
    }
}