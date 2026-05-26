package com.usbtj.springboot.swimming.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrainingPlan {
    private Long id;
    private Long coachId;
    private Long studentId;
    private String planName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // DRAFT, ACTIVE, COMPLETED, CANCELLED
    private String aiSuggestions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造函数
    public TrainingPlan() {
    }

    public TrainingPlan(Long coachId, Long studentId, String planName) {
        this.coachId = coachId;
        this.studentId = studentId;
        this.planName = planName;
        this.status = "DRAFT";
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAiSuggestions() {
        return aiSuggestions;
    }

    public void setAiSuggestions(String aiSuggestions) {
        this.aiSuggestions = aiSuggestions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TrainingPlan{" +
                "id=" + id +
                ", coachId=" + coachId +
                ", studentId=" + studentId +
                ", planName='" + planName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
