package com.example.generateurformulaire.auth.Repositories;

import com.example.generateurformulaire.auth.Dtoauth.JwtRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JwtRefreshTokenRepository extends JpaRepository<JwtRefreshToken, String>
{
}
