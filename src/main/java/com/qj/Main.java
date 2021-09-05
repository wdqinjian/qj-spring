package com.qj;

import com.qj.service.CartService;
import com.qj.service.UserService;
import com.spring.MyApplicationContext;

public class Main {

    public static void main(String[] args) throws Exception {

        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);

        UserService userService = (UserService) myApplicationContext.getBean("userService");
        System.out.println(userService);
        userService.getUser();

        UserService userService1 = (UserService) myApplicationContext.getBean("userService");
        System.out.println(userService1);
        userService1.getUser();

        CartService cartService = (CartService) myApplicationContext.getBean("cartService");
        System.out.println(cartService);
        cartService.add();

    }
}