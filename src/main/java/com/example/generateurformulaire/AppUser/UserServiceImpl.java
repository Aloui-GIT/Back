package com.example.generateurformulaire.AppUser;

import com.example.generateurformulaire.Exceptions.*;
import com.example.generateurformulaire.auth.interfaces.TokenAuthService;
import com.example.generateurformulaire.entities.AdminPermission;
import com.example.generateurformulaire.services.ServiceEmail;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cryptacular.bean.EncodingHashBean;
import org.cryptacular.spec.CodecSpec;
import org.cryptacular.spec.DigestSpec;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

//import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.logging.log4j.util.Strings.EMPTY;


@Slf4j
@Service
public class UserServiceImpl implements UserService
{

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;


	@Autowired

	TokenAuthService tokenAuthService;
	@Autowired
	ServiceEmail serviceEmail ;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder)
	{
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User saveAdmin(User user) throws UsernameNotExist, UsernameExist, EmailExist, MessagingException, io.jsonwebtoken.io.IOException, ParseException , IOException, MessagingException {
		isvalidUsernameAndEmail(EMPTY, user.getUsername(), user.getEmail());
		isValid(user.getPassword());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setLocked(false);
		User savedUser = userRepository.save(user);
		return savedUser;
	}
	@Override

	public User updateUser(Long userId, User updatedUser) {
		User existingUser = userRepository.getUserById(userId);

		// Update user fields
		existingUser.setUsername(updatedUser.getUsername());
		existingUser.setEmail(updatedUser.getEmail());
		existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
		existingUser.setBirthDate(updatedUser.getBirthDate());
		// Handle other fields like profilePicture, etc.
		return userRepository.save(existingUser);
	}
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

	public void isValidUsername(String username) throws InvalidUsernameException {
		if (username == null || username.isEmpty() || !USERNAME_PATTERN.matcher(username).matches()) {
			throw new InvalidUsernameException("Username must not contain spaces or special characters.");
		}}

	@Override
	public User saveUser(User user) throws UsernameNotExist, UsernameExist, EmailExist, MessagingException, IOException, ParseException, InvalidUsernameException {
		// Validate the username
		isValidUsername(user.getUsername());

		// Existing validations
		isvalidUsernameAndEmail(EMPTY, user.getUsername(), user.getEmail());
		isValid(user.getPassword());

		// Generate a verification token
		String verificationToken = generateVerificationToken();
		user.setVerificationToken(verificationToken);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setLocked(true); // Set user as locked until they verify their email

		User savedUser = userRepository.save(user);

		// Send verification email
		serviceEmail.sendVerificationEmail(user.getEmail(), verificationToken);

		return savedUser;
	}
	private String generateVerificationToken() {
		// Generate a unique token
		return UUID.randomUUID().toString();
	}

	public void verifyUser(String token) throws NotFoundException {
		User user = userRepository.findByVerificationToken(token)
				.orElseThrow(() -> new NotFoundException("User not found with token"));

		user.setLocked(false); // Unlock the user after verification
		user.setVerificationToken(null); // Clear the token after verification
		userRepository.save(user);
	}
	@Override
	public void deleteUserById(Long userId) {
		userRepository.deleteById(userId);
	}


	@Override
	public void deleteAdminById(Long userId)   {

		userRepository.deleteById(userId);
	}

	@Override
	public Optional<User> findByUsername(String username)
	{

		return userRepository.findByUsername(username);
	}


	@Override
	public Optional<User> findByEmail(String email)
	{

		return userRepository.findByEmail(email);
	}


	@Override
	public List<User> findAllUsers()
	{

		return userRepository.findAll();
	}

	@Override
	public User getUser(Long userId) {
		User user = userRepository.findById(userId).orElse(null);
		return user;
	}
	@Override
	public void unlockUser(Long userId,Long adminId) {
		User admin = userRepository.findById( adminId)
				.orElseThrow(() -> new EntityNotFoundException("Admin not found"));
		if (!hasPermission(admin, AdminPermission.LOCK_UNLOCK_USER)) {
			throw new AccessDeniedException("Admin does not have permission to unlock users");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		user.setLoginAttempts(0);
		user.setLocked(false);
		userRepository.save(user);
	}
	@Override
	public void lockUser(Long userId ,Long adminId) {
		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new EntityNotFoundException("Admin not found"));
		if (!hasPermission(admin, AdminPermission.LOCK_UNLOCK_USER)) {
			throw new AccessDeniedException("Admin does not have permission to lock users");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		user.setLoginAttempts(0);
		user.setLocked(true);
		userRepository.save(user);
	}

	@Override
	public void grantPermission(Long userId, Long adminId, AdminPermission permission) {
		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new EntityNotFoundException("Admin not found"));
		if (!hasPermission(admin, AdminPermission.GRANT_REVOKE_USER)) {
			throw new AccessDeniedException("Admin does not have permission to grant permissions");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		user.getAdminPermissions().add(permission);
		userRepository.save(user);
	}
	@Override
	public void revokePermission(Long userId, Long adminId, AdminPermission permission) {
		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new EntityNotFoundException("Admin not found"));
		if (!hasPermission(admin, AdminPermission.GRANT_REVOKE_USER)) {
			throw new AccessDeniedException("Admin does not have permission to revoke permissions");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		user.getAdminPermissions().remove(permission);
		userRepository.save(user);
	}
	@Override
	public boolean hasPermission(User user, AdminPermission permission) {
		return user.getAdminPermissions().contains(permission);
	}
	@Override
	public Set<AdminPermission> getAdminPermissions(Long adminId) {
		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new EntityNotFoundException("Admin not found"));
		return admin.getAdminPermissions(); // Access the permissions from the User entity
	}

	private User isvalidUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws UsernameNotExist, UsernameExist, EmailExist {
		User userByNewUsername = findByUsername(newUsername).orElse(null);
		User userByNewEmail = findByEmail(newEmail).orElse(null);
		if(StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findByUsername(currentUsername).orElse(null);
			if (currentUser == null) {
				throw new UsernameNotExist("No user found by username: " + currentUsername);
			}
			if (userByNewUsername != null && !currentUser.getUserId().equals(userByNewUsername.getUserId())) {
				throw new UsernameExist("Username already exists.");
			}
			if (userByNewEmail != null && !currentUser.getUserId().equals(userByNewEmail.getUserId())) {
				throw new EmailExist("Email already exists.");
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExist("Username already exists.");
			}
			if (userByNewEmail != null) {
				throw new EmailExist("Email already exists.");
			}
			return null;
		}
	}
	@Override
	@SneakyThrows
	public boolean isValid(String password) {
		String messageTemplate = null;
		Properties props = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("passay.properties");
		try {
			props.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MessageResolver resolver = new PropertiesMessageResolver(props);
		List<PasswordData.Reference> history = Arrays.asList(
				// Password=P@ssword1
				new PasswordData.HistoricalReference(
						"SHA256",
						"j93vuQDT5ZpZ5L9FxSfeh87zznS3CM8govlLNHU8GRWG/9LjUhtbFp7Jp1Z4yS7t"),

				// Password=P@ssword2
				new PasswordData.HistoricalReference(
						"SHA256",
						"mhR+BHzcQXt2fOUWCy4f903AHA6LzNYKlSOQ7r9np02G/9LjUhtbFp7Jp1Z4yS7t"),

				// Password=P@ssword3
				new PasswordData.HistoricalReference(
						"SHA256",
						"BDr/pEo1eMmJoeP6gRKh6QMmiGAyGcddvfAHH+VJ05iG/9LjUhtbFp7Jp1Z4yS7t")
		);
		EncodingHashBean hasher = new EncodingHashBean(
				new CodecSpec("Base64"), // Handles base64 encoding
				new DigestSpec("SHA256"), // Digest algorithm
				1, // Number of hash rounds
				false); // Salted hash == false

		PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(
				new LengthRule(8, 16),
				new CharacterRule(EnglishCharacterData.UpperCase, 1),
				new CharacterRule(EnglishCharacterData.LowerCase, 1),
				new CharacterRule(EnglishCharacterData.Digit, 1),
				new CharacterRule(EnglishCharacterData.Special, 1),
				new WhitespaceRule(),
				new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),
				new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false)
				,new DigestHistoryRule(hasher)
		));

		RuleResult result = validator.validate(new PasswordData(password));
		PasswordData data = new PasswordData("P@ssword1", password);//"P@ssword1");
		data.setPasswordReferences(history);
		RuleResult result2 = validator.validate(data);

		if (result.isValid() ) {
			return true;
		}
		try {
			if (result.isValid()==false) {
				List<String> messages = validator.getMessages(result);

				messageTemplate = String.join(",", messages);

				System.out.println("Invalid Password: " + validator.getMessages(result));
			}
		} finally
		{
			throw new PasswordValidException(messageTemplate);
		}
	}


	@Override
	@Transactional //Transactional is required when executing an update/delete query.
	public void makeAdmin(String username) {

		userRepository.makeAdmin(username);
		User u = userRepository.findByUsername(username).get();
	}
	@Override
	@Transactional // Transactional is required when executing an update/delete query.
	public void changeRole(Long userId, AppUserRole newRole, String adminUsername) {
		// Find the user by userId
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		// Update the user's role
		user.setRole(newRole);

		// Save the user with the new role
		userRepository.save(user);

		// Optionally, you could log or perform any actions required for the admin performing the change
	}

	@Override
	public List<User> allAdmins(){
		List<User> users = userRepository.findAll();
		List<User> result = new ArrayList<>();
		for (User u : users) {
			if (u.getRole().name().equals("ADMIN")) {
				result.add(u);
			}
			else if(u.getRole().name().equals(("ADMIN_FRANCHISE"))){
				result.add(u);
			}
		}

		return result;
	}

	public User getUserByToken(@NonNull HttpServletRequest request) {
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;
		jwt = authHeader.substring(7);
		userEmail = tokenAuthService.extractUserEmail(jwt);

		return userRepository.findByEmail(userEmail).get();
	}


@Override
public String saveProfilePic(MultipartFile file) throws IOException {
		// Define the upload directory path
		String uploadDirectory = "src/uploads/";

		// Create the directory if it doesn't exist
		Path uploadPath = Paths.get(uploadDirectory);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Define the file path (use a unique identifier, e.g., timestamp, to avoid name conflicts)
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path filePath = uploadPath.resolve(fileName);

		// Save the file to the specified path
		Files.copy(file.getInputStream(), filePath);

		// Return the file name (you can also return the file path if you prefer)
		return fileName;
	}


}


