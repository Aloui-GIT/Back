package com.example.generateurformulaire.AppUser;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("api/user")
public class UserController
{

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;



    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        User user = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getUser/{userId}")
    public User getUser(@PathVariable(value="userId") Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/all")
    public List<User> findAllUsers()
    {
        return userService.findAllUsers();
    }

    @GetMapping("/current")
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username);
    }
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.ok("User with ID " + userId + " was deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting user with ID " + userId + ": " + e.getMessage());
        }
    }

    private static final String UPLOAD_DIRECTORY = "uploads/"; // Adjust this if necessary

    @PostMapping("/uploadProfilePicture/{userId}")
    public ResponseEntity<Map<String, Object>> uploadProfilePicture(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Please select a file to upload.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Ensure the directory exists
            File uploadDir = new File(UPLOAD_DIRECTORY);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save the file locally with the user ID in the file name
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                response.put("status", "error");
                response.put("message", "Invalid file.");
                return ResponseEntity.badRequest().body(response);
            }

            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String fileName = userId + fileExtension;
            Path path = Paths.get(UPLOAD_DIRECTORY + fileName);
            Files.write(path, file.getBytes());

            // Retrieve and update the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

            // Save the file path or URL (you might want to store just the file name or a relative path)
            user.setProfilPic(fileName);
            userRepository.save(user);

            response.put("status", "complete");
            response.put("message", "File uploaded and saved to user profile: " + fileName);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace(); // Replace with logging in production code
            response.put("status", "error");
            response.put("message", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (RuntimeException e) {
            e.printStackTrace(); // Replace with logging in production code
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private static final String UPLOAD_DIR = "uploads/"; // Adjust this if necessary

    @GetMapping("/profilePicture/{id}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Long id) {
        // Build file path
        File file = Paths.get(UPLOAD_DIR, id + ".jpg").toFile();

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(resource);
    }

}