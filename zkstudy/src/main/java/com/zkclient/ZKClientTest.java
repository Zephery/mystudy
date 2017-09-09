package com.zkclient;

import org.I0Itec.zkclient.ZkClient;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/8 15:50
 * Description:
 */
public class ZKClientTest {
    public static void main(String[] args) {
        try {
            ZkClient zkClient=new ZkClient("116.196.115.250:2181",5000);
            System.out.println("ZK is established");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}