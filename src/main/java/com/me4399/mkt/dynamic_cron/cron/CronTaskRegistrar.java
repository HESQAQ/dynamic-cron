package com.me4399.mkt.dynamic_cron.cron;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.me4399.mkt.dynamic_cron.runner.BaseRunnable;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hesq
 * @Date: 2021/05/12/10:48:31
 * @Description:
 */
@Component
public class CronTaskRegistrar implements DisposableBean {
    private final Map<String, ScheduledTask> scheduledTasks = new ConcurrentHashMap<String, ScheduledTask>(16);

    @Autowired
    private TaskScheduler taskScheduler;

    /**
     * 新增定时任务
     * @param task
     * @param cronExpression
     */
    public void addCronTask(BaseRunnable task, String cronExpression, boolean init) throws InterruptedException {
        System.out.println("添加Task任务：" + task.toString());
        if (!init) {
            System.out.println("已经启动的节点，等待两分钟，确保上一节点已停止定时任务。");
            this.scheduledTasks.put(task.toString(), new ScheduledTask());
            Thread.sleep(2 * 60 * 1000L);
        }
        if (StringUtils.isBlank(cronExpression)) {
            addCronTask(task);
        } else {
            addCronTask(new CronTask(task, cronExpression));
        }
    }

    /**
    * @Description: 添加定时任务
    * @Param: [cronTask]
    * @return: void
    * @Author: hesq
    * @Date: 2021/5/27 10:20:28
    */
    public void addCronTask(CronTask cronTask) {
        if (cronTask != null) {
            BaseRunnable task = (BaseRunnable) cronTask.getRunnable();

            if (this.scheduledTasks.containsKey(task.toString())) {
                removeCronTask(task.toString());
            }

            this.scheduledTasks.put(task.toString(), scheduleCronTask(cronTask));
        }
    }

    /**
    * @Description: 添加线程任务
    * @Param: [task]
    * @return: void
    * @Author: hesq
    * @Date: 2021/5/27 10:20:43
    */
    public void addCronTask(BaseRunnable task) {
        if (!Objects.isNull(task)) {
            if (this.scheduledTasks.containsKey(task.toString())) {
                removeCronTask(task.toString());
            }
            this.scheduledTasks.put(task.toString(), scheduleCronTask(task));
        }
    }

    /**
    * @Description: 是否包含定时任务
    * @Param: [task]
    * @return: boolean
    * @Author: hesq
    * @Date: 2021/5/27 10:21:00
    */
    public boolean isContainCron(BaseRunnable task) {
        if (task != null) {
            System.out.println("当前包含的定时任务："+this.scheduledTasks.keySet());
            return this.scheduledTasks.containsKey(task.toString());
        } else {
            return false;
        }
    }

    /**
     * 移除定时任务
     * @param key
     */
    public void removeCronTask(String key) {
        ScheduledTask scheduledTask = this.scheduledTasks.remove(key);
        if (scheduledTask != null){
            scheduledTask.cancel();
        }
    }

    /**
    * @Description: 开始执行定时任务
    * @Param: [cronTask]
    * @return: com.me4399.mkt.dynamic_cron.cron.ScheduledTask
    * @Author: hesq
    * @Date: 2021/5/27 10:21:27
    */
    public ScheduledTask scheduleCronTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        return scheduledTask;
    }

    /**
    * @Description: 启用任务
    * @Param: [task]
    * @return: com.me4399.mkt.dynamic_cron.cron.ScheduledTask
    * @Author: hesq
    * @Date: 2021/5/27 10:21:46
    */
    public ScheduledTask scheduleCronTask(BaseRunnable task) {
        ScheduledTask scheduledTask = new ScheduledTask();
        ExecutorService executor  = newFixedThreadPool(1);
        scheduledTask.future = executor.submit(task);
        return scheduledTask;
    }


    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
        this.scheduledTasks.clear();
    }

    /**
    * @Description: 线程池构造器
    * @Param: [nThreads]
    * @return: java.util.concurrent.ExecutorService
    * @Author: hesq
    * @Date: 2021/5/27 10:22:29
    */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("Thread-pool-%d").build();
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }
}
