package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.CoachStudentRelation;
import com.usbtj.springboot.swimming.entity.PlanDetail;
import com.usbtj.springboot.swimming.entity.TrainingPlan;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.CoachStudentMapper;
import com.usbtj.springboot.swimming.mapper.PlanDetailMapper;
import com.usbtj.springboot.swimming.mapper.TrainingPlanMapper;
import com.usbtj.springboot.swimming.mapper.UserMapper;
import com.usbtj.springboot.swimming.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建计划请求参数
 */
class CreatePlanRequest {
    private Long studentId;
    private String planName;
    private String description;
    private String startDate;
    private Integer days;
    private String status;
    private List<DayDetail> dayDetails;
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<DayDetail> getDayDetails() { return dayDetails; }
    public void setDayDetails(List<DayDetail> dayDetails) { this.dayDetails = dayDetails; }
}

class DayDetail {
    private Integer dayNumber;
    private String trainingDate;
    private String trainingItem;
    private Integer duration;
    private Integer distance;
    private String intensity;
    private String notes;
    
    public Integer getDayNumber() { return dayNumber; }
    public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }
    public String getTrainingDate() { return trainingDate; }
    public void setTrainingDate(String trainingDate) { this.trainingDate = trainingDate; }
    public String getTrainingItem() { return trainingItem; }
    public void setTrainingItem(String trainingItem) { this.trainingItem = trainingItem; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Integer getDistance() { return distance; }
    public void setDistance(Integer distance) { this.distance = distance; }
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

@Controller
@RequestMapping("/api")
public class TrainingPlanController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingPlanController.class);
    private final TrainingPlanMapper trainingPlanMapper;
    private final CoachStudentMapper coachStudentMapper;
    private final UserMapper userMapper;
    private final PlanDetailMapper planDetailMapper;

    public TrainingPlanController(TrainingPlanMapper trainingPlanMapper, 
                                  CoachStudentMapper coachStudentMapper, 
                                  UserMapper userMapper,
                                  PlanDetailMapper planDetailMapper) {
        this.trainingPlanMapper = trainingPlanMapper;
        this.coachStudentMapper = coachStudentMapper;
        this.userMapper = userMapper;
        this.planDetailMapper = planDetailMapper;
    }

    /**
     * 显示训练计划页面
     */
    @GetMapping("/training-plan")
    public String trainingPlanPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/";
        }
        if (!"COACH".equals(user.getRole())) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", user);
        return "index";
    }

    /**
     * 获取教练关联的学员列表
     */
    @GetMapping("/training-plan/students")
    @ResponseBody
    public Result<List<CoachStudentRelation>> getStudents(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<CoachStudentRelation> students = coachStudentMapper.findByCoachId(user.getId());
            return Result.success(students);
        } catch (Exception e) {
            logger.error("获取学员列表失败: {}", e.getMessage(), e);
            return Result.error("获取列表失败");
        }
    }

    /**
     * 获取所有学员列表（用于添加学员下拉选择）
     */
    @GetMapping("/training-plan/all-students")
    @ResponseBody
    public Result<List<Map<String, Object>>> getAllStudents(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            // 获取所有学员用户
            List<User> allStudents = trainingPlanMapper.findAllStudents();
            List<Map<String, Object>> studentList = allStudents.stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId());
                    map.put("username", s.getUsername());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
            return Result.success(studentList);
        } catch (Exception e) {
            logger.error("获取所有学员失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 添加教练-学员关联
     */
    @PostMapping("/training-plan/relation")
    @ResponseBody
    public Result<String> addRelation(@RequestParam Long studentId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            // 检查学员是否存在
            User student = userMapper.findById(studentId);
            if (student == null) {
                return Result.error("学员不存在");
            }
            if (!"STUDENT".equals(student.getRole())) {
                return Result.error("该用户不是学员");
            }
            
            // 检查是否已存在关联
            CoachStudentRelation existing = coachStudentMapper.findByCoachAndStudent(user.getId(), studentId);
            if (existing != null) {
                return Result.error("该学员已关联");
            }

            CoachStudentRelation relation = new CoachStudentRelation();
            relation.setCoachId(user.getId());
            relation.setStudentId(studentId);
            relation.setRelationshipType("正式学员");
            relation.setStartDate(LocalDate.now());
            relation.setStatus(1);

            coachStudentMapper.insert(relation);
            logger.info("教练 {} 添加学员 {} 成功", user.getUsername(), student.getUsername());
            return Result.success("关联成功");
        } catch (Exception e) {
            logger.error("添加关联失败: {}", e.getMessage(), e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定学员的训练计划列表
     */
    @GetMapping("/training-plan/list")
    @ResponseBody
    public Result<List<TrainingPlan>> getPlans(@RequestParam Long studentId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            // 验证教练是否有权限访问该学员的计划
            CoachStudentRelation relation = coachStudentMapper.findByCoachAndStudent(user.getId(), studentId);
            if (relation == null) {
                return Result.error("无权访问该学员的计划");
            }

            List<TrainingPlan> plans = trainingPlanMapper.findByCoachAndStudent(user.getId(), studentId);
            return Result.success(plans);
        } catch (Exception e) {
            logger.error("获取计划列表失败: {}", e.getMessage(), e);
            return Result.error("获取列表失败");
        }
    }

    /**
     * 获取单天训练详情
     */
    @GetMapping("/training-plan/detail-detail")
    @ResponseBody
    public Result<PlanDetail> getDayDetail(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            PlanDetail detail = planDetailMapper.findById(id);
            if (detail == null) {
                return Result.error("记录不存在");
            }
            
            // 验证权限
            TrainingPlan plan = trainingPlanMapper.findById(detail.getPlanId());
            if (plan == null || !plan.getCoachId().equals(user.getId())) {
                return Result.error("无权访问");
            }
            
            return Result.success(detail);
        } catch (Exception e) {
            logger.error("获取详情失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 更新单天训练详情
     */
    @PostMapping("/training-plan/update-detail")
    @ResponseBody
    public Result<Long> updateDayDetail(@RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            Long detailId = Long.valueOf(requestData.get("id").toString());
            String intensity = (String) requestData.get("intensity");
            String notes = (String) requestData.get("notes");
            Boolean isChecked = Boolean.valueOf(requestData.get("isChecked").toString());
            
            PlanDetail detail = planDetailMapper.findById(detailId);
            if (detail == null) {
                return Result.error("记录不存在");
            }
            
            // 验证权限
            TrainingPlan plan = trainingPlanMapper.findById(detail.getPlanId());
            if (plan == null || !plan.getCoachId().equals(user.getId())) {
                return Result.error("无权操作");
            }
            
            detail.setIntensity(intensity);
            detail.setNotes(notes);
            detail.setIsChecked(isChecked);
            planDetailMapper.update(detail);
            
            return Result.success(detail.getPlanId());
        } catch (Exception e) {
            logger.error("更新失败: {}", e.getMessage(), e);
            return Result.error("更新失败");
        }
    }
    @GetMapping("/training-plan/detail")
    @ResponseBody
    public Result<Map<String, Object>> getPlanDetail(@RequestParam(required = false) Long id,
                                                     @PathVariable(required = false) Long planId,
                                                     HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        Long targetId = id != null ? id : planId;
        if (targetId == null) {
            return Result.error("缺少计划ID");
        }

        try {
            TrainingPlan plan = trainingPlanMapper.findById(targetId);
            if (plan == null || !plan.getCoachId().equals(user.getId())) {
                return Result.error("无权访问该计划");
            }

            List<PlanDetail> details = planDetailMapper.findByPlanId(targetId);
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
     * 打卡/取消打卡
     */
    @PostMapping("/training-plan/check/{detailId}")
    @ResponseBody
    public Result<String> toggleCheck(@PathVariable Long detailId, @RequestParam Boolean isChecked, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            PlanDetail detail = planDetailMapper.findById(detailId);
            if (detail == null) {
                return Result.error("记录不存在");
            }

            planDetailMapper.updateCheckStatus(detailId, isChecked);
            return Result.success(isChecked ? "打卡成功" : "已取消打卡");
        } catch (Exception e) {
            logger.error("打卡失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    /**
     * 创建训练计划
     */
    @PostMapping("/training-plan/create")
    @ResponseBody
    public Result<String> createPlan(@RequestBody CreatePlanRequest request, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            // 验证教练是否有权限
            CoachStudentRelation relation = coachStudentMapper.findByCoachAndStudent(user.getId(), request.getStudentId());
            if (relation == null) {
                return Result.error("无权为该学员制定计划");
            }

            // 验证天数
            if (request.getDays() < 5) {
                return Result.error("计划周期最短为5天");
            }
            if (request.getDays() > 14) {
                return Result.error("单个计划周期最长为14天，超过请创建新计划");
            }

            LocalDate start = LocalDate.parse(request.getStartDate());
            LocalDate end = start.plusDays(request.getDays() - 1);

            TrainingPlan plan = new TrainingPlan();
            plan.setCoachId(user.getId());
            plan.setStudentId(request.getStudentId());
            plan.setPlanName(request.getPlanName());
            plan.setDescription(request.getDescription());
            plan.setStartDate(start);
            plan.setEndDate(end);
            plan.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");

            trainingPlanMapper.insert(plan);
            
            // 根据前端传递的每天数据创建训练任务
            if (request.getDayDetails() != null && !request.getDayDetails().isEmpty()) {
                for (DayDetail detail : request.getDayDetails()) {
                    PlanDetail planDetail = new PlanDetail();
                    planDetail.setPlanId(plan.getId());
                    planDetail.setDayNumber(detail.getDayNumber());
                    planDetail.setTrainingDate(LocalDate.parse(detail.getTrainingDate()));
                    planDetail.setTrainingItem(detail.getTrainingItem());
                    planDetail.setDuration(detail.getDuration());
                    planDetail.setDistance(detail.getDistance());
                    planDetail.setIntensity(detail.getIntensity());
                    planDetail.setNotes(detail.getNotes());
                    planDetail.setIsChecked(false);
                    planDetail.setSortOrder(1);
                    planDetailMapper.insert(planDetail);
                }
            }
            
            logger.info("教练 {} 为学员 {} 创建 {} 天训练计划成功", user.getUsername(), request.getStudentId(), request.getDays());
            return Result.success("计划创建成功");
        } catch (Exception e) {
            logger.error("创建计划失败: {}", e.getMessage(), e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新训练计划
     */
    @PostMapping("/training-plan/update")
    @ResponseBody
    public Result<String> updatePlan(@RequestParam Long id,
                                     @RequestParam String planName,
                                     @RequestParam String description,
                                     @RequestParam String startDate,
                                     @RequestParam Integer days,
                                     @RequestParam String status,
                                     HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            TrainingPlan plan = trainingPlanMapper.findById(id);
            if (plan == null || !plan.getCoachId().equals(user.getId())) {
                return Result.error("无权修改该计划");
            }

            // 验证天数
            if (days < 5) {
                return Result.error("计划周期最短为5天");
            }
            if (days > 14) {
                return Result.error("单个计划周期最长为14天");
            }

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = start.plusDays(days - 1);

            plan.setPlanName(planName);
            plan.setDescription(description);
            plan.setStartDate(start);
            plan.setEndDate(end);
            plan.setStatus(status);

            trainingPlanMapper.update(plan);
            
            // 如果天数变化，重新生成每天的任务
            int oldDays = (int) ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
            if (oldDays != days) {
                planDetailMapper.deleteByPlanId(id);
                for (int i = 1; i <= days; i++) {
                    PlanDetail detail = new PlanDetail();
                    detail.setPlanId(id);
                    detail.setDayNumber(i);
                    detail.setTrainingDate(start.plusDays(i - 1));
                    detail.setTrainingItem("自由泳基础训练");
                    detail.setDuration(60);
                    detail.setDistance(1000);
                    detail.setIntensity("中等");
                    detail.setNotes("第 " + i + " 天训练任务");
                    detail.setIsChecked(false);
                    detail.setSortOrder(1);
                    planDetailMapper.insert(detail);
                }
            }
            
            return Result.success("计划更新成功");
        } catch (Exception e) {
            logger.error("更新计划失败: {}", e.getMessage(), e);
            return Result.error("更新失败");
        }
    }

    /**
     * 删除训练计划
     */
    @PostMapping("/training-plan/delete")
    @ResponseBody
    public Result<String> deletePlan(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            TrainingPlan plan = trainingPlanMapper.findById(id);
            if (plan == null || !plan.getCoachId().equals(user.getId())) {
                return Result.error("无权删除该计划");
            }

            trainingPlanMapper.deleteById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            logger.error("删除计划失败: {}", e.getMessage(), e);
            return Result.error("删除失败");
        }
    }

    /**
     * 获取教练的所有训练计划（用于Dashboard统计）
     */
    @GetMapping("/training-plan/my-plans")
    @ResponseBody
    public Result<List<TrainingPlan>> getPlanList(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<TrainingPlan> plans = trainingPlanMapper.findByCoachId(user.getId());
            return Result.success(plans);
        } catch (Exception e) {
            logger.error("获取训练计划列表失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 获取教练的学员列表（用于Dashboard统计）
     */
    @GetMapping("/coach/students")
    @ResponseBody
    public Result<List<CoachStudentRelation>> getCoachStudents(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<CoachStudentRelation> students = coachStudentMapper.findByCoachId(user.getId());
            return Result.success(students);
        } catch (Exception e) {
            logger.error("获取学员列表失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }
}
