package com.qj.service;

import com.spring.Service;

@Service
public class CartService {

    private String after;

    private String before;

    public void add(){
        System.out.println("添加购物车成功！");
    }


    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }
}