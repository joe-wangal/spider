package com.test.oschina.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.oschina.domain.NewsItem;
import com.test.oschina.mapper.NewsItemMapper;
import com.test.oschina.utils.KafkaUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class NewsItemServiceImpl extends ServiceImpl<NewsItemMapper, NewsItem> implements NewsItemService {
    @Override
    public boolean saveNewsItem(NewsItem newsItem) {
        // 调用BaseMapper的insert方法（MyBatis-Plus自动生成SQL）
        return baseMapper.updateById(newsItem) > 0; // 插入成功返回true（影响行数>0）
    }

    @Override
    public boolean saveBatchNewsItem(List<NewsItem> newsItems) {
        // 批量插入：每次插入100条（可根据数据库性能调整批次大小）
        try{
            List<String> itemsList = entityTOCSV(newsItems);
            KafkaUtils.sendMessagesBatchSync("test-topic",itemsList);

        }catch (Exception e){
            e.printStackTrace();
        }
        return saveOrUpdateBatch(newsItems, 100);
    }

    private List<String> entityTOCSV(List<NewsItem> newsItems) {
        List<String> itemsList = new ArrayList<>();
        for (NewsItem newsItem : newsItems) {
            itemsList.add(newsItem.toCSVString());
        }
        return itemsList;
    }
}
