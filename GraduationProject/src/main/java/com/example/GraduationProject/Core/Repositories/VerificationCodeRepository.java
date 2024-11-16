package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.VerificationCode;
import com.example.GraduationProject.Common.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.user = :user AND vc.isUsed = false AND vc.expirationTime > CURRENT_TIMESTAMP")
    Optional<VerificationCode> findByUserAndIsUsedFalse(@Param("user") User user);
}