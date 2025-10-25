package com.test.oschina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.oschina.domain.NewsItem;
import com.test.oschina.domain.NewsResponse;
import com.test.oschina.service.NewsItemService;
import junit.framework.Assert;
//import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;



import java.util.List;

@SpringBootTest
public class NewsInsertTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsItemService newsItemService;

//    @Test
    public void testInsertNews() throws Exception {
        String json = "";
        NewsResponse newsResponse = objectMapper.readValue(json, NewsResponse.class);
        List<NewsItem> list = newsResponse.getList();
        boolean result = newsItemService.saveBatchNewsItem(list);
        // Assert.assertTrue(result);
    }
}
