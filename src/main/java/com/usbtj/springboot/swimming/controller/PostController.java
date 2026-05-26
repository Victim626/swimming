package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.PlanDetail;
import com.usbtj.springboot.swimming.entity.Post;
import com.usbtj.springboot.swimming.entity.TrainingPlan;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.PlanDetailMapper;
import com.usbtj.springboot.swimming.mapper.PostMapper;
import com.usbtj.springboot.swimming.mapper.TrainingPlanMapper;
import com.usbtj.springboot.swimming.response.Result;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final PostMapper postMapper;
    private final PlanDetailMapper planDetailMapper;
    private final TrainingPlanMapper trainingPlanMapper;

    public PostController(PostMapper postMapper, 
                         PlanDetailMapper planDetailMapper,
                         TrainingPlanMapper trainingPlanMapper) {
        this.postMapper = postMapper;
        this.planDetailMapper = planDetailMapper;
        this.trainingPlanMapper = trainingPlanMapper;
    }

    /**
     * 获取打卡数据列表（用于发帖时选择）
     */
    @GetMapping("/checkin-data")
    public Result<List<Map<String, Object>>> getCheckinData(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            // 查询该学员的所有训练计划详情
            List<TrainingPlan> plans = trainingPlanMapper.findByStudentId(user.getId());
            List<Map<String, Object>> checkinData = new java.util.ArrayList<>();
            
            for (TrainingPlan plan : plans) {
                List<PlanDetail> details = planDetailMapper.findByPlanId(plan.getId());
                for (PlanDetail detail : details) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", detail.getId());
                    item.put("planName", plan.getPlanName());
                    item.put("dayNumber", detail.getDayNumber());
                    item.put("trainingDate", detail.getTrainingDate());
                    item.put("intensity", detail.getIntensity());
                    item.put("duration", detail.getDuration());
                    item.put("distance", detail.getDistance());
                    item.put("isChecked", detail.getIsChecked());
                    checkinData.add(item);
                }
            }
            
            return Result.success(checkinData);
        } catch (Exception e) {
            logger.error("获取打卡数据失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 创建帖子
     */
    @PostMapping
    public Result<Post> createPost(@RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Post post = new Post();
            post.setUserId(user.getId());
            post.setTitle((String) requestData.get("title"));
            post.setContent((String) requestData.get("content"));
            
            // 关联的打卡数据（可选）
            Object planDetailIdObj = requestData.get("planDetailId");
            if (planDetailIdObj != null) {
                Long planDetailId = Long.valueOf(planDetailIdObj.toString());
                post.setPlanDetailId(planDetailId);
                
                // 自动填充打卡信息
                PlanDetail detail = planDetailMapper.findById(planDetailId);
                if (detail != null) {
                    TrainingPlan plan = trainingPlanMapper.findById(detail.getPlanId());
                    post.setDayNumber(detail.getDayNumber());
                    post.setPlanName(plan != null ? plan.getPlanName() : null);
                    post.setTrainingDate(detail.getTrainingDate());
                    post.setIntensity(detail.getIntensity());
                    post.setDuration(detail.getDuration());
                    post.setDistance(detail.getDistance());
                }
            }
            
            // 是否显示训练信息
            Object showInfoObj = requestData.get("showTrainingInfo");
            post.setShowTrainingInfo(showInfoObj != null && Boolean.parseBoolean(showInfoObj.toString()));
            
            postMapper.insert(post);
            return Result.success(postMapper.findById(post.getId(), user.getId()));
        } catch (Exception e) {
            logger.error("创建帖子失败: {}", e.getMessage(), e);
            return Result.error("创建失败");
        }
    }

    /**
     * 更新帖子
     */
    @PutMapping("/{id}")
    public Result<Post> updatePost(@PathVariable Long id, @RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Post post = postMapper.findById(id, user.getId());
            if (post == null) {
                return Result.error("帖子不存在");
            }
            
            if (!post.getUserId().equals(user.getId())) {
                return Result.error("无权操作");
            }
            
            post.setTitle((String) requestData.get("title"));
            post.setContent((String) requestData.get("content"));
            
            Object planDetailIdObj = requestData.get("planDetailId");
            if (planDetailIdObj != null) {
                Long planDetailId = Long.valueOf(planDetailIdObj.toString());
                post.setPlanDetailId(planDetailId);
                
                PlanDetail detail = planDetailMapper.findById(planDetailId);
                if (detail != null) {
                    TrainingPlan plan = trainingPlanMapper.findById(detail.getPlanId());
                    post.setDayNumber(detail.getDayNumber());
                    post.setPlanName(plan != null ? plan.getPlanName() : null);
                    post.setTrainingDate(detail.getTrainingDate());
                    post.setIntensity(detail.getIntensity());
                    post.setDuration(detail.getDuration());
                    post.setDistance(detail.getDistance());
                }
            } else {
                post.setPlanDetailId(null);
                post.setDayNumber(null);
                post.setPlanName(null);
                post.setTrainingDate(null);
                post.setIntensity(null);
                post.setDuration(null);
                post.setDistance(null);
            }
            
            Object showInfoObj = requestData.get("showTrainingInfo");
            post.setShowTrainingInfo(showInfoObj != null && Boolean.parseBoolean(showInfoObj.toString()));
            
            postMapper.update(post);
            return Result.success(postMapper.findById(id, user.getId()));
        } catch (Exception e) {
            logger.error("更新帖子失败: {}", e.getMessage(), e);
            return Result.error("更新失败");
        }
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Post post = postMapper.findById(id, user.getId());
            if (post == null) {
                return Result.error("帖子不存在");
            }
            
            if (!post.getUserId().equals(user.getId())) {
                return Result.error("无权操作");
            }
            
            postMapper.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            logger.error("删除帖子失败: {}", e.getMessage(), e);
            return Result.error("删除失败");
        }
    }

    /**
     * 获取帖子列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            int offset = (page - 1) * pageSize;
            List<Post> posts = postMapper.findAll(offset, pageSize, user.getId());
            int total = postMapper.countAll();
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", posts);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", (int) Math.ceil((double) total / pageSize));
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取帖子列表失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{id}")
    public Result<Post> getPostDetail(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Post post = postMapper.findById(id, user.getId());
            if (post == null) {
                return Result.error("帖子不存在");
            }
            return Result.success(post);
        } catch (Exception e) {
            logger.error("获取帖子详情失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 点赞
     */
    @PostMapping("/{id}/like")
    @Transactional
    public Result<Void> likePost(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Post post = postMapper.findById(id, user.getId());
            if (post == null) {
                return Result.error("帖子不存在");
            }
            int inserted = postMapper.insertLike(id, user.getId());
            if (inserted > 0) {
                postMapper.incrementLikeCount(id);
            }
            return Result.success(null);
        } catch (Exception e) {
            logger.error("点赞失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{id}/like")
    @Transactional
    public Result<Void> unlikePost(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        try {
            Post post = postMapper.findById(id, user.getId());
            if (post == null) {
                return Result.error("帖子不存在");
            }
            int deleted = postMapper.deleteLike(id, user.getId());
            if (deleted > 0) {
                postMapper.decrementLikeCount(id);
            }
            return Result.success(null);
        } catch (Exception e) {
            logger.error("取消点赞失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }
}
