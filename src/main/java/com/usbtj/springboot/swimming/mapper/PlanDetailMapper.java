package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.PlanDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlanDetailMapper {
    
    /**
     * 根据计划ID获取所有天的详情
     */
    List<PlanDetail> findByPlanId(@Param("planId") Long planId);
    
    /**
     * 根据学员ID获取所有训练详情
     */
    List<PlanDetail> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据ID获取详情
     */
    PlanDetail findById(@Param("id") Long id);
    
    /**
     * 插入计划详情
     */
    int insert(PlanDetail detail);
    
    /**
     * 更新计划详情
     */
    int update(PlanDetail detail);
    
    /**
     * 删除计划详情
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据计划ID删除所有详情
     */
    int deleteByPlanId(@Param("planId") Long planId);
    
    /**
     * 打卡/取消打卡
     */
    int updateCheckStatus(@Param("id") Long id, @Param("isChecked") Boolean isChecked);
}
