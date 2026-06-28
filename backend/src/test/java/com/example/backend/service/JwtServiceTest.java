package com.example.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.nimbusds.jwt.SignedJWT;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    // Phải là key có độ dài >= 32 byte (256-bit) cho HS256
    private static final String SIGNER_KEY = "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "SIGNER_KEY", SIGNER_KEY);
    }

    @Test
    void generateToken_ValidInput_ReturnsToken() {
        String token = jwtService.generateToken("testuser", "user-123", List.of("ROLE_USER"));
        assertThat(token).isNotNull();
    }

    @Test
    void parseToken_ValidToken_ReturnsSignedJWT() throws Exception {
        String token = jwtService.generateToken("testuser", "user-123", List.of("ROLE_USER"));
        SignedJWT signedJWT = jwtService.parseToken(token);
        
        assertThat(signedJWT).isNotNull();
        assertThat(signedJWT.getJWTClaimsSet().getSubject()).isEqualTo("testuser");
        assertThat(signedJWT.getJWTClaimsSet().getStringClaim("userId")).isEqualTo("user-123");
    }

    @Test
    void parseToken_InvalidSignature_ThrowsAppException() {
        JwtService anotherService = new JwtService();
        ReflectionTestUtils.setField(anotherService, "SIGNER_KEY", "differentkey12345678901234567890");
        String badToken = anotherService.generateToken("testuser", "user-123", List.of("ROLE_USER"));

        assertThatThrownBy(() -> jwtService.parseToken(badToken))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_SIGNATURE.name());
    }

    @Test
    void parseToken_MalformedToken_ThrowsAppException() {
        assertThatThrownBy(() -> jwtService.parseToken("malformed-token"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_TOKEN.name());
    }
}
