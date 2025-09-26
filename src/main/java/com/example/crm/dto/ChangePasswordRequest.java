package com.example.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求 DTO
 */
public class ChangePasswordRequest {

    @NotBlank(message = "原密码不能为空")
    public String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "新密码长度至少6个字符")
    public String newPassword;

    public ChangePasswordRequest() {}

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
