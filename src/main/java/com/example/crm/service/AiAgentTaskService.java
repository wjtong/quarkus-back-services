package com.example.crm.service;

import com.example.crm.entity.AiAgentTask;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI Agent 任务管理服务
 * 负责 AI Agent 任务的创建、执行和监控
 */
@ApplicationScoped
public class AiAgentTaskService {

    private static final Logger LOG = Logger.getLogger(AiAgentTaskService.class);

    /**
     * 创建新任务
     */
    @Transactional
    public AiAgentTask createTask(String agentId, String taskTitle, String taskDescription, 
                                String taskType, String inputData, Integer priority) {
        try {
            AiAgentTask task = new AiAgentTask();
            task.taskId = generateTaskId();
            task.agentId = agentId;
            task.taskTitle = taskTitle;
            task.taskDescription = taskDescription;
            task.taskType = taskType;
            task.inputData = inputData;
            task.priority = priority != null ? priority : 5;
            task.status = "PENDING";
            task.assignedDate = LocalDateTime.now();
            task.createdDate = LocalDateTime.now();
            task.createdStamp = LocalDateTime.now();
            task.createdTxStamp = LocalDateTime.now();
            task.lastUpdatedStamp = LocalDateTime.now();
            task.lastUpdatedTxStamp = LocalDateTime.now();

            task.persist();
            LOG.info("创建 AI Agent 任务: " + task.taskId);
            
            return task;
        } catch (Exception e) {
            LOG.error("创建 AI Agent 任务失败", e);
            throw new RuntimeException("创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 开始执行任务
     */
    @Transactional
    public AiAgentTask startTask(String taskId) {
        try {
            AiAgentTask task = AiAgentTask.findById(taskId);
            if (task == null) {
                throw new IllegalArgumentException("任务不存在: " + taskId);
            }

            if (!"PENDING".equals(task.status)) {
                throw new IllegalStateException("任务状态不允许开始执行: " + task.status);
            }

            task.updateStatus("IN_PROGRESS");
            task.persist();
            LOG.info("开始执行任务: " + taskId);
            
            return task;
        } catch (Exception e) {
            LOG.error("开始执行任务失败", e);
            throw new RuntimeException("开始执行任务失败: " + e.getMessage());
        }
    }

    /**
     * 完成任务
     */
    @Transactional
    public AiAgentTask completeTask(String taskId, String outputData, String resultSummary, 
                                  Double confidenceScore, Double qualityRating) {
        try {
            AiAgentTask task = AiAgentTask.findById(taskId);
            if (task == null) {
                throw new IllegalArgumentException("任务不存在: " + taskId);
            }

            if (!"IN_PROGRESS".equals(task.status)) {
                throw new IllegalStateException("任务状态不允许完成: " + task.status);
            }

            task.setResult(outputData, resultSummary, confidenceScore);
            task.qualityRating = qualityRating;
            task.updateStatus("COMPLETED");
            task.persist();
            LOG.info("完成任务: " + taskId);
            
            return task;
        } catch (Exception e) {
            LOG.error("完成任务失败", e);
            throw new RuntimeException("完成任务失败: " + e.getMessage());
        }
    }

    /**
     * 任务失败
     */
    @Transactional
    public AiAgentTask failTask(String taskId, String errorMessage) {
        try {
            AiAgentTask task = AiAgentTask.findById(taskId);
            if (task == null) {
                throw new IllegalArgumentException("任务不存在: " + taskId);
            }

            task.setError(errorMessage);
            task.persist();
            LOG.info("任务失败: " + taskId + ", 错误: " + errorMessage);
            
            return task;
        } catch (Exception e) {
            LOG.error("设置任务失败状态失败", e);
            throw new RuntimeException("设置任务失败状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有任务
     */
    public List<AiAgentTask> getAllTasks() {
        return AiAgentTask.listAll();
    }

    /**
     * 根据 Agent ID 获取任务列表
     */
    public List<AiAgentTask> getTasksByAgentId(String agentId) {
        return AiAgentTask.find("agentId", agentId).list();
    }

    /**
     * 根据状态获取任务列表
     */
    public List<AiAgentTask> getTasksByStatus(String status) {
        return AiAgentTask.find("status", status).list();
    }

    /**
     * 获取待处理的任务列表
     */
    public List<AiAgentTask> getPendingTasks() {
        return getTasksByStatus("PENDING");
    }

    /**
     * 获取进行中的任务列表
     */
    public List<AiAgentTask> getInProgressTasks() {
        return getTasksByStatus("IN_PROGRESS");
    }

    /**
     * 获取已完成的任务列表
     */
    public List<AiAgentTask> getCompletedTasks() {
        return getTasksByStatus("COMPLETED");
    }

    /**
     * 获取失败的任务列表
     */
    public List<AiAgentTask> getFailedTasks() {
        return getTasksByStatus("FAILED");
    }

    /**
     * 获取过期任务列表
     */
    public List<AiAgentTask> getOverdueTasks() {
        List<AiAgentTask> allTasks = AiAgentTask.listAll();
        return allTasks.stream()
            .filter(AiAgentTask::isOverdue)
            .toList();
    }

    /**
     * 更新任务反馈
     */
    @Transactional
    public AiAgentTask updateTaskFeedback(String taskId, String feedback, Double qualityRating) {
        try {
            AiAgentTask task = AiAgentTask.findById(taskId);
            if (task == null) {
                throw new IllegalArgumentException("任务不存在: " + taskId);
            }

            task.feedback = feedback;
            if (qualityRating != null) {
                task.qualityRating = qualityRating;
            }
            task.lastModifiedDate = LocalDateTime.now();
            task.lastUpdatedStamp = LocalDateTime.now();
            task.lastUpdatedTxStamp = LocalDateTime.now();

            task.persist();
            LOG.info("更新任务反馈: " + taskId);
            
            return task;
        } catch (Exception e) {
            LOG.error("更新任务反馈失败", e);
            throw new RuntimeException("更新任务反馈失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务统计信息
     */
    public String getTaskStatistics() {
        try {
            List<AiAgentTask> allTasks = getAllTasks();
            
            long totalTasks = allTasks.size();
            long completedTasks = allTasks.stream().filter(AiAgentTask::isCompleted).count();
            long failedTasks = allTasks.stream().filter(AiAgentTask::isFailed).count();
            long pendingTasks = allTasks.stream().filter(AiAgentTask::isPending).count();
            long inProgressTasks = allTasks.stream().filter(AiAgentTask::isInProgress).count();
            long overdueTasks = allTasks.stream().filter(AiAgentTask::isOverdue).count();

            double successRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
            double failureRate = totalTasks > 0 ? (double) failedTasks / totalTasks * 100 : 0;

            double avgQuality = allTasks.stream()
                .filter(t -> t.qualityRating != null)
                .mapToDouble(t -> t.qualityRating)
                .average()
                .orElse(0.0);

            double avgConfidence = allTasks.stream()
                .filter(t -> t.confidenceScore != null)
                .mapToDouble(t -> t.confidenceScore)
                .average()
                .orElse(0.0);

            StringBuilder stats = new StringBuilder();
            stats.append("AI Agent 任务统计信息:\n");
            stats.append("总任务数: ").append(totalTasks).append("\n");
            stats.append("已完成: ").append(completedTasks).append("\n");
            stats.append("失败: ").append(failedTasks).append("\n");
            stats.append("待处理: ").append(pendingTasks).append("\n");
            stats.append("进行中: ").append(inProgressTasks).append("\n");
            stats.append("已过期: ").append(overdueTasks).append("\n");
            stats.append("成功率: ").append(String.format("%.2f%%", successRate)).append("\n");
            stats.append("失败率: ").append(String.format("%.2f%%", failureRate)).append("\n");
            stats.append("平均质量评分: ").append(String.format("%.2f", avgQuality)).append("\n");
            stats.append("平均置信度: ").append(String.format("%.2f", avgConfidence)).append("\n");

            return stats.toString();
        } catch (Exception e) {
            LOG.error("获取任务统计信息失败", e);
            return "获取统计信息失败: " + e.getMessage();
        }
    }

    /**
     * 生成唯一的任务 ID
     */
    private String generateTaskId() {
        return "TASK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
