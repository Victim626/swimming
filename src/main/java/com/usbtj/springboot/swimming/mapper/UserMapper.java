package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    
    User findByUsername(@Param("username") String username);
    
    User findById(@Param("id") Long id);
    
    int insert(User user);
    
    int deleteById(@Param("id") Long id);
    
    /**
     * 获取所有用户（排除当前用户）
     */
    List<User> findAllUsers(@Param("excludeUserId") Long excludeUserId);
    
    /**
     * 更新用户信息
     */
    int update(User user);
}
