package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.PlanDetail;
import com.usbtj.springboot.swimming.entity.TrainingPlan;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.PlanDetailMapper;
import com.usbtj.springboot.swimming.mapper.TrainingPlanMapper;
import com.usbtj.springboot.swimming.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/student")
public class StudentTrainingController {

    private static final Logger logger = LoggerFactory.getLogger(StudentTrainingController.class);
    private final TrainingPlanMapper trainingPlanMapper;
    private final PlanDetailMapper planDetailMapper;

    public StudentTrainingController(TrainingPlanMapper trainingPlanMapper, PlanDetailMapper planDetailMapper) {
        this.trainingPlanMapper = trainingPlanMapper;
        this.planDetailMapper = planDetailMapper;
    }

    /**
     * 显示学员训练计划页面
     */
    @GetMapping("/training")
    public String trainingPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/";
        }
        if (!"STUDENT".equals(user.getRole())) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", user);
        return "index";
    }

    /**
     * 获取今日训练计划
     */
    @GetMapping("/today-plans")
    @ResponseBody
    public Result<List<Map<String, Object>>> getTodayPlans(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            LocalDate today = LocalDate.now();
            List<PlanDetail> allDetails = planDetailMapper.findByStudentId(user.getId());
            
            // 筛选今天的训练任务
            List<Map<String, Object>> todayPlans = allDetails.stream()
                .filter(detail -> detail.getTrainingDate().equals(today))
                .map(detail -> {
                    Map<String, Object> map = new HashMap<>();
                    TrainingPlan plan = trainingPlanMapper.findById(detail.getPlanId());
                    map.put("id", detail.getId());
                    map.put("planId", detail.getPlanId());
                    map.put("planName", plan.getPlanName());
                    map.put("dayNumber", detail.getDayNumber());
                    map.put("trainingItem", detail.getTrainingItem());
                    map.put("duration", detail.getDuration());
                    map.put("distance", detail.getDistance());
                    map.put("intensity", detail.getIntensity());
                    map.put("notes", detail.getNotes());
                    map.put("isChecked", detail.getIsChecked());
                    return map;
                })
                .collect(Collectors.toList());
            
            return Result.success(todayPlans);
        } catch (Exception e) {
            logger.error("获取今日计划失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 打卡/取消打卡
     */
    @PostMapping("/check/{detailId}")
    @ResponseBody
    public Result<String> toggleCheck(@PathVariable Long detailId, @RequestParam Boolean isChecked, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            logger.info("开始打卡 - detailId: {}, isChecked: {}, userId: {}", detailId, isChecked, user.getId());
            PlanDetail detail = planDetailMapper.findById(detailId);
            if (detail == null) {
                logger.error("打卡失败 - 记录不存在: {}", detailId);
                return Result.error("记录不存在");
            }

            // 验证是否为该学员的计划
            TrainingPlan plan = trainingPlanMapper.findById(detail.getPlanId());
            if (plan == null) {
                logger.error("打卡失败 - 计划不存在: {}", detail.getPlanId());
                return Result.error("计划不存在");
            }
            if (!plan.getStudentId().equals(user.getId())) {
                logger.error("打卡失败 - 无权操作, plan.studentId: {}, currentUserId: {}", plan.getStudentId(), user.getId());
                return Result.error("无权操作");
            }

            planDetailMapper.updateCheckStatus(detailId, isChecked);
            logger.info("打卡成功 - detailId: {}, isChecked: {}", detailId, isChecked);
            return Result.success(isChecked ? "打卡成功" : "已取消打卡");
        } catch (Exception e) {
            logger.error("打卡异常: {}", e.getMessage(), e);
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有训练计划
     */
    @GetMapping("/all-plans")
    @ResponseBody
    public Result<List<TrainingPlan>> getAllPlans(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<TrainingPlan> plans = trainingPlanMapper.findByStudentId(user.getId());
            return Result.success(plans);
        } catch (Exception e) {
            logger.error("获取计划列表失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 获取计划详情
     */
    @GetMapping("/plan-detail")
    @ResponseBody
    public Result<Map<String, Object>> getPlanDetail(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            TrainingPlan plan = trainingPlanMapper.findById(id);
            if (plan == null || !plan.getStudentId().equals(user.getId())) {
                return Result.error("无权访问");
            }
            
            List<PlanDetail> details = planDetailMapper.findByPlanId(id);
            Map<String, Object> result = new HashMap<>();
            result.put("plan", plan);
            result.put("details", details);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取计划详情失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取学员关联的教练
     */
    @GetMapping("/my-coach")
    @ResponseBody
    public Result<User> getMyCoach(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            User coach = trainingPlanMapper.findCoachByStudentId(user.getId());
            if (coach == null) {
                return Result.error("暂无关联教练");
            }
            return Result.success(coach);
        } catch (Exception e) {
            logger.error("获取教练失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 获取学员的训练计划列表（用于Dashboard统计）
     */
    @GetMapping("/my-plans")
    @ResponseBody
    public Result<List<TrainingPlan>> getMyPlans(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<TrainingPlan> plans = trainingPlanMapper.findByStudentId(user.getId());
            return Result.success(plans);
        } catch (Exception e) {
            logger.error("获取训练计划失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 获取指定日期的任务列表（用于Dashboard统计）
     */
    @GetMapping("/today-tasks")
    @ResponseBody
    public Result<List<PlanDetail>> getTodayTasks(@RequestParam LocalDate date, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<PlanDetail> allDetails = planDetailMapper.findByStudentId(user.getId());
            // 筛选指定日期的任务
            List<PlanDetail> dayTasks = allDetails.stream()
                .filter(detail -> detail.getTrainingDate().equals(date))
                .collect(Collectors.toList());
            return Result.success(dayTasks);
        } catch (Exception e) {
            logger.error("获取任务列表失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }
}
