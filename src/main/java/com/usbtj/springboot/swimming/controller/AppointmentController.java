package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.Appointment;
import com.usbtj.springboot.swimming.entity.TimeSlot;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.AppointmentMapper;
import com.usbtj.springboot.swimming.mapper.TimeSlotMapper;
import com.usbtj.springboot.swimming.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentMapper appointmentMapper;
    private final TimeSlotMapper timeSlotMapper;

    public AppointmentController(AppointmentMapper appointmentMapper, TimeSlotMapper timeSlotMapper) {
        this.appointmentMapper = appointmentMapper;
        this.timeSlotMapper = timeSlotMapper;
    }

    /**
     * 教练端预约管理页面
     */
    @GetMapping("/appointment-manage")
    public String appointmentManagePage(HttpSession session, Model model) {
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
     * 学员端预约页面
     */
    @GetMapping("/appointment")
    public String appointmentPage(HttpSession session, Model model) {
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
     * 获取时间段列表
     */
    @GetMapping("/api/time-slots")
    @ResponseBody
    public Result<List<TimeSlot>> getTimeSlots() {
        try {
            List<TimeSlot> slots = timeSlotMapper.findAll();
            return Result.success(slots);
        } catch (Exception e) {
            logger.error("获取时间段失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 获取教练某日期的预约情况
     */
    @GetMapping("/api/appointments/coach/date")
    @ResponseBody
    public Result<List<Appointment>> getCoachAppointmentsByDate(@RequestParam LocalDate date, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            logger.info("教练查询预约 - 教练ID: {}, 日期: {}", user.getId(), date);
            List<Appointment> appointments = appointmentMapper.findByCoachIdAndDate(user.getId(), date);
            logger.info("查询到 {} 条预约记录", appointments.size());
            if (!appointments.isEmpty()) {
                appointments.forEach(app -> 
                    logger.info("预约记录 - ID: {}, 学员ID: {}, 学员名: {}, 时间: {}-{}, 状态: {}",
                        app.getId(), app.getStudentId(), app.getStudentName(), 
                        app.getStartTime(), app.getEndTime(), app.getStatus())
                );
            }
            return Result.success(appointments);
        } catch (Exception e) {
            logger.error("获取预约失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 学员预约
     */
    @PostMapping("/api/appointment/book")
    @ResponseBody
    public Result<String> bookAppointment(@RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }
        if (!"STUDENT".equals(user.getRole())) {
            return Result.error("只有学员可以预约");
        }

        try {
            Long coachId = Long.valueOf(requestData.get("coachId").toString());
            LocalDate appointmentDate = LocalDate.parse(requestData.get("appointmentDate").toString());
            LocalTime startTime = LocalTime.parse(requestData.get("startTime").toString());
            LocalTime endTime = LocalTime.parse(requestData.get("endTime").toString());
            
            // 验证预约日期：必须是明天或之后的日期
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            if (appointmentDate.isBefore(tomorrow)) {
                return Result.error("只能预约明天及以后的日期");
            }
            
            logger.info("学员预约 - 学员ID: {}, 教练ID: {}, 日期: {}, 时间: {}-{}", 
                user.getId(), coachId, appointmentDate, startTime, endTime);
            
            // 检查教练是否设置了该时间段不可用
            List<Appointment> coachAppointments = appointmentMapper.findByCoachIdAndDate(coachId, appointmentDate);
            boolean isUnavailable = coachAppointments.stream().anyMatch(a -> 
                a.getStudentId().equals(coachId) && // 教练自己设置的
                a.getStartTime().equals(startTime) && 
                a.getEndTime().equals(endTime)
            );
            
            if (isUnavailable) {
                return Result.error("该时间段教练已设置为不可用");
            }
            
            // 检查该时间段已确认的预约数量
            int count = appointmentMapper.countConfirmedByCoachDateAndTime(coachId, appointmentDate, startTime, endTime);
            
            // 获取该时间段的最大容量
            List<TimeSlot> slots = timeSlotMapper.findAll();
            int maxCapacity = 5;
            for (TimeSlot slot : slots) {
                if (slot.getStartTime().equals(startTime) && slot.getEndTime().equals(endTime)) {
                    maxCapacity = slot.getMaxCapacity();
                    break;
                }
            }
            
            if (count >= maxCapacity) {
                return Result.error("该时间段已约满");
            }
            
            Appointment appointment = new Appointment();
            appointment.setCoachId(coachId);
            appointment.setStudentId(user.getId());
            appointment.setAppointmentDate(appointmentDate);
            appointment.setStartTime(startTime);
            appointment.setEndTime(endTime);
            appointment.setLocation((String) requestData.get("location"));
            appointment.setStatus("PENDING");
            appointment.setNotes((String) requestData.get("notes"));
            
            int result = appointmentMapper.insert(appointment);
            if (result > 0) {
                return Result.success("预约成功，等待教练确认");
            } else {
                return Result.error("预约失败");
            }
        } catch (Exception e) {
            logger.error("预约失败: {}", e.getMessage(), e);
            return Result.error("预约失败");
        }
    }

    /**
     * 教练确认预约
     */
    @PostMapping("/api/appointment/confirm")
    @ResponseBody
    public Result<String> confirmAppointment(@RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            Long appointmentId = Long.valueOf(requestData.get("id").toString());
            Appointment appointment = appointmentMapper.findById(appointmentId);
            
            if (appointment == null) {
                return Result.error("预约不存在");
            }
            
            if (!appointment.getCoachId().equals(user.getId())) {
                return Result.error("无权操作");
            }
            
            appointmentMapper.updateStatus(appointmentId, "CONFIRMED");
            return Result.success("已确认");
        } catch (Exception e) {
            logger.error("确认失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    /**
     * 教练取消预约
     */
    @PostMapping("/api/appointment/cancel")
    @ResponseBody
    public Result<String> cancelAppointment(@RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            Long appointmentId = Long.valueOf(requestData.get("id").toString());
            Appointment appointment = appointmentMapper.findById(appointmentId);
            
            if (appointment == null) {
                return Result.error("预约不存在");
            }
            
            // 教练或学员都可以取消自己的预约
            if (!appointment.getCoachId().equals(user.getId()) && !appointment.getStudentId().equals(user.getId())) {
                return Result.error("无权操作");
            }
            
            appointmentMapper.updateStatus(appointmentId, "CANCELLED");
            return Result.success("已取消");
        } catch (Exception e) {
            logger.error("取消失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    /**
     * 获取学员的所有预约
     */
    @GetMapping("/api/appointments/student")
    @ResponseBody
    public Result<List<Appointment>> getStudentAppointments(@RequestParam Long coachId, @RequestParam LocalDate date, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            // 获取该教练该日期的所有预约（包括学员自己的）
            List<Appointment> appointments = appointmentMapper.findByCoachIdAndDate(coachId, date);
            return Result.success(appointments);
        } catch (Exception e) {
            logger.error("获取预约失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 获取当前学员的所有预约（用于“我的预约”页面）
     */
    @GetMapping("/api/student/appointments")
    @ResponseBody
    public Result<List<Appointment>> getMyAppointments(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<Appointment> appointments = appointmentMapper.findByStudentId(user.getId());
            logger.info("学员获取我的预约 - 学员ID: {}, 总数: {}", user.getId(), appointments.size());
            return Result.success(appointments);
        } catch (Exception e) {
            logger.error("获取我的预约失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 教练设置时间段约满（手动关闭某个时间段的预约）
     */
    @PostMapping("/api/appointment/slot/close")
    @ResponseBody
    public Result<String> closeTimeSlot(@RequestBody Map<String, Object> requestData, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }
        if (!"COACH".equals(user.getRole())) {
            return Result.error("只有教练可以操作");
        }

        try {
            LocalDate appointmentDate = LocalDate.parse(requestData.get("appointmentDate").toString());
            LocalTime startTime = LocalTime.parse(requestData.get("startTime").toString());
            LocalTime endTime = LocalTime.parse(requestData.get("endTime").toString());
            
            // 创建一条特殊的预约记录，标记为教练不可用
            // 使用 student_id = coach_id 来标识这是教练自己的设置
            Appointment appointment = new Appointment();
            appointment.setCoachId(user.getId());
            appointment.setStudentId(user.getId()); // 特殊标记：教练自己
            appointment.setAppointmentDate(appointmentDate);
            appointment.setStartTime(startTime);
            appointment.setEndTime(endTime);
            appointment.setStatus("CANCELLED"); // 状态为取消，表示不可用
            appointment.setNotes("教练设置不可用");
            
            int result = appointmentMapper.insert(appointment);
            if (result > 0) {
                return Result.success("已设置为约满");
            } else {
                return Result.error("设置失败");
            }
        } catch (Exception e) {
            logger.error("设置失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    /**
     * 删除预约（用于取消不可用设置）
     */
    @PostMapping("/api/appointment/delete")
    @ResponseBody
    public Result<String> deleteAppointment(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            Appointment appointment = appointmentMapper.findById(id);
            
            if (appointment == null) {
                return Result.error("预约不存在");
            }
            
            if (!appointment.getCoachId().equals(user.getId())) {
                return Result.error("无权操作");
            }
            
            appointmentMapper.deleteById(id);
            return Result.success("已删除");
        } catch (Exception e) {
            logger.error("删除失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }

    /**
     * 获取教练的所有预约
     */
    @GetMapping("/api/appointments/coach")
    @ResponseBody
    public Result<List<Appointment>> getCoachAppointments(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<Appointment> appointments = appointmentMapper.findByCoachId(user.getId());
            logger.info("教练获取所有预约 - 教练ID: {}, 总数: {}", user.getId(), appointments.size());
            return Result.success(appointments);
        } catch (Exception e) {
            logger.error("获取预约失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 获取教练今日的所有预约（用于Dashboard统计）
     */
    @GetMapping("/api/appointment/today")
    @ResponseBody
    public Result<List<Appointment>> getTodayAppointments(@RequestParam LocalDate date, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            List<Appointment> appointments = appointmentMapper.findByCoachIdAndDate(user.getId(), date);
            return Result.success(appointments);
        } catch (Exception e) {
            logger.error("获取今日预约失败: {}", e.getMessage(), e);
            return Result.error("获取失败");
        }
    }

    /**
     * 清除过期预约（定时任务调用）
     */
    @PostMapping("/api/appointment/clean-expired")
    @ResponseBody
    public Result<String> cleanExpiredAppointments(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            LocalDate today = LocalDate.now();
            // 删除所有日期在今天之前的预约
            int deletedCount = appointmentMapper.deleteExpiredAppointments(today);
            logger.info("清除过期预约成功 - 教练ID: {}, 删除数量: {}", user.getId(), deletedCount);
            return Result.success("已清除 " + deletedCount + " 条过期预约");
        } catch (Exception e) {
            logger.error("清除过期预约失败: {}", e.getMessage(), e);
            return Result.error("清除失败");
        }
    }
}
