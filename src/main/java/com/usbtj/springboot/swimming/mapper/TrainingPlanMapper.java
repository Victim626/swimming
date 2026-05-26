package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.TrainingPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TrainingPlanMapper {
    
    /**
     * 根据教练ID获取训练计划列表
     */
    List<TrainingPlan> findByCoachId(@Param("coachId") Long coachId);
    
    /**
     * 根据学员ID获取训练计划列表
     */
    List<TrainingPlan> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据教练和学员获取计划列表
     */
    List<TrainingPlan> findByCoachAndStudent(@Param("coachId") Long coachId, @Param("studentId") Long studentId);
    
    /**
     * 根据ID获取计划详情
     */
    TrainingPlan findById(@Param("id") Long id);
    
    /**
     * 插入训练计划
     */
    int insert(TrainingPlan plan);
    
    /**
     * 更新训练计划
     */
    int update(TrainingPlan plan);
    
    /**
     * 删除训练计划
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 获取计划数量（用于验证周期）
     */
    int countByCoachAndStudent(@Param("coachId") Long coachId, @Param("studentId") Long studentId);
    
    /**
     * 获取所有学员用户
     */
    List<com.usbtj.springboot.swimming.entity.User> findAllStudents();
    
    /**
     * 查询学员关联的教练（通过教练-学员关联表）
     */
    com.usbtj.springboot.swimming.entity.User findCoachByStudentId(@Param("studentId") Long studentId);
}
