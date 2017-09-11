package com.quartz.hello;

import org.joda.time.DateTime;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/11 10:01
 * Description:
 */
public class SimpleExample {
    public static void main(String[] args) throws Exception {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity("job1", "group1")
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startAt(DateTime.now().plusMillis(4).toDate())//固定一个时间，现在设置为程序启动后4秒
                .build();
        sched.start();
        sched.scheduleJob(job, trigger);
        //启动后要sleep一下，不然立即关闭
        Thread.sleep(6);
        sched.shutdown(true);
    }
}