-- ============================================
-- 教练-学员关联表
-- ============================================
CREATE TABLE `coach_student_relations` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    `coach_id` BIGINT NOT NULL COMMENT '教练ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `relationship_type` VARCHAR(50) COMMENT '关系类型（如：正式学员、试训学员）',
    `start_date` DATE COMMENT '开始关联日期',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-已结束，1-进行中',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_coach_student` (`coach_id`, `student_id`),
    INDEX `idx_coach_id` (`coach_id`),
    INDEX `idx_student_id` (`student_id`),
    FOREIGN KEY (`coach_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`student_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练-学员关联表';
