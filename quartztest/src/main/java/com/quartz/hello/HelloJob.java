package com.quartz.hello;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/11 10:01
 * Description:
 */
public class HelloJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Say Hello to the World and display the date/time
        System.out.println("Hello World! - " + new Date());
    }
}