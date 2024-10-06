package com.example.GraduationProject.WebApi.config;

import com.example.GraduationProject.Common.Entities.Token;
import com.example.GraduationProject.Core.Repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenCleanupService {

    @Autowired
    private TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 * * ?") // every day at midnight
    public void cleanupExpiredTokens() {
        List<Token> expiredTokens = tokenRepository.findAllByExpiredTrue();
        for (Token token : expiredTokens) {
            tokenRepository.deleteById(token.getId());
        }
    }
}
