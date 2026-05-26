package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.CoachProfile;
import com.usbtj.springboot.swimming.entity.StudentProfile;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.CoachProfileMapper;
import com.usbtj.springboot.swimming.mapper.StudentProfileMapper;
import com.usbtj.springboot.swimming.mapper.UserMapper;
import com.usbtj.springboot.swimming.response.Result;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人资料控制器
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CoachProfileMapper coachProfileMapper;
    
    @Autowired
    private StudentProfileMapper studentProfileMapper;
    
    /**
     * 获取当前用户的个人资料
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> getMyProfile(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        
        Map<String, Object> profileData = new HashMap<>();
        
        // 获取用户基本信息
        User user = userMapper.findById(currentUser.getId());
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        profileData.put("id", user.getId());
        profileData.put("username", user.getUsername());
        profileData.put("role", user.getRole());
        profileData.put("status", user.getStatus());
        
        // 根据角色获取详细信息
        if ("COACH".equals(user.getRole())) {
            CoachProfile coachProfile = coachProfileMapper.findByUserId(user.getId());
            if (coachProfile != null) {
                profileData.put("realName", coachProfile.getRealName());
                profileData.put("phone", coachProfile.getPhone());
                profileData.put("email", coachProfile.getEmail());
                profileData.put("avatar", coachProfile.getAvatar());
                profileData.put("certificateNumber", coachProfile.getCertificateNumber());
                profileData.put("idCardNumber", coachProfile.getIdCardNumber());
                profileData.put("specialization", coachProfile.getSpecialization());
                profileData.put("introduction", coachProfile.getIntroduction());
                profileData.put("qualificationVerified", coachProfile.getQualificationVerified());
            }
        } else if ("STUDENT".equals(user.getRole())) {
            StudentProfile studentProfile = studentProfileMapper.findByUserId(user.getId());
            if (studentProfile != null) {
                profileData.put("realName", studentProfile.getRealName());
                profileData.put("phone", studentProfile.getPhone());
                profileData.put("email", studentProfile.getEmail());
                profileData.put("avatar", studentProfile.getAvatar());
                
                // 性别映射：枚举转中文
                String gender = studentProfile.getGender();
                if (gender != null) {
                    switch (gender) {
                        case "MALE": profileData.put("gender", "男"); break;
                        case "FEMALE": profileData.put("gender", "女"); break;
                        case "OTHER": profileData.put("gender", "其他"); break;
                        default: profileData.put("gender", gender);
                    }
                }
                
                profileData.put("age", studentProfile.getAge());
            }
        }
        
        return Result.success(profileData);
    }
    
    /**
     * 更新用户基本信息（用户名、密码）
     */
    @PostMapping("/update-user")
    public Result<String> updateUser(
            @RequestBody Map<String, String> requestBody,
            HttpSession session) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        
        User existingUser = userMapper.findByUsername(username.trim());
        if (existingUser != null && !existingUser.getId().equals(currentUser.getId())) {
            return Result.error("用户名已存在");
        }
        
        currentUser.setUsername(username.trim());
        if (password != null && !password.isEmpty()) {
            if (password.length() < 6) {
                return Result.error("密码长度至少6位");
            }
            currentUser.setPassword(password);
        }
        
        int result = userMapper.update(currentUser);
        if (result <= 0) {
            return Result.error("更新失败");
        }
        
        // 更新session中的用户信息
        session.setAttribute("currentUser", userMapper.findById(currentUser.getId()));
        
        logger.info("用户基本信息更新成功: {}", currentUser.getUsername());
        return Result.success("更新成功");
    }
    
    /**
     * 更新教练个人资料
     */
    @PostMapping("/update-coach")
    public Result<String> updateCoachProfile(
            @RequestBody Map<String, Object> requestBody,
            HttpSession session) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        
        if (!"COACH".equals(currentUser.getRole())) {
            return Result.error("只有教练可以更新教练资料");
        }
        
        CoachProfile coachProfile = coachProfileMapper.findByUserId(currentUser.getId());
        if (coachProfile == null) {
            return Result.error("教练资料不存在");
        }
        
        coachProfile.setRealName((String) requestBody.get("realName"));
        coachProfile.setPhone((String) requestBody.get("phone"));
        coachProfile.setEmail((String) requestBody.get("email"));
        coachProfile.setAvatar((String) requestBody.get("avatar"));
        coachProfile.setSpecialization((String) requestBody.get("specialization"));
        coachProfile.setIntroduction((String) requestBody.get("introduction"));
        
        int result = coachProfileMapper.update(coachProfile);
        if (result <= 0) {
            return Result.error("更新失败");
        }
        
        logger.info("教练资料更新成功: {}", currentUser.getUsername());
        return Result.success("更新成功");
    }
    
    /**
     * 更新学员个人资料
     */
    @PostMapping("/update-student")
    public Result<String> updateStudentProfile(
            @RequestBody Map<String, Object> requestBody,
            HttpSession session) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        
        if (!"STUDENT".equals(currentUser.getRole())) {
            return Result.error("只有学员可以更新学员资料");
        }
        
        StudentProfile studentProfile = studentProfileMapper.findByUserId(currentUser.getId());
        if (studentProfile == null) {
            return Result.error("学员资料不存在");
        }
        
        studentProfile.setRealName((String) requestBody.get("realName"));
        studentProfile.setPhone((String) requestBody.get("phone"));
        studentProfile.setEmail((String) requestBody.get("email"));
        studentProfile.setAvatar((String) requestBody.get("avatar"));
        
        // 性别映射：中文转枚举
        String gender = (String) requestBody.get("gender");
        if (gender != null) {
            switch (gender) {
                case "男": studentProfile.setGender("MALE"); break;
                case "女": studentProfile.setGender("FEMALE"); break;
                default: studentProfile.setGender(gender);
            }
        }
        
        Object ageObj = requestBody.get("age");
        if (ageObj != null) {
            studentProfile.setAge(((Number) ageObj).intValue());
        }
        
        int result = studentProfileMapper.update(studentProfile);
        if (result <= 0) {
            return Result.error("更新失败");
        }
        
        logger.info("学员资料更新成功: {}", currentUser.getUsername());
        return Result.success("更新成功");
    }
}
