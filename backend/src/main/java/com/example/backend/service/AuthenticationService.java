package com.example.backend.service;

import com.example.backend.dto.request.user.AuthenticationRequest;
import com.example.backend.dto.response.user.AuthenticationResponse;
import com.example.backend.entity.RefreshToken;
import com.example.backend.entity.Users;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ConcurrentHashMap<String, OtpRecord> otpCache = new ConcurrentHashMap<>();

    private static class OtpRecord {
        String otp;
        LocalDateTime expiryTime;
        
        OtpRecord(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        Users user = userRepository.findByEmailIncludeBanned(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeleted() != null && user.getDeleted() == 1) {
            throw new AppException(ErrorCode.USER_BANNED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        java.util.List<String> permissions = new java.util.ArrayList<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                permissions.add("ROLE_" + role.getId().toUpperCase());
            });
        }
        if (permissions.isEmpty()) {
            permissions.add("ROLE_USER");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getId(), permissions);

        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }
    public AuthenticationResponse refreshToken(String requestRefreshToken) {
        RefreshToken rt = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        refreshTokenService.verifyExpiration(rt);

        Users user = rt.getUser();
        java.util.List<String> permissions = new java.util.ArrayList<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                permissions.add("ROLE_" + role.getId().toUpperCase());
            });
        }
        if (permissions.isEmpty()) {
            permissions.add("ROLE_USER");
        }
        String token = jwtService.generateToken(user.getUsername(), user.getId(), permissions);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .refreshToken(requestRefreshToken)
                .build();
    }

    public void forgotPassword(String email) {
        Users user = userRepository.findByEmailIncludeBanned(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeleted() != null && user.getDeleted() == 1) {
            throw new AppException(ErrorCode.USER_BANNED);
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Save to memory cache (Valid for 5 minutes)
        otpCache.put(email, new OtpRecord(otp, LocalDateTime.now().plusMinutes(5)));

        // Send Email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Mã xác nhận khôi phục mật khẩu - Vietnam Photo Scout");
        message.setText("Chào " + user.getUsername() + ",\n\n"
                + "Mã OTP để khôi phục mật khẩu của bạn là: " + otp + "\n"
                + "Mã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\n"
                + "Trân trọng,\nĐội ngũ Vietnam Photo Scout");
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                mailSender.send(message);
            } catch (Exception e) {
                log.error("Lỗi khi gửi email khôi phục mật khẩu: ", e);
            }
        });
    }

    public void resetPassword(String email, String otp, String newPassword) {
        Users user = userRepository.findByEmailIncludeBanned(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        OtpRecord record = otpCache.get(email);

        if (record == null || !record.otp.equals(otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (record.expiryTime.isBefore(LocalDateTime.now())) {
            otpCache.remove(email);
            throw new AppException(ErrorCode.EXPIRED_TOKEN);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpCache.remove(email);
    }
}