package com.me4399.mkt.dynamic_cron;

import com.me4399.mkt.dynamic_cron.cron.CronTaskRegistrar;
import com.me4399.mkt.dynamic_cron.lock.ZooKeeperDistributedLock;
import com.me4399.mkt.dynamic_cron.runner.SchedulingRunnable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = DynamicCronApplication.class)
class DynamicCronApplicationTests {

    @Autowired
    CronTaskRegistrar cronTaskRegistrar;

    @Autowired
    ZooKeeperDistributedLock lock;

    @Test
    public void testTask() throws InterruptedException {
        SchedulingRunnable task = new SchedulingRunnable("demoTask", "taskNoParams", null);
        cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?", true);

        // 便于观察
        Thread.sleep(30000);
    }

    @Test
    public void testHaveParamsTask() throws InterruptedException {
        SchedulingRunnable task = new SchedulingRunnable("demoTask", "taskWithParams", "haha", 23);
        cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?", true);

        // 便于观察
        Thread.sleep(300000);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testRunnableTask() throws InterruptedException {
        SchedulingRunnable task = new SchedulingRunnable("demoTask", "taskWithParams", "haha", 23);
        cronTaskRegistrar.addCronTask(task);
        // 便于观察
//        Thread.sleep(3000);
    }

    @Test
    public void testScheduleDestroy() {
        cronTaskRegistrar.destroy();
    }


//    @Test
//    public void testZookeeperLock1() throws Exception {
//        boolean isLock = lock.acquireDistributedLock("10000000030");
//        System.out.println(isLock);
//        Thread.sleep(30000);
//        boolean isLock1 = lock.acquireDistributedLock("10000000031");
//        System.out.println(isLock1);
//
//    }


    @Test
    public void testZookeeperLock2() throws Exception {
        boolean isLock = lock.acquireDistributedLock(null);
        System.out.println(isLock);
        Thread.sleep(300000);
    }


    @Test
    public void testString() {
        String s1 = "/locks/10000000060";
        System.out.println(s1.substring(7));
        System.out.println(s1.indexOf("/locks/"));
    }
}
