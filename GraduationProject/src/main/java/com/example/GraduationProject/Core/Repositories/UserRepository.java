package com.example.GraduationProject.Core.Repositories;


import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmail(String email);
    // make a query to get the user when the user is deleted equal to false
    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.UserID = :UserID")
    Optional<User> findById(@Param("UserID") Long UserID);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isDeleted = false")
    Page<User> findAllByRole(@Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false ")
    Page<User> findAll(Pageable pageable );

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND " +
            "(COALESCE(:search, '') = '' OR u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search% OR u.phone LIKE %:search%) " +
            "AND (:role IS NULL OR u.role = :role)")
    Page<User> findAll(Pageable pageable, @Param("search") String search, @Param("role") Role role);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND " +
            "(:roles IS NULL OR u.role IN :roles)  " )
    List<User> findByRoleIn(@Param("roles") List<Role> roles);
    @Query("SELECT u FROM User u WHERE u.role=:role AND u.isDeleted = false  " )
    List<User> findAllByRole(@Param("role")Role role);

}

