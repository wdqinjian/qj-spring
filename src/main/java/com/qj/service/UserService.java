package com.qj.service;

import com.spring.Autowired;
import com.spring.InitializingBean;
import com.spring.Scope;
import com.spring.Service;

/**
 * @author qinjian
 */
@Service(value = "userService")
@Scope("prototype")
public class UserService implements InitializingBean {

    @Autowired
    private CartService cartService;

    private String after;

    private String before;

    public void getUser(){
        System.out.println( "zhangsan");

        cartService.add();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(" UserService   afterPropertiesSet");
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