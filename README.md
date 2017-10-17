# chat
支持webSocket协议的分布式聊天系统后台

webSocket聊天系统后台，支持分布式部署。使用的技术有SpringMVC+dubbo+zookeeper+redis。

chat项目包括三个模块：
chat-api:通信接口
chat-provider:dubbo服务
chat-service:业务处理

启动注意事项：
chat-provider模块与chat-service模块要放到同一个web容器中启动。因为chat-provider模块内部需要调用chat-service模块的接口，通过Http的方式调用，所以两个模块的IP地址和端口号要一致。

处理流程：
1、客户端发起请求创建WebSocket链接
2、服务端chat-service模块接收请求，与客户端建立链接，并保存session信息到内存中，在线人数+1（服务端与客户端断开链接时，在线人数-1）
3、chat-service模块对消息进行编码，使用dubbo广播的方式调用chat-provider模块的服务
4、chat-provider模块服务内部再通过HTTP的方式回调chat-service模块（chat-provider只通知当前web容器内的chat-service模块）
5、chat-service模块再收到HTTP请求后，将消息进行解码，发送消息给本地维护的客户端
