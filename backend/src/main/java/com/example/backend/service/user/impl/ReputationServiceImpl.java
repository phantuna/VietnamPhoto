package com.example.backend.service.user.impl;

import com.example.backend.entity.Users;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.user.ReputationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReputationServiceImpl implements ReputationService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addPoints(Users user, int points, String reason) {
        int currentScore = user.getReputationScore() != null ? user.getReputationScore() : 0;
        user.setReputationScore(currentScore + points);
        log.info("User {} gained {} points. Reason: {}", user.getUsername(), points, reason);
        checkLevelUp(user);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void subtractPoints(Users user, int points, String reason) {
        int currentScore = user.getReputationScore() != null ? user.getReputationScore() : 0;
        int newScore = Math.max(0, currentScore - points);
        user.setReputationScore(newScore);
        log.info("User {} lost {} points. Reason: {}", user.getUsername(), points, reason);
        checkLevelUp(user);
        userRepository.save(user);
    }

    @Override
    public void checkLevelUp(Users user) {
        int score = user.getReputationScore() != null ? user.getReputationScore() : 0;
        int newLevel = calculateLevel(score);
        
        int currentLevel = user.getLevel() != null ? user.getLevel() : 1;
        if (newLevel != currentLevel) {
            user.setLevel(newLevel);
            log.info("User {} level updated to {}", user.getUsername(), newLevel);
        }
    }

    private int calculateLevel(int score) {
        if (score >= 4000) return 6;
        if (score >= 1500) return 5;
        if (score >= 500) return 4;
        if (score >= 200) return 3;
        if (score >= 50) return 2;
        return 1;
    }
}
