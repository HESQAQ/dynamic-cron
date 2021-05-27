package com.me4399.mkt.dynamic_cron.task;

import com.me4399.mkt.dynamic_cron.bean.A;
import com.me4399.mkt.dynamic_cron.service.AService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hesq
 * @Date: 2021/05/12/10:51:51
 * @Description:
 */
@Component("demoTask")
public class DemoTask {

    @Resource
    private AService aService;

    public void taskWithParams(String param1, Integer param2) {
        for (int i = 0; i < 10; i++) {
            System.out.println("Start Runnable!");
            System.out.println("这是有参示例任务：" + param1 + param2);
            System.out.println("Stop Runnable!");
        }

    }

    public void taskOneParams(String param) {
        A a = new A();
        a.setName(param);
        aService.insert(a);
        System.out.println(a);
    }
}
