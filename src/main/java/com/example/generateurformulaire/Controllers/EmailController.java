package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.DTO.EmailDto;
import com.example.generateurformulaire.services.ServiceEmail;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@CrossOrigin(origins = "*")

public class EmailController {

    @Autowired
    private ServiceEmail serviceEmail;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmailToUsers(@RequestBody EmailDto emailDTO) {
        try {
            serviceEmail.sendEmailsToUsers(emailDTO);
            return ResponseEntity.ok("Emails sent successfully.");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending emails.");
        }
    }

}
