package com.example.crm.service;

import com.example.crm.config.JwtConfig;
import com.example.crm.entity.User;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证服务类
 * 处理用户认证、授权和 JWT Token 管理
 */
@ApplicationScoped
public class AuthService {

    @Inject
    JwtConfig jwtConfig;

    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password) {
        Optional<User> userOpt = User.find("username", username).firstResultOptional();
        
        if (userOpt.isEmpty()) {
            throw new SecurityException("用户名或密码错误");
        }

        User user = userOpt.get();
        
        // 检查用户是否被锁定
        if (user.isLocked) {
            throw new SecurityException("账户已被锁定，请联系管理员");
        }

        // 检查用户是否激活
        if (!user.isActive) {
            throw new SecurityException("账户未激活，请联系管理员");
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.password)) {
            user.incrementFailedLoginAttempts();
            user.persist();
            throw new SecurityException("用户名或密码错误");
        }

        // 登录成功，重置失败次数并更新最后登录时间
        user.resetFailedLoginAttempts();
        user.updateLastLoginDate();
        user.persist();

        // 生成 JWT Token
        Set<String> roles = user.roles.stream()
                .map(role -> role.name())
                .collect(Collectors.toSet());

        String accessToken = jwtConfig.generateToken(username, roles);
        String refreshToken = jwtConfig.generateRefreshToken(username);

        Map<String, Object> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);
        result.put("token_type", "Bearer");
        result.put("expires_in", jwtConfig.getExpirationTime());
        result.put("user", Map.of(
                "id", user.id,
                "username", user.username,
                "email", user.email,
                "fullName", user.fullName != null ? user.fullName : "",
                "roles", roles
        ));

        return result;
    }

    /**
     * 用户注册
     */
    @Transactional
    public User register(@Valid User user) {
        // 检查用户名是否已存在
        if (User.find("username", user.username).firstResultOptional().isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (User.find("email", user.email).firstResultOptional().isPresent()) {
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 加密密码
        user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());

        // 设置默认角色
        if (user.roles.isEmpty()) {
            user.addRole(User.Role.USER);
        }

        user.persist();
        return user;
    }

    /**
     * 根据用户名获取用户
     */
    public Optional<User> getUserByUsername(String username) {
        return User.find("username", username).firstResultOptional();
    }

    /**
     * 根据邮箱获取用户
     */
    public Optional<User> getUserByEmail(String email) {
        return User.find("email", email).firstResultOptional();
    }

    /**
     * 根据ID获取用户
     */
    public Optional<User> getUserById(Long id) {
        return User.findByIdOptional(id);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, @Valid User userData) {
        User user = User.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 更新允许修改的字段
        if (userData.fullName != null) {
            user.fullName = userData.fullName;
        }
        if (userData.email != null && !userData.email.equals(user.email)) {
            // 检查新邮箱是否已被其他用户使用
            if (getUserByEmail(userData.email).filter(u -> !u.id.equals(id)).isPresent()) {
                throw new IllegalArgumentException("邮箱已被其他用户使用");
            }
            user.email = userData.email;
        }

        return user;
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = User.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证旧密码
        if (!BCrypt.checkpw(oldPassword, user.password)) {
            throw new SecurityException("原密码错误");
        }

        // 设置新密码
        user.password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.persist();
    }

    /**
     * 重置密码
     */
    @Transactional
    public void resetPassword(String email) {
        Optional<User> userOpt = getUserByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("邮箱不存在");
        }

        User user = userOpt.get();
        
        // 生成重置令牌（这里简化处理，实际应该发送邮件）
        String resetToken = java.util.UUID.randomUUID().toString();
        user.passwordResetToken = resetToken;
        user.passwordResetExpires = LocalDateTime.now().plusHours(1); // 1小时有效期
        
        user.persist();
        
        // 这里应该发送包含重置链接的邮件
        // 为了演示，我们直接返回令牌（实际应用中不应该这样做）
        System.out.println("密码重置令牌: " + resetToken);
    }

    /**
     * 使用重置令牌设置新密码
     */
    @Transactional
    public void setNewPasswordWithToken(String resetToken, String newPassword) {
        Optional<User> userOpt = User.find("passwordResetToken", resetToken).firstResultOptional();
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("无效的重置令牌");
        }

        User user = userOpt.get();
        
        // 检查令牌是否过期
        if (user.passwordResetExpires == null || user.passwordResetExpires.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("重置令牌已过期");
        }

        // 设置新密码并清除重置令牌
        user.password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.passwordResetToken = null;
        user.passwordResetExpires = null;
        user.resetFailedLoginAttempts(); // 重置失败次数
        
        user.persist();
    }

    /**
     * 获取所有用户
     */
    public java.util.List<User> getAllUsers() {
        return User.listAll();
    }

    /**
     * 删除用户
     */
    @Transactional
    public boolean deleteUser(Long id) {
        User user = User.findById(id);
        if (user == null) {
            return false;
        }
        user.delete();
        return true;
    }

    /**
     * 激活/停用用户
     */
    @Transactional
    public void toggleUserStatus(Long id) {
        User user = User.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.isActive = !user.isActive;
        user.persist();
    }

    /**
     * 解锁用户
     */
    @Transactional
    public void unlockUser(Long id) {
        User user = User.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.resetFailedLoginAttempts();
        user.persist();
    }
}
