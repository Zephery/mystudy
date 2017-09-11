package com.quartz.springtask;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/11 13:02
 * Description:
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/aaa.xml");
        ScheduledTaskService ss = (ScheduledTaskService) context.getBean("ss");
        System.out.println();
    }
}