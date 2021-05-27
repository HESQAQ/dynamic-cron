package com.me4399.mkt.dynamic_cron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author Administrator
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class DynamicCronApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicCronApplication.class, args);
    }

}
