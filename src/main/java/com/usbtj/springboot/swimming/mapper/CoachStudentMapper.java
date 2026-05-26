package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.CoachStudentRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CoachStudentMapper {
    
    /**
     * 获取教练关联的学员列表
     */
    List<CoachStudentRelation> findByCoachId(@Param("coachId") Long coachId);
    
    /**
     * 获取学员关联的教练
     */
    List<CoachStudentRelation> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 添加教练-学员关联
     */
    int insert(CoachStudentRelation relation);
    
    /**
     * 删除关联
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查关联是否存在
     */
    CoachStudentRelation findByCoachAndStudent(@Param("coachId") Long coachId, @Param("studentId") Long studentId);
    
    /**
     * 更新关联状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
