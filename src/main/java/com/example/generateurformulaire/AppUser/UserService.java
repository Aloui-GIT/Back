package com.example.generateurformulaire.AppUser;



import com.example.generateurformulaire.Exceptions.EmailExist;
import com.example.generateurformulaire.Exceptions.InvalidUsernameException;
import com.example.generateurformulaire.Exceptions.UsernameExist;
import com.example.generateurformulaire.Exceptions.UsernameNotExist;
import com.example.generateurformulaire.entities.AdminPermission;
import io.jsonwebtoken.io.IOException;
import jakarta.mail.MessagingException;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.management.Notification;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserService
{


    User updateUser(Long userId, User updatedUser);

    User saveUser(User user) throws UsernameNotExist, UsernameExist, EmailExist, MessagingException, IOException, ParseException , java.io.IOException, MessagingException, InvalidUsernameException;


    User saveAdmin(User user) throws UsernameNotExist, UsernameExist, EmailExist, MessagingException, IOException, ParseException , java.io.IOException, MessagingException;


    void deleteUserById(Long userId);

    void deleteAdminById(Long userId) ;

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);


    List<User> findAllUsers();

    User getUser(Long userId);






    void unlockUser(Long userId, Long adminId);

    void lockUser(Long userId, Long adminId);

    Set<AdminPermission> getAdminPermissions(Long adminId);

    @SneakyThrows
    boolean isValid(String password);

    @Transactional
        //Transactional is required when executing an update/delete query.
    void makeAdmin(String username);



    @Transactional // Transactional is required when executing an update/delete query.
    void changeRole(Long userId, AppUserRole newRole, String adminUsername);

    List<User> allAdmins();


    void grantPermission(Long userId, Long adminId, AdminPermission permission);

    void revokePermission(Long userId, Long adminId, AdminPermission permission);

    boolean hasPermission(User user, AdminPermission permission);


    String saveProfilePic(MultipartFile file) throws java.io.IOException;
}