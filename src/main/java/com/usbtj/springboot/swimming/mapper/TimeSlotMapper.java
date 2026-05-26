package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.TimeSlot;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TimeSlotMapper {
    
    /**
     * 查询所有时间段
     */
    List<TimeSlot> findAll();
    
    /**
     * 根据ID查询
     */
    TimeSlot findById(Long id);
    
    /**
     * 插入时间段
     */
    int insert(TimeSlot timeSlot);
    
    /**
     * 更新时间段
     */
    int update(TimeSlot timeSlot);
    
    /**
     * 删除时间段
     */
    int deleteById(Long id);
}
