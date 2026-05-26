package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.Comment;
import com.usbtj.springboot.swimming.entity.Post;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.CommentMapper;
import com.usbtj.springboot.swimming.mapper.PostMapper;
import com.usbtj.springboot.swimming.response.Result;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    public CommentController(CommentMapper commentMapper, PostMapper postMapper) {
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
    }

    @GetMapping("/posts/{postId}/comments")
    public Result<List<Comment>> getComments(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            List<Comment> comments = buildCommentTree(commentMapper.findByPostId(postId, user.getId()));
            return Result.success(comments);
        } catch (Exception e) {
            logger.error("获取评论失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    @PostMapping("/posts/{postId}/comments")
    @Transactional
    public Result<Comment> createComment(@PathVariable Long postId, @RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Post post = postMapper.findById(postId, user.getId());
            if (post == null) {
                return Result.error("帖子不存在");
            }

            String content = requestData.get("content") == null ? "" : requestData.get("content").toString().trim();
            if (content.isEmpty()) {
                return Result.error("评论内容不能为空");
            }

            Long parentId = null;
            Object parentIdObj = requestData.get("parentId");
            if (parentIdObj != null && !parentIdObj.toString().isBlank()) {
                parentId = Long.valueOf(parentIdObj.toString());
                Comment parent = commentMapper.findById(parentId, user.getId());
                if (parent == null || !postId.equals(parent.getPostId()) || Boolean.TRUE.equals(parent.getDeleted())) {
                    return Result.error("回复目标不存在");
                }
            }

            Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setUserId(user.getId());
            comment.setContent(content);
            comment.setParentId(parentId);
            comment.setLikeCount(0);
            comment.setDeleted(false);

            commentMapper.insert(comment);
            postMapper.incrementCommentCount(postId);
            return Result.success(commentMapper.findById(comment.getId(), user.getId()));
        } catch (Exception e) {
            logger.error("创建评论失败: {}", e.getMessage(), e);
            return Result.error("创建失败");
        }
    }

    @PutMapping("/comments/{commentId}")
    public Result<Comment> updateComment(@PathVariable Long commentId, @RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Comment comment = commentMapper.findById(commentId, user.getId());
            if (comment == null) {
                return Result.error("评论不存在");
            }
            if (!user.getId().equals(comment.getUserId())) {
                return Result.error("无权操作");
            }
            if (Boolean.TRUE.equals(comment.getDeleted())) {
                return Result.error("评论已删除");
            }

            String content = requestData.get("content") == null ? "" : requestData.get("content").toString().trim();
            if (content.isEmpty()) {
                return Result.error("评论内容不能为空");
            }

            comment.setContent(content);
            commentMapper.update(comment);
            return Result.success(commentMapper.findById(commentId, user.getId()));
        } catch (Exception e) {
            logger.error("更新评论失败: {}", e.getMessage(), e);
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Comment comment = commentMapper.findById(commentId, user.getId());
            if (comment == null) {
                return Result.error("评论不存在");
            }
            if (Boolean.TRUE.equals(comment.getDeleted())) {
                return Result.error("评论已删除");
            }
            if (!user.getId().equals(comment.getUserId())) {
                return Result.error("无权操作");
            }
            commentMapper.softDelete(commentId);
            postMapper.decrementCommentCount(comment.getPostId());
            return Result.success(null);
        } catch (Exception e) {
            logger.error("删除评论失败: {}", e.getMessage(), e);
            return Result.error("删除失败");
        }
    }

    @PostMapping("/comments/{commentId}/like")
    @Transactional
    public Result<Void> likeComment(@PathVariable Long commentId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Comment comment = commentMapper.findById(commentId, user.getId());
            if (comment == null) {
                return Result.error("评论不存在");
            }
            if (Boolean.TRUE.equals(comment.getDeleted())) {
                return Result.error("评论已删除");
            }

            int inserted = commentMapper.insertLike(commentId, user.getId());
            if (inserted > 0) {
                commentMapper.incrementLikeCount(commentId);
            }
            return Result.success(null);
        } catch (Exception e) {
            logger.error("点赞评论失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    @DeleteMapping("/comments/{commentId}/like")
    @Transactional
    public Result<Void> unlikeComment(@PathVariable Long commentId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Comment comment = commentMapper.findById(commentId, user.getId());
            if (comment == null) {
                return Result.error("评论不存在");
            }
            if (Boolean.TRUE.equals(comment.getDeleted())) {
                return Result.error("评论已删除");
            }

            int deleted = commentMapper.deleteLike(commentId, user.getId());
            if (deleted > 0) {
                commentMapper.decrementLikeCount(commentId);
            }
            return Result.success(null);
        } catch (Exception e) {
            logger.error("取消评论点赞失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    private List<Comment> buildCommentTree(List<Comment> flatComments) {
        Map<Long, Comment> commentMap = new LinkedHashMap<>();
        for (Comment comment : flatComments) {
            comment.setReplies(new ArrayList<>());
            commentMap.put(comment.getId(), comment);
        }

        List<Comment> roots = new ArrayList<>();
        for (Comment comment : flatComments) {
            Long parentId = comment.getParentId();
            if (parentId == null) {
                roots.add(comment);
                continue;
            }

            Comment parent = commentMap.get(parentId);
            if (parent == null) {
                roots.add(comment);
                continue;
            }

            parent.getReplies().add(comment);
        }

        return roots;
    }
}
