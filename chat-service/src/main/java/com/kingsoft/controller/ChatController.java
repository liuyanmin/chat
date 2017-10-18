package com.kingsoft.controller;

import com.kingsoft.Constants;
import com.kingsoft.service.ITransferMessage;
import com.kingsoft.service.RedisService;
import com.kingsoft.tools.WebApplicationContext;
import com.kingsoft.utils.RedisTemplate;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by LIUYANMIN on 2017/10/16.
 */

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */

@ServerEndpoint(value = "/chat")
public class ChatController extends RedisService {

    /**
     * 类使用 @ServerEndpoint 注解后无法自动注入bean，所以不能使用@Autowired、@Resource注解自动注入
     */
    private ITransferMessage transferMessage;
    private RedisTemplate redisTemplate;
    public ChatController() {
        transferMessage = (ITransferMessage) WebApplicationContext.getBean("transferMessage");
        redisTemplate = (RedisTemplate) WebApplicationContext.getBean("redisTemplate");
    }

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
     */
    public static CopyOnWriteArraySet<ChatController> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
        setSession(session);
        redisTemplate.hincrBy(Constants.REDIS_CHAT_ONLINE_COUNT, Constants.REDIS_CHAT_ONLINE_COUNT_ONE, 1);
        webSocketSet.add(this);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        redisTemplate.hincrBy(Constants.REDIS_CHAT_ONLINE_COUNT, Constants.REDIS_CHAT_ONLINE_COUNT_ONE, -1);
        webSocketSet.remove(this);
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        try {
            message = URLEncoder.encode(message, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        transferMessage.transferTextMsg(message);
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 向客户端发送消息
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

}
