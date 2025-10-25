package com.test.oschina.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.oschina.domain.NewsItem;
import com.test.oschina.domain.NewsResponse;
import com.test.oschina.service.NewsItemService;
import com.test.oschina.utils.OkHttpUtils;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class NewsController {

    private static final OkHttpClient client = new OkHttpClient();
    String URL= "https://query.asilu.com/news/oschina-news";


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsItemService newsItemService;

    @GetMapping("/aaa")
    public String insertNews() throws IOException {
        String requestSync = OkHttpUtils.getRequestSync(URL);
        NewsResponse newsResponse = objectMapper.readValue(requestSync, NewsResponse.class);
        List<NewsItem> list = newsResponse.getList();
        boolean result = newsItemService.saveBatchNewsItem(list);
        if (result) {return "success";}else {return "fail";}
    }




}
