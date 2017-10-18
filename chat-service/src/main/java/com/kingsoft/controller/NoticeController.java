package com.kingsoft.controller;

import com.kingsoft.Constants;
import com.kingsoft.utils.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LIUYANMIN on 2017/10/16.
 */
@Controller
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 服务端调用此接口，等待通知下发消息
     * @param message
     * @return
     */
    @RequestMapping("/msg")
    @ResponseBody
    public Object broadcastMsg(String message) {
        try {
            message = URLDecoder.decode(message, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //群发消息
        for(ChatController item: ChatController.webSocketSet){
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
        return "OK";
    }

    /**
     * 在线总人数
     * @return
     */
    @RequestMapping("/count")
    @ResponseBody
    public Object onlineCount() {
        Map<String, Object> returnMap = new HashMap<>(16);
        Map<String, String> countMap = redisTemplate.hgetAll(Constants.REDIS_CHAT_ONLINE_COUNT);
        Integer count = 0;
        for (String item : countMap.values()) {
            count += Integer.valueOf(item);
        }
        returnMap.put("count", count);
        return returnMap;
    }

    /**
     * 每台服务器管理的人数
     * @return
     */
    @RequestMapping("/count/all")
    @ResponseBody
    public Object onlineCountOne() {
        Map<String, String> countMap = redisTemplate.hgetAll(Constants.REDIS_CHAT_ONLINE_COUNT);
        return countMap;
    }

}
