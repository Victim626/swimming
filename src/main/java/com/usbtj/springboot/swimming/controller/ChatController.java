package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.ChatMessage;
import com.usbtj.springboot.swimming.entity.CoachStudentRelation;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.ChatMapper;
import com.usbtj.springboot.swimming.mapper.CoachStudentMapper;
import com.usbtj.springboot.swimming.mapper.UserMapper;
import com.usbtj.springboot.swimming.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatMapper chatMapper;
    private final UserMapper userMapper;
    private final CoachStudentMapper coachStudentMapper;

    public ChatController(ChatMapper chatMapper, UserMapper userMapper, CoachStudentMapper coachStudentMapper) {
        this.chatMapper = chatMapper;
        this.userMapper = userMapper;
        this.coachStudentMapper = coachStudentMapper;
    }

    /**
     * 获取可聊天的用户列表（根据角色过滤）
     */
    @GetMapping("/users")
    public Result<List<User>> getAllUsers(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        try {
            List<User> users = new ArrayList<>();
            
            if ("STUDENT".equals(currentUser.getRole())) {
                // 学员只能看到自己的教练
                List<CoachStudentRelation> relations = coachStudentMapper.findByStudentId(currentUser.getId());
                for (CoachStudentRelation relation : relations) {
                    User coach = new User();
                    coach.setId(relation.getCoachId());
                    coach.setUsername(relation.getCoachUsername());
                    coach.setRole("COACH");
                    users.add(coach);
                }
            } else if ("COACH".equals(currentUser.getRole())) {
                // 教练可以看到自己的学员
                List<CoachStudentRelation> relations = coachStudentMapper.findByCoachId(currentUser.getId());
                for (CoachStudentRelation relation : relations) {
                    User student = new User();
                    student.setId(relation.getStudentId());
                    student.setUsername(relation.getStudentUsername());
                    student.setRealName(relation.getStudentRealName());
                    student.setRole("STUDENT");
                    users.add(student);
                }
            }
            
            return Result.success(users);
        } catch (Exception e) {
            logger.error("获取用户列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Result<Void> sendMessage(@RequestBody ChatMessage message, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        // 设置发送者ID
        message.setSenderId(currentUser.getId());
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(false);

        try {
            chatMapper.insertMessage(message);
            logger.info("用户 {} 发送消息给用户 {}: {}", 
                currentUser.getId(), message.getReceiverId(), message.getContent());
            return Result.success();
        } catch (Exception e) {
            logger.error("发送消息失败", e);
            return Result.error("发送失败：" + e.getMessage());
        }
    }

    /**
     * 获取与某个用户的聊天记录
     */
    @GetMapping("/history/{receiverId}")
    public Result<List<ChatMessage>> getChatHistory(@PathVariable Long receiverId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        try {
            // 标记消息为已读
            chatMapper.markAsRead(receiverId, currentUser.getId());
            
            // 获取聊天记录
            List<ChatMessage> messages = chatMapper.getChatHistory(currentUser.getId(), receiverId);
            return Result.success(messages);
        } catch (Exception e) {
            logger.error("获取聊天记录失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取联系人列表
     */
    @GetMapping("/contacts")
    public Result<List<Long>> getContacts(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        try {
            List<Long> contacts = chatMapper.getContactList(currentUser.getId());
            return Result.success(contacts);
        } catch (Exception e) {
            logger.error("获取联系人列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread")
    public Result<Map<String, Object>> getUnreadCount(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        try {
            int count = chatMapper.getUnreadCount(currentUser.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            return Result.success(data);
        } catch (Exception e) {
            logger.error("获取未读消息数量失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }
}
