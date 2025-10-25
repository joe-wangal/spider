package com.test.oschina.domain;

import java.util.List;

public class NewsResponse {
    private int page;
    private int next;
    private String title;
//    @JsonProperty("list")
    private List<NewsItem> list;

    public NewsResponse(int page, int next, String title, List<NewsItem> list) {
        this.page = page;
        this.next = next;
        this.title = title;
        this.list = list;
    }
    public NewsResponse() {}

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NewsItem> getList() {
        return list;
    }

    public void setList(List<NewsItem> list) {
        this.list = list;
    }
}
