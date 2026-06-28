package com.example.backend.config.security;



import com.example.backend.service.JwtService;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        String authHeader = req.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            token = req.getParameter("token");
        }

        if (token != null) {
            try {
                SignedJWT jwt = jwtService.parseToken(token);

                String username = jwt.getJWTClaimsSet().getSubject();


                String userId = jwt.getJWTClaimsSet().getStringClaim("userId");

                List<String> permissions = jwt.getJWTClaimsSet().getStringListClaim("permissions");
                var authorities = (permissions != null) ? permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()) : null;

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                authorities
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                chain.doFilter(req, res);
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
