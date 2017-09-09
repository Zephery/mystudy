package com.distributelock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/9 19:25
 * Description:
 */
public class Recipes_NoLock {
    public static void main(String[] args) throws Exception {
        final CountDownLatch downLatch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        downLatch.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sdf.format(new Date());
                    System.out.println(orderNo);
                }
            }).start();
        }
        downLatch.countDown();
    }
}