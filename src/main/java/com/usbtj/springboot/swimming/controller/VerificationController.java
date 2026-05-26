package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.CoachProfile;
import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.mapper.CoachProfileMapper;
import com.usbtj.springboot.swimming.mapper.UserMapper;
import com.usbtj.springboot.swimming.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class VerificationController {

    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);
    private final CoachProfileMapper coachProfileMapper;
    private final UserMapper userMapper;

    public VerificationController(CoachProfileMapper coachProfileMapper, UserMapper userMapper) {
        this.coachProfileMapper = coachProfileMapper;
        this.userMapper = userMapper;
    }

    /**
     * 显示资质认证页面
     */
    @GetMapping("/coach/verification")
    public String verificationPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "index";
    }

    /**
     * 获取当前用户的认证状态
     */
    @GetMapping("/coach/verification/status")
    @ResponseBody
    public Result<Map<String, Object>> getVerificationStatus(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        try {
            CoachProfile profile = coachProfileMapper.findByUserId(user.getId());
            
            Map<String, Object> data = new HashMap<>();
            if (profile != null) {
                data.put("id", profile.getId());
                data.put("realName", profile.getRealName());
                data.put("idCardNumber", profile.getIdCardNumber());
                data.put("certificateNumber", profile.getCertificateNumber());
                data.put("qualificationVerified", profile.getQualificationVerified());
            } else {
                data.put("qualificationVerified", null);
            }
            
            return Result.success(data);
        } catch (Exception e) {
            logger.error("获取认证状态失败: {}", e.getMessage(), e);
            return Result.error("获取状态失败");
        }
    }

    /**
     * 提交资质认证
     */
    @PostMapping("/coach/verification/submit")
    @ResponseBody
    public Result<String> submitVerification(@RequestParam String realName,
                                              @RequestParam String idCardNumber,
                                              @RequestParam String certificateNumber,
                                              HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }

        if (!"COACH".equals(user.getRole())) {
            return Result.error("非教练用户");
        }

        // 验证必填字段
        if (realName == null || realName.trim().isEmpty()) {
            return Result.error("真实姓名不能为空");
        }
        if (idCardNumber == null || idCardNumber.trim().isEmpty()) {
            return Result.error("身份证号不能为空");
        }
        // 身份证号格式验证
        if (!idCardNumber.trim().matches("^[0-9Xx]{18}$")) {
            return Result.error("身份证号格式不正确");
        }
        if (certificateNumber == null || certificateNumber.trim().isEmpty()) {
            return Result.error("职业资格证书号不能为空");
        }

        try {
            CoachProfile existingProfile = coachProfileMapper.findByUserId(user.getId());
            
            if (existingProfile != null) {
                // 已存在记录，更新
                existingProfile.setRealName(realName.trim());
                existingProfile.setIdCardNumber(idCardNumber.trim());
                existingProfile.setCertificateNumber(certificateNumber.trim());
                existingProfile.setQualificationVerified(0); // 重新设为待审核
                coachProfileMapper.update(existingProfile);
                logger.info("教练更新资质信息: {}", user.getUsername());
            } else {
                // 检查身份证号是否已被使用
                CoachProfile existingIdCard = coachProfileMapper.findByIdCardNumber(idCardNumber.trim());
                if (existingIdCard != null) {
                    return Result.error("该身份证号已被其他教练使用");
                }

                // 检查资格证号是否已被使用
                CoachProfile existingCert = coachProfileMapper.findByCertificateNumber(certificateNumber.trim());
                if (existingCert != null) {
                    return Result.error("该职业资格证书号已被使用");
                }

                // 创建新记录
                CoachProfile coachProfile = new CoachProfile();
                coachProfile.setUserId(user.getId());
                coachProfile.setRealName(realName.trim());
                coachProfile.setIdCardNumber(idCardNumber.trim());
                coachProfile.setCertificateNumber(certificateNumber.trim());
                coachProfile.setQualificationVerified(0); // 待审核
                
                coachProfileMapper.insert(coachProfile);
                logger.info("教练提交资质认证: {}", user.getUsername());
            }
            
            return Result.success("提交成功，等待审核");
        } catch (Exception e) {
            logger.error("提交资质认证失败: {}", e.getMessage(), e);
            return Result.error("提交失败，请稍后重试");
        }
    }

    /**
     * 显示审核管理页面
     */
    @GetMapping("/admin/verification")
    public String adminVerificationPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/";
        }
        // TODO: 添加管理员权限验证
        model.addAttribute("user", user);
        return "index";
    }

    /**
     * 获取所有教练的认证列表
     */
    @GetMapping("/admin/verification/list")
    @ResponseBody
    public Result<List<Map<String, Object>>> getVerificationList() {
        try {
            // 获取所有教练档案
            List<CoachProfile> profiles = coachProfileMapper.findAll();
            
            List<Map<String, Object>> result = profiles.stream().map(profile -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", profile.getId());
                map.put("realName", profile.getRealName());
                map.put("idCardNumber", profile.getIdCardNumber());
                map.put("certificateNumber", profile.getCertificateNumber());
                map.put("qualificationVerified", profile.getQualificationVerified());
                map.put("createdAt", profile.getCreatedAt());
                
                // 获取用户名
                User user = userMapper.findById(profile.getUserId());
                map.put("username", user != null ? user.getUsername() : "-");
                
                return map;
            }).collect(Collectors.toList());
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取认证列表失败: {}", e.getMessage(), e);
            return Result.error("获取列表失败");
        }
    }

    /**
     * 通过审核
     */
    @PostMapping("/admin/verification/approve")
    @ResponseBody
    public Result<String> approveVerification(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        if (id == null) {
            return Result.error("参数错误");
        }

        try {
            int result = coachProfileMapper.updateVerificationStatus(id, 1);
            if (result > 0) {
                logger.info("审核通过，教练ID: {}", id);
                return Result.success("审核通过");
            }
            return Result.error("审核失败");
        } catch (Exception e) {
            logger.error("审核失败: {}", e.getMessage(), e);
            return Result.error("审核失败");
        }
    }

    /**
     * 拒绝审核
     */
    @PostMapping("/admin/verification/reject")
    @ResponseBody
    public Result<String> rejectVerification(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        if (id == null) {
            return Result.error("参数错误");
        }

        try {
            int result = coachProfileMapper.updateVerificationStatus(id, 2);
            if (result > 0) {
                logger.info("审核拒绝，教练ID: {}", id);
                return Result.success("已拒绝");
            }
            return Result.error("操作失败");
        } catch (Exception e) {
            logger.error("拒绝失败: {}", e.getMessage(), e);
            return Result.error("操作失败");
        }
    }
}
