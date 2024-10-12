package com.example.generateurformulaire.auth.Service;


import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserRepository;
import com.example.generateurformulaire.Utils.SecurityUtils;
import com.example.generateurformulaire.auth.Dtoauth.JwtRefreshToken;
import com.example.generateurformulaire.auth.Repositories.JwtRefreshTokenRepository;
import com.example.generateurformulaire.auth.interfaces.JwtRefreshTokenService;
import com.example.generateurformulaire.security.UserPrincipal;
import com.example.generateurformulaire.security.jwt.JwtProvider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class JwtRefreshTokenServiceImpl implements JwtRefreshTokenService
{
    @Value("${app.jwt.refresh-expiration-in-ms}")
    Long REFRESH_EXPIRATION_IN_MS;

    @Autowired
    JwtRefreshTokenRepository jwtRefreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Override
    public JwtRefreshToken createRefreshToken(Long userId)
    {
        JwtRefreshToken jwtRefreshToken = new JwtRefreshToken();

        jwtRefreshToken.setTokenId(UUID.randomUUID().toString());
        jwtRefreshToken.setUserId(userId);
        jwtRefreshToken.setCreateDate(LocalDateTime.now());
        jwtRefreshToken.setExpirationDate(LocalDateTime.now().plus(REFRESH_EXPIRATION_IN_MS, ChronoUnit.MILLIS));

        return jwtRefreshTokenRepository.save(jwtRefreshToken);
    }

    @Override
    public User generateAccessTokenFromRefreshToken(String refreshTokenId)
    {
        JwtRefreshToken jwtRefreshToken = jwtRefreshTokenRepository.findById(refreshTokenId).orElseThrow(null);

        if (jwtRefreshToken.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            throw new RuntimeException("JWT refresh token is not valid.");
        }

        User user = userRepository.findById(jwtRefreshToken.getUserId()).orElseThrow(null);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Set.of(SecurityUtils.convertToAuthority(user.getRole().name())))
                .build();

        String accessToken = jwtProvider.generateToken(userPrincipal);

        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshTokenId);

        return user;
    }
}