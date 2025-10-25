package com.test.oschina.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.oschina.domain.NewsItem;

import java.util.List;

public interface NewsItemService extends IService<NewsItem> {
    boolean saveNewsItem(NewsItem newsItem);
    boolean saveBatchNewsItem(List<NewsItem> newsItems);
}
