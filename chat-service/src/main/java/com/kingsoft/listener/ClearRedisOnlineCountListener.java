package com.kingsoft.listener;

import com.kingsoft.Constants;
import com.kingsoft.utils.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Created by LIUYANMIN on 2017/10/17.
 */

/**
 * Spring容器关闭时触发该事件
 */
public class ClearRedisOnlineCountListener implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        // 容器关闭时清除当前服务redis中的连接数
        if(contextClosedEvent.getApplicationContext().getParent() == null) {
            redisTemplate.hdel(Constants.REDIS_CHAT_ONLINE_COUNT, Constants.REDIS_CHAT_ONLINE_COUNT_ONE);
        }
    }

}
