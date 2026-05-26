package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentProfileMapper {
    
    int insert(StudentProfile studentProfile);
    
    StudentProfile findByUserId(@Param("userId") Long userId);
    
    int update(StudentProfile studentProfile);
}
