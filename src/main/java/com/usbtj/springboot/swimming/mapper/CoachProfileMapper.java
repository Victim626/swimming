package com.usbtj.springboot.swimming.mapper;

import com.usbtj.springboot.swimming.entity.CoachProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CoachProfileMapper {
    
    int insert(CoachProfile coachProfile);
    
    CoachProfile findByUserId(@Param("userId") Long userId);
    
    CoachProfile findByCertificateNumber(@Param("certificateNumber") String certificateNumber);
    
    CoachProfile findByIdCardNumber(@Param("idCardNumber") String idCardNumber);
    
    java.util.List<CoachProfile> findAllPending();
    
    int updateVerificationStatus(@Param("id") Long id, @Param("status") Integer status);
    
    int update(CoachProfile coachProfile);
    
    java.util.List<CoachProfile> findAll();
}
