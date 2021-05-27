package com.me4399.mkt.dynamic_cron.controller;

import com.google.common.collect.Maps;
import com.me4399.mkt.dynamic_cron.cron.CronTaskRegistrar;
import com.me4399.mkt.dynamic_cron.lock.ZooKeeperDistributedLock;
import com.me4399.mkt.dynamic_cron.runner.BaseRunnable;
import com.me4399.mkt.dynamic_cron.runner.DefaultRunnable;
import com.me4399.mkt.dynamic_cron.runner.SchedulingRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hesq
 * @Date: 2021/05/13/16:29:34
 * @Description:
 */
@Component
public class DistributeLockCron {

    private Logger logger = LoggerFactory.getLogger(DistributeLockCron.class);

    @Autowired
    ZooKeeperDistributedLock lock;

    @Autowired
    CronTaskRegistrar cronTaskRegistrar;

    private final static Map<String, String> lockMap = Maps.newConcurrentMap();

    private static boolean check = false;

    private static boolean init = true;

    @Async
    @Scheduled(cron="0/30 * * * * ?")
    public void dealFeishuEventNext() {
        try {
            logger.info("LockMap:" + lockMap);
            boolean isLock = lock.acquireDistributedLock(lockMap);
            logger.info("Lock:" + isLock + ",Check:" + check + ", Init:" + init);
            if (isLock && !check) {
                BaseRunnable task = new SchedulingRunnable("demoTask", "taskOneParams", "test2");
                BaseRunnable thread = new DefaultRunnable("demoTask", "taskWithParams", "hesq", 25);
                logger.info("DefaultRunnable: "  + thread.toString());
                if (!cronTaskRegistrar.isContainCron(thread)) {
                    logger.info("添加新的单线程任务......");
                    cronTaskRegistrar.addCronTask(thread, null, init);
                }
                logger.info("SchedulingRunnable: "  + task.toString());
                if (!cronTaskRegistrar.isContainCron(task)) {
                    logger.info("添加新的定时任务......");
                    cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?", init);
                }
                check = true;
            } else if (!isLock && check) {
                check = false;
                logger.info("销毁定时任务......");
                cronTaskRegistrar.destroy();
            } else if (!isLock) {
                logger.info("Waiting!!!");
            } else {
                logger.info("Success!!!");
            }
            logger.info("check: " + check + ", isLock: " + isLock);
            init = false;
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            try {
                logger.info("销毁所有的定时任务......");
                cronTaskRegistrar.destroy();
                logger.info("释放zookeeper锁......");
                lockMap.clear();
//                lock.unlock(lockMap.get("node"));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
