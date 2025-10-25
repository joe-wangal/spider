package com.test.oschina.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.oschina.domain.NewsItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsItemMapper extends BaseMapper<NewsItem> {
}
