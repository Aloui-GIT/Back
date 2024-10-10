package com.example.generateurformulaire.auth.interfaces;




import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.auth.Dtoauth.JwtRefreshToken;


public interface JwtRefreshTokenService
{
    JwtRefreshToken createRefreshToken(Long userId);

    User generateAccessTokenFromRefreshToken(String refreshTokenId);
}
