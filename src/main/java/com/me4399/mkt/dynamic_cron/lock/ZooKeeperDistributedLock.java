package com.me4399.mkt.dynamic_cron.lock;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hesq
 * @Date: 2021/05/13/11:58:46
 * @Description:
 */
@Service
public class ZooKeeperDistributedLock implements Watcher {

    private final String locksRoot = "/locks";
    private String iP = "1";

    private String waitNode;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private CountDownLatch latch;

    @Resource(name = "curatorFramework")
    private CuratorFramework curatorFramework;


    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) {
            countDownLatch.countDown();
            return;
        }
        if (this.latch != null) {
            this.latch.countDown();
        }
    }

    public boolean acquireDistributedLock(Map<String, String> lockMap) throws Exception {
        if (this.tryLock(lockMap)) {
            return true;
        } else {
            waitForLock(waitNode);
            return false;
        }
    }

    /***
    * @Description: 获取锁
    * @Param: []
    * @return: boolean
    * @Author: hesq
    * @Date: 2021/5/13 14:17:49
    */
    public boolean tryLock(Map<String, String> lockMap) {
        try {
            /*
            传入进去的locksRoot + “/” + productId
             假设productId代表了一个商品id，比如说1
             locksRoot = locks
             /locks/10000000000，/locks/10000000001，/locks/10000000002
             */
            // 看看刚创建的节点是不是最小的节点
            // locks：10000000000，10000000001，10000000002
            String lockPath = "";
            if (StringUtils.isBlank(lockMap.getOrDefault("node", ""))) {
                lockPath = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(locksRoot + "/" + iP);
                lockMap.put("node", lockPath.substring(7));
            } else {
                Stat stat = curatorFramework.checkExists().forPath(locksRoot + "/" + lockMap.get("node"));
                if (Objects.isNull(stat)) {
                    lockPath = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(locksRoot + "/" + iP);
                    lockMap.put("node", lockPath.substring(7));
                } else {
                    lockPath = locksRoot + "/" + lockMap.get("node");
                }
            }
            List<String> locks = curatorFramework.getChildren().forPath(locksRoot);
            Collections.sort(locks);
            System.out.println(locks);
            if(lockPath.equals(locksRoot + "/" + locks.get(0))){
                //如果是最小的节点,则表示取得锁
                String lockNode = locks.get(0);
                lockMap.put("node", lockNode);
                System.out.println("lockNode:" + lockNode);
                System.out.println("lockPath:" + lockPath);
                return true;
            }

            //如果不是最小的节点，找到比自己小1的节点 (自己前面的一个节点)
            int previousLockIndex = -1;
            for(int i = 0; i < locks.size(); i++) {
                if(lockMap.get("node").equals(locks.get(i))) {
                    previousLockIndex = i - 1;
                    break;
                }
            }
            this.waitNode = locks.get(previousLockIndex);
            System.out.println("waitNode:" + this.waitNode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void waitForLock(String waitNode) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(locksRoot + "/" + waitNode);
        if (stat != null) {
            this.latch = new CountDownLatch(1);
            this.latch.await(3000, TimeUnit.MILLISECONDS);
            this.latch = null;
        }
    }

    //释放锁
    public void unlock(String lockNode) throws Exception{
        try {
            System.out.println("unlock " + lockNode);
            curatorFramework.delete().forPath(locksRoot + "/" + lockNode);
            curatorFramework.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
