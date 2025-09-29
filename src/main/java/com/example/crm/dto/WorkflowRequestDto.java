package com.example.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 工作流请求DTO
 * 用于创建工作流实例的请求数据
 */
public class WorkflowRequestDto {

    @NotBlank(message = "工作流类型ID不能为空")
    public String workflowTypeId;

    @NotBlank(message = "发起人ID不能为空")
    public String initiatorId;

    @NotBlank(message = "标题不能为空")
    public String title;

    public String description;

    public String requestData;

    public WorkflowRequestDto() {
    }

    public WorkflowRequestDto(String workflowTypeId, String initiatorId, String title, String description, String requestData) {
        this.workflowTypeId = workflowTypeId;
        this.initiatorId = initiatorId;
        this.title = title;
        this.description = description;
        this.requestData = requestData;
    }

    @Override
    public String toString() {
        return "WorkflowRequestDto{" +
                "workflowTypeId='" + workflowTypeId + '\'' +
                ", initiatorId='" + initiatorId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", requestData='" + requestData + '\'' +
                '}';
    }
}
