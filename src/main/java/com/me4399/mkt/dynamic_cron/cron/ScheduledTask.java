package com.me4399.mkt.dynamic_cron.cron;

import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hesq
 * @Date: 2021/05/12/10:49:38
 * @Description:
 */
public final class ScheduledTask {

    public volatile Future<?> future;
    /**
     * 取消定时任务
     */
    public void cancel() {
        Future<?> future = this.future;
        if (future != null) {
            future.cancel(true);
        }
    }
}
