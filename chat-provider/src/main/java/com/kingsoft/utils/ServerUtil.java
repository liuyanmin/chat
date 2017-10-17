package com.kingsoft.utils;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by LIUYANMIN on 2017/10/17.
 */
public class ServerUtil {

    /**
     * 获取主机服务器端ip和端口号
     * @return
     */
    public static Map<String, Object> getEndPoints() {
        Map<String, Object> endPointsMap = new HashMap<>();
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
            String hostname = InetAddress.getLocalHost().getHostName();
            InetAddress[] addresses = InetAddress.getAllByName(hostname);

            if (objs.iterator().hasNext()) {
                ObjectName obj = objs.iterator().next();
                endPointsMap.put("port", obj.getKeyProperty("port"));
                endPointsMap.put("ip", addresses[0].getHostAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endPointsMap;
    }

}
