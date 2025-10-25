package com.test.oschina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.oschina.domain.NewsResponse;
import okhttp3.*;

import java.io.IOException;

public class MainPage {
    private static final OkHttpClient client = new OkHttpClient();
    static ObjectMapper objectMapper = new ObjectMapper();


    public static void main(String[] args) {
        // GET请求示例
        getRequest("https://query.asilu.com/news/oschina-news");

        // POST请求示例（带JSON参数）
        String jsonBody = "{\"name\":\"test\",\"age\":18}";
        // postRequest("https://api.example.com/submit", jsonBody);
    }

    // GET请求
    private static void getRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer your_token")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("请求失败: " + response);
                    }
                    NewsResponse newsResponse = objectMapper.readValue(responseBody.string(), NewsResponse.class);
                    System.out.println("GET响应: " + newsResponse.getList().get(0).getTitle());
                    System.out.println("GET响应: " + newsResponse.getList().get(0).getImage());
                    System.out.println("GET响应: " + newsResponse.getList().get(0).getDate());
                    System.out.println("GET响应: " + newsResponse.getList().get(0).getDesc());
                }
            }
        });
    }

    // POST请求（JSON参数）
    private static void postRequest(String url, String jsonBody) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer your_token")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    System.out.println("POST响应: " + responseBody.string());
                }
            }
        });
    }
}
