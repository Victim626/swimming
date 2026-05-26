package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    
    /**
     * 创建帖子
     */
    int insert(Post post);
    
    /**
     * 更新帖子
     */
    int update(Post post);
    
    /**
     * 删除帖子
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 查询帖子详情
     */
    Post findById(@Param("id") Long id, @Param("currentUserId") Long currentUserId);

    /**
     * 获取所有帖子（分页）
     */
    List<Post> findAll(@Param("offset") int offset, @Param("limit") int limit, @Param("currentUserId") Long currentUserId);

    /**
     * 获取用户的帖子
     */
    List<Post> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit, @Param("currentUserId") Long currentUserId);

    /**
     * 点赞
     */
    int insertLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 取消点赞
     */
    int deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 点赞数+1
     */
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 取消点赞数-1
     */
    int decrementLikeCount(@Param("id") Long id);
    
    /**
     * 增加评论数
     */
    int incrementCommentCount(@Param("id") Long id);
    
    /**
     * 减少评论数
     */
    int decrementCommentCount(@Param("id") Long id);
    
    /**
     * 统计总数
     */
    int countAll();
}
