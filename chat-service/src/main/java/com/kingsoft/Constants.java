package com.kingsoft;

import com.kingsoft.utils.ServerUtil;

/**
 * Created by LIUYANMIN on 2017/10/17.
 */
public class Constants {

    public static int REDIS_EXPIRE_TIME_ONE_HOUR = 3600;

    public static String REDIS_CHAT_ONLINE_COUNT = "redis_chat_online_count";

    public static String REDIS_CHAT_ONLINE_COUNT_ONE =  ServerUtil.getLocalIP() + ":" + (String) ServerUtil.getEndPoints().get("port");

}
