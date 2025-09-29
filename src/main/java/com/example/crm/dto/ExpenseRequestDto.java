package com.example.crm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 报销申请请求DTO
 * 用于报销申请的具体数据
 */
public class ExpenseRequestDto {

    @NotBlank(message = "报销类型不能为空")
    public String expenseType;

    @NotNull(message = "报销金额不能为空")
    @DecimalMin(value = "0.01", message = "报销金额必须大于0")
    public BigDecimal amount;

    @NotBlank(message = "报销原因不能为空")
    public String reason;

    public String description;

    public String attachments;

    public String department;

    public String project;

    public ExpenseRequestDto() {
    }

    public ExpenseRequestDto(String expenseType, BigDecimal amount, String reason, String description) {
        this.expenseType = expenseType;
        this.amount = amount;
        this.reason = reason;
        this.description = description;
    }

    /**
     * 转换为JSON字符串用于存储
     */
    public String toJsonString() {
        return String.format("{\"expenseType\":\"%s\",\"amount\":%s,\"reason\":\"%s\",\"description\":\"%s\",\"attachments\":\"%s\",\"department\":\"%s\",\"project\":\"%s\"}",
                expenseType != null ? expenseType : "",
                amount != null ? amount.toString() : "0",
                reason != null ? reason : "",
                description != null ? description : "",
                attachments != null ? attachments : "",
                department != null ? department : "",
                project != null ? project : "");
    }

    @Override
    public String toString() {
        return "ExpenseRequestDto{" +
                "expenseType='" + expenseType + '\'' +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                ", description='" + description + '\'' +
                ", attachments='" + attachments + '\'' +
                ", department='" + department + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}
