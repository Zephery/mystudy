package com.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2017/9/8 16:06
 * Description:
 */
public class ZKUtil {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(ZKUtil.class);
    private static ZKUtil zkUtil = null;
    private static Configuration configuration;

    static {
        try {
            configuration = new PropertiesConfiguration("load.properties");
        } catch (ConfigurationException e) {
            logger.error("读取配置文件错误", e);
        }
    }

    private ZkClient zkClient = null;

    public ZKUtil() {
        initZKClient();
    }

    public static ZKUtil getInstance() {
        if (zkUtil == null) {
            zkUtil = createInstantce();
        }
        return zkUtil;
    }

    public synchronized static ZKUtil createInstantce() {
        if (zkUtil == null)
            zkUtil = new ZKUtil();
        return zkUtil;
    }

    private void initZKClient() {
        zkClient = new ZkClient(configuration.getString(""), configuration.getInt("timeout"));//TODO 链接可以修改
    }
}