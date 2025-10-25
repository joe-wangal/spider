package com.test.oschina.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("news_item")
public class NewsItem {
    @TableId(type = IdType.INPUT) // 主键由外部传入（即JSON中的id），而非数据库自增
    long id;
    String title;
    String url;
    @TableField("`desc`")
    String desc;
    String username;
    String date;
    long time;
    String reply;
    String image;
    // 自动填充字段：插入数据时，由MyBatis-Plus自动填充当前时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public NewsItem() { }
    @Override
    public String toString() {
        return "Artical{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", desc='" + desc + '\'' +
                ", username='" + username + '\'' +
                ", date='" + date + '\'' +
                ", time=" + time +
                ", reply='" + reply + '\'' +
                '}';
    }
    public String toCSVString() {
        // 直接用逗号分隔所有字段，不添加引号
        return id + "," +
                title + "," +
                url + "," +
                desc + "," +
                username + "," +
                date + "," +
                time + "," +
                reply;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDesc() {
        return desc;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public String getReply() {
        return reply;
    }
}
