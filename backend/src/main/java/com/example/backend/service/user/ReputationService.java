package com.example.backend.service.user;

import com.example.backend.entity.Users;

public interface ReputationService {
    void addPoints(Users user, int points, String reason);
    void subtractPoints(Users user, int points, String reason);
    void checkLevelUp(Users user);
}
