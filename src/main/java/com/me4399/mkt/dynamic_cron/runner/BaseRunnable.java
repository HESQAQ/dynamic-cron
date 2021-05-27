package com.me4399.mkt.dynamic_cron.runner;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hesq
 * @Date: 2021/05/27/11:48:46
 * @Description:
 */
public abstract class BaseRunnable implements Runnable{
    protected String beanName;
    protected String methodName;
    protected Object[] params;

    @Override
    public String toString() {
        return beanName + "_" + methodName;
    }
}
