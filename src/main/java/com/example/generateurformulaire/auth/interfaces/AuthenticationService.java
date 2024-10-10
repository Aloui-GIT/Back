package com.example.generateurformulaire.auth.interfaces;


import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.Exceptions.EmailNotExist;
import com.example.generateurformulaire.Exceptions.ResetPasswordException;
import com.example.generateurformulaire.Exceptions.ResetPasswordTokenException;
import com.example.generateurformulaire.auth.Dtoauth.PasswordResetToken;
import io.jsonwebtoken.io.IOException;
import jakarta.mail.MessagingException;


public interface AuthenticationService
{
    User signInAndReturnJWT(User signInRequest);
	
    PasswordResetToken generatePasswordResetToken(String email) throws EmailNotExist, IOException, java.io.IOException, MessagingException;
	
	void updatePassword(String token, String newPassword) throws ResetPasswordException, ResetPasswordTokenException;
}
