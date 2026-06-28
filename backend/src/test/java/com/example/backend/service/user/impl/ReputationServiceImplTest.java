package com.example.backend.service.user.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.entity.Users;
import com.example.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReputationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReputationServiceImpl reputationService;

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUsername("testuser");
        user.setReputationScore(10);
        user.setLevel(1);
    }

    @Test
    void addPoints_ValidInput_AddsPointsAndSaves() {
        reputationService.addPoints(user, 15, "Test positive contribution");

        assertThat(user.getReputationScore()).isEqualTo(25);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void subtractPoints_ValidInput_SubtractsPointsAndSaves() {
        reputationService.subtractPoints(user, 5, "Test negative behavior");

        assertThat(user.getReputationScore()).isEqualTo(5);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void subtractPoints_ResultingInNegative_ResetsToZero() {
        reputationService.subtractPoints(user, 100, "Severe violation");

        assertThat(user.getReputationScore()).isEqualTo(0);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void checkLevelUp_Thresholds_UpdatesLevelCorrectly() {
        // Test Level 1
        user.setReputationScore(49);
        reputationService.checkLevelUp(user);
        assertThat(user.getLevel()).isEqualTo(1);

        // Test Level 2
        user.setReputationScore(50);
        reputationService.checkLevelUp(user);
        assertThat(user.getLevel()).isEqualTo(2);

        // Test Level 3
        user.setReputationScore(200);
        reputationService.checkLevelUp(user);
        assertThat(user.getLevel()).isEqualTo(3);

        // Test Level 4
        user.setReputationScore(500);
        reputationService.checkLevelUp(user);
        assertThat(user.getLevel()).isEqualTo(4);

        // Test Level 5
        user.setReputationScore(1500);
        reputationService.checkLevelUp(user);
        assertThat(user.getLevel()).isEqualTo(5);

        // Test Level 6
        user.setReputationScore(4000);
        reputationService.checkLevelUp(user);
        assertThat(user.getLevel()).isEqualTo(6);
    }

    @Test
    void addPoints_TriggeringLevelUp_UpdatesLevelAutomatically() {
        user.setReputationScore(40);
        user.setLevel(1);

        reputationService.addPoints(user, 15, "Level up trigger");

        assertThat(user.getReputationScore()).isEqualTo(55);
        assertThat(user.getLevel()).isEqualTo(2);
        verify(userRepository, times(1)).save(user);
    }
}
