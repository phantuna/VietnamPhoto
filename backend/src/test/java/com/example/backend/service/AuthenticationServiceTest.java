package com.example.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.backend.dto.request.user.AuthenticationRequest;
import com.example.backend.dto.response.user.AuthenticationResponse;
import com.example.backend.entity.RefreshToken;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Users activeUser;
    private Users bannedUser;
    private RefreshToken validRefreshToken;

    @BeforeEach
    void setUp() {
        activeUser = new Users();
        activeUser.setId("user-01");
        activeUser.setUsername("activeuser");
        activeUser.setEmail("active@example.com");
        activeUser.setPassword("encoded_pass");
        activeUser.setDeleted(0);
        activeUser.setRoles(Collections.emptyList());

        bannedUser = new Users();
        bannedUser.setId("user-02");
        bannedUser.setUsername("banneduser");
        bannedUser.setEmail("banned@example.com");
        bannedUser.setPassword("encoded_pass");
        bannedUser.setDeleted(1);

        validRefreshToken = new RefreshToken();
        validRefreshToken.setToken("refresh_token_01");
        validRefreshToken.setUser(activeUser);
        validRefreshToken.setExpiryDate(java.time.Instant.now().plusSeconds(86400));
    }

    @Test
    void authenticate_ValidCredentials_ReturnsResponse() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("active@example.com");
        request.setPassword("plain_pass");
        when(userRepository.findByEmailIncludeBanned("active@example.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("plain_pass", "encoded_pass")).thenReturn(true);
        when(jwtService.generateToken("activeuser", "user-01", Collections.singletonList("ROLE_USER"))).thenReturn("jwt_token_abc");
        when(refreshTokenService.createRefreshToken("user-01")).thenReturn(validRefreshToken);

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertThat(response).isNotNull();
        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.getToken()).isEqualTo("jwt_token_abc");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token_01");
    }

    @Test
    void authenticate_EmailNotFound_ThrowsAppException() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("plain_pass");
        when(userRepository.findByEmailIncludeBanned("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    void authenticate_UserBanned_ThrowsAppException() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("banned@example.com");
        request.setPassword("plain_pass");
        when(userRepository.findByEmailIncludeBanned("banned@example.com")).thenReturn(Optional.of(bannedUser));

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_BANNED.name());
    }

    @Test
    void authenticate_InvalidPassword_ThrowsAppException() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("active@example.com");
        request.setPassword("wrong_pass");
        when(userRepository.findByEmailIncludeBanned("active@example.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrong_pass", "encoded_pass")).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_PASSWORD.name());
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewResponse() {
        when(refreshTokenService.findByToken("refresh_token_01")).thenReturn(Optional.of(validRefreshToken));
        when(jwtService.generateToken("activeuser", "user-01", Collections.singletonList("ROLE_USER"))).thenReturn("new_jwt_token");

        AuthenticationResponse response = authenticationService.refreshToken("refresh_token_01");

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new_jwt_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token_01");
    }

    @Test
    void refreshToken_TokenNotFound_ThrowsAppException() {
        when(refreshTokenService.findByToken("invalid_token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.refreshToken("invalid_token"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_TOKEN.name());
    }

    @Test
    void forgotPassword_ValidEmail_SendsEmailAsync() {
        when(userRepository.findByEmailIncludeBanned("active@example.com")).thenReturn(Optional.of(activeUser));

        // Không throw Exception
        authenticationService.forgotPassword("active@example.com");
    }

    @Test
    void forgotPassword_EmailNotFound_ThrowsAppException() {
        when(userRepository.findByEmailIncludeBanned("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.forgotPassword("notfound@example.com"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    void resetPassword_EmailNotFound_ThrowsAppException() {
        when(userRepository.findByEmailIncludeBanned("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.resetPassword("notfound@example.com", "123456", "new_pass"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    void resetPassword_InvalidOtp_ThrowsAppException() {
        when(userRepository.findByEmailIncludeBanned("active@example.com")).thenReturn(Optional.of(activeUser));

        // OTP cache rỗng hoặc OTP không khớp
        assertThatThrownBy(() -> authenticationService.resetPassword("active@example.com", "123456", "new_pass"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_OTP.name());
    }
}
