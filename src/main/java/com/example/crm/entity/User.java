package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 * 用于系统认证和授权
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(nullable = false, unique = true, length = 50)
    public String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6个字符")
    @Column(nullable = false)
    public String password;

    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Column(nullable = false, unique = true, length = 100)
    public String email;

    @Size(max = 100, message = "姓名长度不能超过100个字符")
    @Column(length = 100)
    public String fullName;

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;

    @Column(name = "is_locked", nullable = false)
    public Boolean isLocked = false;

    @Column(name = "failed_login_attempts", nullable = false)
    public Integer failedLoginAttempts = 0;

    @Column(name = "last_login_date")
    public LocalDateTime lastLoginDate;

    @Column(name = "password_reset_token")
    public String passwordResetToken;

    @Column(name = "password_reset_expires")
    public LocalDateTime passwordResetExpires;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    public Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 用户角色枚举
     */
    public enum Role {
        ADMIN("管理员"),
        USER("普通用户"),
        MANAGER("经理"),
        SALES("销售"),
        CUSTOMER_SERVICE("客服");

        private final String description;

        Role(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    /**
     * 添加角色
     */
    public void addRole(Role role) {
        roles.add(role);
    }

    /**
     * 移除角色
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }

    /**
     * 检查用户是否可用
     */
    public boolean isEnabled() {
        return isActive && !isLocked;
    }

    /**
     * 增加登录失败次数
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.isLocked = true;
        }
    }

    /**
     * 重置登录失败次数
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.isLocked = false;
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }
}
