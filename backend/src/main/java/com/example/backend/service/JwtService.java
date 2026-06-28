package com.example.backend.service;


import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;


import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.signerKey}")
    public  String SIGNER_KEY ;

    public String generateToken(String username, String userId) {
        return generateToken(username, userId, List.of());
    }

    public String generateToken(String username, String userId, List<String> permissions) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issuer("devteria.com")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)))
                    .claim("permissions", permissions)
                    .claim("userId", userId)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claims);

            JWSSigner signer = new MACSigner(SIGNER_KEY.getBytes());
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (Exception e) {
            throw new AppException(ErrorCode.JWT_NOT_CREATED);
        }
    }

    public SignedJWT parseToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

            if (!signedJWT.verify(verifier)) {
                throw new AppException(ErrorCode.INVALID_SIGNATURE);
            }

            return signedJWT;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public static class UserService {
    }
}