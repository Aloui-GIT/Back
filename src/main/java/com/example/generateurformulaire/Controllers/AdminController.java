package com.example.generateurformulaire.Controllers;


import com.example.generateurformulaire.AppUser.AppUserRole;
import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserRepository;
import com.example.generateurformulaire.AppUser.UserService;
import com.example.generateurformulaire.entities.AdminPermission;
import com.example.generateurformulaire.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/admin")//pre-path
@CrossOrigin(origins = "*")
public class AdminController
{


    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository ;

    @GetMapping("all")//api/admin/all
    public ResponseEntity<?> findAllUsers()
    {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PutMapping("/lock/{userId}")
    public ResponseEntity<?> lockUser(@PathVariable Long userId ,@RequestBody Long adminId) {
        userService.lockUser(userId , adminId);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/unlock/{userId}")
    public ResponseEntity<?> unlockUser(@PathVariable Long userId ,@RequestBody  Long adminId) {
        userService.unlockUser(userId ,adminId);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/change/{userId}/{role}") // api/user/change/{userId}/{role}
    public ResponseEntity<?> changeRole(
            @PathVariable Long userId, // Assuming userId is of type Long
            @PathVariable AppUserRole role,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        userService.changeRole(userId, role, userPrincipal.getUsername()); // Pass userId to the service
        return ResponseEntity.ok(true);
    }


    @PutMapping("/makeAdmin/{username}")
    public ResponseEntity<?> makeAdmin(@PathVariable(value="username") String username) {
        userService.makeAdmin(username);
        return ResponseEntity.ok(true);
    }
    @GetMapping("/admins")
    public List<User> allAdmins(){
    	return userService.allAdmins();
    }


    @PostMapping("/grant")
    public ResponseEntity<String> grantPermission(@RequestParam Long userId, @RequestParam Long adminId, @RequestParam AdminPermission permission) {
        userService.grantPermission(userId, adminId, permission);
        return ResponseEntity.ok("Permission granted successfully");
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokePermission(@RequestParam Long userId, @RequestParam Long adminId, @RequestParam AdminPermission permission) {
        userService.revokePermission(userId, adminId, permission);
        return ResponseEntity.ok("Permission revoked successfully");
    }

    // Endpoint to check if a user has a specific permission
    @GetMapping("/hasPermission")
    public ResponseEntity<Boolean> hasPermission( @RequestParam Long userId, @RequestParam AdminPermission permission) {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

             boolean hasPermission = userService.hasPermission(user, permission);
            return new ResponseEntity<>(hasPermission, HttpStatus.OK);

    }
    @DeleteMapping("/deleteAdmin/{userId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long userId) {
        try {
            userService.deleteAdminById(userId);
            return ResponseEntity.noContent().build(); // Return 204 No Content if the deletion is successful
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if the admin is not found
        }
    }

    @GetMapping("/permissions/{adminId}")
    public ResponseEntity<Set<AdminPermission>> getAdminPermissions(@PathVariable Long adminId) {
        Set<AdminPermission> permissions = userService.getAdminPermissions(adminId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/roles") // Endpoint to get available roles
    public ResponseEntity<List<String>> getAvailableRoles() {
        List<String> roles = Arrays.stream(AppUserRole.values())
                .map(Enum::name)
                .toList(); // Convert enum values to a list of strings
        return ResponseEntity.ok(roles);
    }
}
