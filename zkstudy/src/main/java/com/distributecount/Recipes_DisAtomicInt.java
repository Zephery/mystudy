package com.distributecount;

import com.common.Config;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/9 20:18
 * Description:
 */
public class Recipes_DisAtomicInt {
    static String distatomicint_path = "/curator_receipes_distatomicint_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(Config.getProperty("zookeeperurl"))
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    public static void main(String[] args) throws Exception {
        client.start();
        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, distatomicint_path, new RetryNTimes(3, 1000));
        AtomicValue<Integer> rc = atomicInteger.add(8);
        System.out.println("Result:" + rc.succeeded());
    }

}
