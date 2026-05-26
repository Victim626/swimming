package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    int insert(Comment comment);

    int update(Comment comment);

    int softDelete(@Param("id") Long id);

    Comment findById(@Param("id") Long id, @Param("currentUserId") Long currentUserId);

    List<Comment> findByPostId(@Param("postId") Long postId, @Param("currentUserId") Long currentUserId);

    int insertLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

    int deleteLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

    int incrementLikeCount(@Param("commentId") Long commentId);

    int decrementLikeCount(@Param("commentId") Long commentId);
}
