package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMapper {
    
    /**
     * 插入消息
     */
    int insertMessage(ChatMessage message);
    
    /**
     * 获取两个用户之间的聊天记录
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @return 消息列表（按时间排序）
     */
    List<ChatMessage> getChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * 获取用户的所有联系人列表（去重）
     */
    List<Long> getContactList(@Param("userId") Long userId);
    
    /**
     * 标记消息为已读
     */
    int markAsRead(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    
    /**
     * 获取未读消息数量
     */
    int getUnreadCount(@Param("receiverId") Long receiverId);
}
