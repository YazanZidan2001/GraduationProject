package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.UserID = u.UserID\s
      where u.UserID = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Long id);

    Optional<Token> findByToken(String token);

    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.UserID = u.UserID\s
      where u.UserID = :id and t.token = :token and (t.expired = false)\s
      """)
    Optional<Token> findValidTokenByUserAndToken(Long id, String token);

    @Query("SELECT t FROM Token t WHERE t.expired = true")
    List<Token> findAllByExpiredTrue();

    @Modifying
    @Transactional
    @Query("delete from Token t where t.id = :id")
    void deleteById(@Param("id") Long id);

}
