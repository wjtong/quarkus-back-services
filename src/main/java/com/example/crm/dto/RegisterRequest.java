package com.example.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求 DTO
 */
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    public String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6个字符")
    public String password;

    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    public String email;

    @Size(max = 100, message = "姓名长度不能超过100个字符")
    public String fullName;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }
}
