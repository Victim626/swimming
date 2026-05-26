package com.usbtj.springboot.swimming.controller;

import com.usbtj.springboot.swimming.entity.User;
import com.usbtj.springboot.swimming.entity.CoachProfile;
import com.usbtj.springboot.swimming.entity.StudentProfile;
import com.usbtj.springboot.swimming.mapper.UserMapper;
import com.usbtj.springboot.swimming.mapper.CoachProfileMapper;
import com.usbtj.springboot.swimming.mapper.StudentProfileMapper;
import com.usbtj.springboot.swimming.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserMapper userMapper;
    private final CoachProfileMapper coachProfileMapper;
    private final StudentProfileMapper studentProfileMapper;

    public LoginController(UserMapper userMapper, 
                          CoachProfileMapper coachProfileMapper,
                          StudentProfileMapper studentProfileMapper) {
        this.userMapper = userMapper;
        this.coachProfileMapper = coachProfileMapper;
        this.studentProfileMapper = studentProfileMapper;
    }

    /**
     * 显示登录页面
     */
    @GetMapping("/")
    public String loginPage() {
        return "index";
    }

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "index";
    }

    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    @ResponseBody
    public Result<String> register(@RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam String role,
                                    @RequestParam(required = false) String coachRealName,
                                    @RequestParam(required = false) String coachPhone,
                                    @RequestParam(required = false) String coachEmail,
                                    @RequestParam(required = false) String studentRealName,
                                    @RequestParam(required = false) String studentPhone,
                                    @RequestParam(required = false) String studentEmail,
                                    @RequestParam(required = false) String gender,
                                    @RequestParam(required = false) Integer age) {
        logger.info("用户尝试注册: {}, 角色: {}", username, role);
        
        try {
            String realName = "COACH".equals(role.toUpperCase()) ? coachRealName : studentRealName;
            String phone = "COACH".equals(role.toUpperCase()) ? coachPhone : studentPhone;
            String email = "COACH".equals(role.toUpperCase()) ? coachEmail : studentEmail;
            
            // 验证必填字段
            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (password == null || password.length() < 6) {
                return Result.error("密码长度至少6位");
            }
            if (realName == null || realName.trim().isEmpty()) {
                return Result.error("真实姓名不能为空");
            }
            
            // 检查用户名是否已存在
            User existingUser = userMapper.findByUsername(username.trim());
            if (existingUser != null) {
                return Result.error("用户名已存在");
            }
            
            // 创建用户
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(password); // 实际应该加密
            newUser.setRole(role.toUpperCase());
            newUser.setStatus(1);
            
            int result = userMapper.insert(newUser);
            if (result <= 0) {
                return Result.error("注册失败，请稍后重试");
            }
            
            Long userId = newUser.getId();
            
            // 根据角色创建详细信息
            if ("COACH".equals(role.toUpperCase())) {
                // 插入教练信息
                CoachProfile coachProfile = new CoachProfile();
                coachProfile.setUserId(userId);
                coachProfile.setRealName(realName.trim());
                coachProfile.setPhone(phone);
                coachProfile.setEmail(email);
                coachProfile.setQualificationVerified(2); // 未提交审核
                
                coachProfileMapper.insert(coachProfile);
                logger.info("教练注册成功: {}", username);
            } else {
                // 插入学员信息
                StudentProfile studentProfile = new StudentProfile();
                studentProfile.setUserId(userId);
                studentProfile.setRealName(realName.trim());
                studentProfile.setPhone(phone);
                studentProfile.setEmail(email);
                studentProfile.setGender(gender);
                studentProfile.setAge(age);
                
                studentProfileMapper.insert(studentProfile);
                logger.info("学员注册成功: {}", username);
            }
            
            return Result.success("注册成功");
            
        } catch (Exception e) {
            logger.error("注册过程发生异常: {}", e.getMessage(), e);
            return Result.error("注册失败，请稍后重试");
        }
    }

    /**
     * 处理登录请求
     */
    @PostMapping("/login")
    @ResponseBody
    public Result<User> login(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session) {
        logger.info("用户尝试登录: {}", username);
        
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        
        try {
            // 查询用户
            User user = userMapper.findByUsername(username.trim());
            
            if (user == null) {
                logger.warn("登录失败: 用户名不存在 - {}", username);
                return Result.error("用户名或密码错误");
            }
            
            // 检查用户状态
            if (user.getStatus() != null && user.getStatus() == 0) {
                logger.warn("登录失败: 账户已被禁用 - {}", username);
                return Result.error("账户已被禁用，请联系管理员");
            }
            
            // 验证密码（实际项目中应该使用加密密码）
            if (!user.getPassword().equals(password)) {
                logger.warn("登录失败: 密码错误 - {}", username);
                return Result.error("用户名或密码错误");
            }
            
            // 登录成功，保存用户信息到session
            session.setAttribute("currentUser", user);
            logger.info("用户登录成功: {} (角色: {})", username, user.getRole());
            
            return Result.success(user);
            
        } catch (Exception e) {
            logger.error("登录过程发生异常: {}", e.getMessage(), e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 显示仪表板页面
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            logger.debug("未登录用户尝试访问仪表板，重定向到登录页");
            return "redirect:/";
        }
        
        // 教练需通过资质验证才能访问
        model.addAttribute("user", user);
        return "index";
    }
    
    /**
     * 学员的我的预约页面
     */
    @GetMapping("/my-appointments")
    public String myAppointments(HttpSession session, Model model) {
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
     * 显示聊天页面
     */
    @GetMapping("/chat")
    public String chatPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "index";
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            logger.info("用户退出登录: {}", user.getUsername());
        }
        session.invalidate();
        return "redirect:/";
    }
}
