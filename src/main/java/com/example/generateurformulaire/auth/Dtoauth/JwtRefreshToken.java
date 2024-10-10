package com.example.generateurformulaire.auth.Dtoauth;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "jwt_refresh_token")
public class JwtRefreshToken {
	
	@Id
    @Column(name = "token_id", nullable = false)
    String tokenId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "create_date", nullable = false)
    LocalDateTime createDate;

    @Column(name = "expiration_date", nullable = false)
    LocalDateTime expirationDate;

}
