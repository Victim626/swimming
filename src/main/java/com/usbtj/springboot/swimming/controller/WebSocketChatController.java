package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.ChatMessage;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.ChatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * WebSocket聊天控制器
 */
@Controller
public class WebSocketChatController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;

    public WebSocketChatController(SimpMessagingTemplate messagingTemplate, ChatMapper chatMapper) {
        this.messagingTemplate = messagingTemplate;
        this.chatMapper = chatMapper;
    }

    /**
     * 处理客户端发送的聊天消息
     * @param message 消息内容
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        try {
            // 保存到数据库
            message.setSendTime(LocalDateTime.now());
            message.setIsRead(false);
            chatMapper.insertMessage(message);

            // 发送给接收者
            String destination = "/queue/messages/" + message.getReceiverId();
            messagingTemplate.convertAndSend(destination, message);

            logger.info("WebSocket消息发送: 用户{} -> 用户{}", 
                message.getSenderId(), message.getReceiverId());
        } catch (Exception e) {
            logger.error("WebSocket消息发送失败", e);
        }
    }

    /**
     * 用户加入聊天
     */
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // 存储用户名到WebSocket session
        headerAccessor.getSessionAttributes().put("username", message.getSenderName());
        
        logger.info("用户加入聊天: {}", message.getSenderName());
    }
}
