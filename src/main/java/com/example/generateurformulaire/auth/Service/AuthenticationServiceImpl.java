package com.example.generateurformulaire.auth.Service;


import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserRepository;
import com.example.generateurformulaire.Exceptions.EmailNotExist;
import com.example.generateurformulaire.Exceptions.PasswordValidException;
import com.example.generateurformulaire.Exceptions.ResetPasswordException;
import com.example.generateurformulaire.Exceptions.ResetPasswordTokenException;
import com.example.generateurformulaire.auth.Repositories.PasswordResetTokenRepository;
import com.example.generateurformulaire.auth.interfaces.AuthenticationService;
import com.example.generateurformulaire.auth.Dtoauth.PasswordResetToken;
import com.example.generateurformulaire.auth.interfaces.JwtRefreshTokenService;
import com.example.generateurformulaire.security.UserPrincipal;
import com.example.generateurformulaire.security.jwt.JwtProvider;
import com.example.generateurformulaire.services.ServiceEmail;
import jakarta.mail.MessagingException;
import lombok.SneakyThrows;

import net.bytebuddy.utility.RandomString;
import org.cryptacular.bean.EncodingHashBean;
import org.cryptacular.spec.CodecSpec;
import org.cryptacular.spec.DigestSpec;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


@Service
public class AuthenticationServiceImpl implements AuthenticationService
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtRefreshTokenService jwtRefreshTokenService;

	@Autowired
	private ServiceEmail serviceEmail;

    @Override
    public User signInAndReturnJWT(User signInRequest)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtProvider.generateToken(userPrincipal);

        User signInUser = userPrincipal.getUser();
        signInUser.setAccessToken(jwt);
        signInUser.setRefreshToken(jwtRefreshTokenService.createRefreshToken(signInUser.getUserId()).getTokenId());

        return signInUser;
    }


	@Override
	public PasswordResetToken generatePasswordResetToken(String email) throws EmailNotExist, IOException, MessagingException {
		User user = userRepository.findByEmail(email).orElse(null);
		if (user != null) {
			PasswordResetToken token = new PasswordResetToken();
			LocalDateTime nowDate = LocalDateTime.now();
			token.setCreateDate(nowDate);
			String tokenValue = RandomString.make(45);
			token.setUserId(user.getUserId());
			token.setToken(tokenValue);
			token.setExprirationDate(nowDate.plusMinutes(15));

			passwordResetTokenRepository.save(token);

			// Send email with the reset link
			serviceEmail.sendResetPasswordEmail(user.getUsername(), tokenValue, email);

			return token;
		} else {
			throw new EmailNotExist("Could not find any user related to the email: " + email);
		}
	}



	@Override
	public void updatePassword(String token, String newPassword) throws ResetPasswordException, ResetPasswordTokenException {
		try {
			// Fetch the token from the repository
			PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

			// Check if the token exists
			if (resetToken == null) {
				System.out.println("Token not found in the database.");
				throw new ResetPasswordTokenException("Invalid or expired token.");
			}

			// Check if the token has expired
			if (resetToken.getExprirationDate().isBefore(LocalDateTime.now())) {
				System.out.println("Token has expired.");
				throw new ResetPasswordTokenException("Reset password request has expired.");
			}

			// Token is valid; find the associated user
			User user = userRepository.findById(resetToken.getUserId()).orElseThrow(() ->
					new ResetPasswordException("User not found for the provided token.")
			);

			// Validate the new password (ensure this method validates the password format/requirements)
			isValid(newPassword);

			// Optional: Check if the new password is the same as the old password
			if (passwordEncoder.matches(newPassword, user.getPassword())) {
				throw new ResetPasswordException("New password cannot be the same as the old password.");
			}

			// Encode the new password
			String encodedPassword = passwordEncoder.encode(newPassword);

			// Log both the old and new encoded passwords (avoid logging the plain text)
			System.out.println("Old Encoded Password: " + user.getPassword());
			System.out.println("New Encoded Password: " + encodedPassword);

			// Update the user's password
			user.setPassword(encodedPassword);

			// Save the updated user
			userRepository.save(user);

			// Verify if the password was saved correctly
			User updatedUser = userRepository.findById(user.getUserId()).orElseThrow(() ->
					new ResetPasswordException("Failed to retrieve updated user.")
			);

			// Log the verification
			if (passwordEncoder.matches(newPassword, updatedUser.getPassword())) {
				System.out.println("Password update verification successful.");
			} else {
				System.out.println("Password update verification failed.");
				throw new ResetPasswordException("The password update was unsuccessful.");
			}

			// Delete the token after successful password reset
			passwordResetTokenRepository.delete(resetToken);

			System.out.println("Password successfully updated for user ID: " + user.getUserId());
		} catch (ResetPasswordTokenException | ResetPasswordException e) {
			// Log specific reset password exceptions
			System.err.println("Password reset error: " + e.getMessage());
			throw e; // Rethrow to maintain control over exception handling
		} catch (Exception e) {
			// Log any unexpected exceptions
			System.err.println("An error occurred while updating the password: " + e.getMessage());
			throw new ResetPasswordException("An error occurred while updating the password. Please try again.");
		}
	}



	@SneakyThrows
	public boolean isValid(String password) {
		Properties props = new Properties();
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("passay.properties")) {
			if (inputStream == null) {
				throw new IOException("Property file 'passay.properties' not found in the classpath");
			}
			props.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error loading password validation properties", e);
		}

		MessageResolver resolver = new PropertiesMessageResolver(props);

		List<PasswordData.Reference> history = Arrays.asList(
				new PasswordData.HistoricalReference("SHA256", "j93vuQDT5ZpZ5L9FxSfeh87zznS3CM8govlLNHU8GRWG/9LjUhtbFp7Jp1Z4yS7t"),
				new PasswordData.HistoricalReference("SHA256", "mhR+BHzcQXt2fOUWCy4f903AHA6LzNYKlSOQ7r9np02G/9LjUhtbFp7Jp1Z4yS7t"),
				new PasswordData.HistoricalReference("SHA256", "BDr/pEo1eMmJoeP6gRKh6QMmiGAyGcddvfAHH+VJ05iG/9LjUhtbFp7Jp1Z4yS7t")
		);

		EncodingHashBean hasher = new EncodingHashBean(
				new CodecSpec("Base64"),
				new DigestSpec("SHA256"),
				1,
				false
		);

		// Update the PasswordValidator with only uppercase, digit, and special character rules
		PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(
				// Require at least one uppercase character
				new CharacterRule(EnglishCharacterData.UpperCase, 1),
				// Require at least one digit
				new CharacterRule(EnglishCharacterData.Digit, 1),
				// Require at least one special character
				new CharacterRule(EnglishCharacterData.Special, 1),
				// No whitespace allowed
				new WhitespaceRule(),
				// No alphabetical sequences like 'abc'
				new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),
				// No numerical sequences like '123'
				new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),
				// Password history check
				new DigestHistoryRule(hasher)
		));

		PasswordData passwordData = new PasswordData(password);
		passwordData.setPasswordReferences(history);
		RuleResult result = validator.validate(passwordData);

		if (result.isValid()) {
			return true;
		} else {
			List<String> messages = validator.getMessages(result);
			String messageTemplate = String.join(", ", messages);
			System.out.println("Invalid Password: " + messageTemplate);
			throw new PasswordValidException(messageTemplate);
		}
	}

}
