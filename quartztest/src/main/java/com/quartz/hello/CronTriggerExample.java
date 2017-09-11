package com.quartz.hello;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/11 10:29
 * Description:
 */
public class CronTriggerExample {
    public static void main(String[] args) throws Exception {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity("job1", "group1")
                .build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(cronSchedule("0/3 * * * * ?"))
                .build();

        sched.scheduleJob(job, trigger);
        sched.start();
        Thread.sleep(20);
        sched.shutdown();
    }
}