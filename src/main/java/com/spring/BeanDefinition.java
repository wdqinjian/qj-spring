package com.spring;

/**
 * @author qinjian
 */
public class BeanDefinition {

    // class 类型
    private Class type;
    // 范围
    private String scope;
    // 懒加载
    private boolean isLazy;


    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }
}