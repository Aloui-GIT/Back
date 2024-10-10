package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserRepository;
import com.example.generateurformulaire.AppUser.UserService;
import com.example.generateurformulaire.AppUser.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

@RestController
@RequestMapping("/api/v1/auth")
public class VerificationController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        String htmlResponse;
        HttpStatus status;

        try {
            userService.verifyUser(token);
            htmlResponse = "<html><head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; text-align: center; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                    ".container { display: flex; flex-direction: column; justify-content: center; align-items: center; height: 100vh; }" +
                    ".message { padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }" +
                    ".success { background-color: #d4edda; color: #155724; }" +
                    ".error { background-color: #f8d7da; color: #721c24; }" +
                    "h1 { margin: 0; }" +
                    "p { margin: 10px 0; }" +
                    "a { color: #007bff; text-decoration: none; font-weight: bold; }" +
                    "a:hover { text-decoration: underline; }" +
                    "</style>" +
                    "</head><body>" +
                    "<div class='container'>" +
                    "<div class='message success'>" +
                    "<h1>Account Verified Successfully!</h1>" +
                    "<p>Your account has been verified. You can now <a href='http://localhost:4200/login'>login</a> to your account.</p>" +
                    "<script type='text/javascript'>" +
                    "setTimeout(function() {" +
                    "    window.location.href = 'http://localhost:4200/login';" +
                    "}, 3000); // Redirect after 3 seconds" +
                    "</script>" +
                    "</div>" +
                    "</div>" +
                    "</body></html>";
            status = HttpStatus.OK;
        } catch (NotFoundException e) {
            htmlResponse = "<html><head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; text-align: center; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                    ".container { display: flex; flex-direction: column; justify-content: center; align-items: center; height: 100vh; }" +
                    ".message { padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }" +
                    ".success { background-color: #d4edda; color: #155724; }" +
                    ".error { background-color: #f8d7da; color: #721c24; }" +
                    "h1 { margin: 0; }" +
                    "p { margin: 10px 0; }" +
                    "a { color: #007bff; text-decoration: none; font-weight: bold; }" +
                    "a:hover { text-decoration: underline; }" +
                    "</style>" +
                    "</head><body>" +
                    "<div class='container'>" +
                    "<div class='message error'>" +
                    "<h1>Invalid Token</h1>" +
                    "<p>The verification link is invalid or has expired. Please try again. You will be redirected to the <a href='http://localhost:4200/login'>login page</a> shortly.</p>" +
                    "<script type='text/javascript'>" +
                    "setTimeout(function() {" +
                    "    window.location.href = 'http://localhost:4200/login';" +
                    "}, 5000); // Redirect after 5 seconds" +
                    "</script>" +
                    "</div>" +
                    "</div>" +
                    "</body></html>";
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(htmlResponse, status);
    }



}