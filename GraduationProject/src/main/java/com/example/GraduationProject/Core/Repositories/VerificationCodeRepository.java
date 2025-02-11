package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.VerificationCode;
import com.example.GraduationProject.Common.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.user = :user AND vc.isUsed = false AND vc.expirationTime > CURRENT_TIMESTAMP")
    Optional<VerificationCode> findByUserAndIsUsedFalse(@Param("user") User user);


    @Query("SELECT COUNT(vc) FROM VerificationCode vc " +
            "WHERE vc.user = :user AND vc.expirationTime > :boundaryTime")
    long countRecentRequests(@Param("user") User user, @Param("boundaryTime") LocalDateTime boundaryTime);

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.user = :user AND vc.isUsed = false")
    List<VerificationCode> findAllByUserAndIsUsedFalse(@Param("user") User user);


}