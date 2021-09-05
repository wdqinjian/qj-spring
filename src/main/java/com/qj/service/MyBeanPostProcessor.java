package com.qj.service;

import com.qj.service.UserService;
import com.spring.BeanPostProcessor;
import com.spring.Service;
import org.springframework.beans.BeansException;

import java.util.concurrent.BrokenBarrierException;

/**
 * @author qinjian
 */
@Service
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // System.out.println(" MyBeanPostProcessor  postProcessBeforeInitialization");
        if (beanName.equals("userService")){
            UserService userService = (UserService) bean;
            userService.setBefore("before");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // System.out.println(" MyBeanPostProcessor  postProcessAfterInitialization");

        if (beanName.equals("userService")){
            UserService userService = (UserService) bean;
            userService.setAfter("after");
        }
        return bean;
    }
}