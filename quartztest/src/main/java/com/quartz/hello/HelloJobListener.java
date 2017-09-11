package com.quartz.hello;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/11 10:35
 * Description:
 */
public class HelloJobListener implements JobListener {

    private static final String LISTENER_NAME = "dummyJobListenerName";

    @Override
    public String getName() {
        return LISTENER_NAME; //must return a name
    }

    // Run this if job is about to be executed.
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

        String jobName = context.getJobDetail().getKey().toString();
        System.out.println("jobToBeExecuted");
        System.out.println("Job : " + jobName + " is going to start...");

    }

    // No idea when will run this?
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println("jobExecutionVetoed");
    }

    //Run this after job has been executed
    @Override
    public void jobWasExecuted(JobExecutionContext context,
                               JobExecutionException jobException) {
        System.out.println("jobWasExecuted");

        String jobName = context.getJobDetail().getKey().toString();
        System.out.println("Job : " + jobName + " is finished...");
        if (jobException != null && !jobException.getMessage().equals("")) {
            System.out.println("Exception thrown by: " + jobName
                    + " Exception: " + jobException.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        JobKey jobKey = new JobKey("dummyJobName", "group1");
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity(jobKey).build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("dummyTriggerName", "group1")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();

        //Listener attached to jobKey
        scheduler.getListenerManager().addJobListener(
                new HelloJobListener(), KeyMatcher.keyEquals(jobKey)
        );

        //Listener attached to group named "group 1" only.
        //scheduler.getListenerManager().addJobListener(
        //	new HelloJobListener(), GroupMatcher.jobGroupEquals("group1")
        //);

        scheduler.start();
        scheduler.scheduleJob(job, trigger);

    }
}