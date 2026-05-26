package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AppointmentMapper {
    
    /**
     * 根据ID查询预约
     */
    Appointment findById(Long id);
    
    /**
     * 查询教练的所有预约
     */
    List<Appointment> findByCoachId(Long coachId);
    
    /**
     * 查询学员的所有预约
     */
    List<Appointment> findByStudentId(Long studentId);
    
    /**
     * 查询某教练某日期的预约
     */
    List<Appointment> findByCoachIdAndDate(@Param("coachId") Long coachId, 
                                            @Param("date") LocalDate date);
    
    /**
     * 查询某时间段已确认的预约数量
     */
    int countConfirmedByCoachDateAndTime(@Param("coachId") Long coachId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") java.time.LocalTime startTime,
                                          @Param("endTime") java.time.LocalTime endTime);
    
    /**
     * 插入预约
     */
    int insert(Appointment appointment);
    
    /**
     * 更新预约
     */
    int update(Appointment appointment);
    
    /**
     * 更新预约状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 删除预约
     */
    int deleteById(Long id);
    
    /**
     * 清除过期预约（删除日期在今天之前的所有预约）
     */
    int deleteExpiredAppointments(@Param("beforeDate") LocalDate beforeDate);
}
