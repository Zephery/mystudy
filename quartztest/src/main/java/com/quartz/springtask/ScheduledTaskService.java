package com.quartz.springtask;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/11 12:55
 * Description:
 */
@Service("ss")
public class ScheduledTaskService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        System.out.println("每隔5秒运行一次" + sdf.format(new Date()));
    }
}