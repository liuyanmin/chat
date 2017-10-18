package com.kingsoft.tools;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by LIUYANMIN on 2017/10/18.
 */
public class WebApplicationContext implements ApplicationContextAware {

    protected static ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext app) throws BeansException {
        WebApplicationContext.appContext = app;
    }

    public static ApplicationContext getAppContext() {
        return (appContext);
    }

    public static Object getBean(String beanName) {
        return appContext.getBean(beanName);
    }

}
