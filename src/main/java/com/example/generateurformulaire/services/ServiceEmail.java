package com.example.generateurformulaire.services;

import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserRepository;
import com.example.generateurformulaire.DTO.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceEmail {

    @Autowired
    private Configuration configuration;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;


    public void sendVerificationEmail(String recipientEmail, String token) throws MessagingException {
        String subject = "Account Verification";
        String confirmationUrl = "http://localhost:8091/api/v1/auth/verify?token=" + token;
        String message = "Click the link to verify your account: " + confirmationUrl;

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(message, true);

        emailSender.send(mimeMessage);
    }

    public void sendResetPasswordEmail(String firstName, String token, String email) throws MessagingException {
        MimeMessage message = createResetPasswordEmail(firstName, token, email);
        emailSender.send(message);
    }

    private MimeMessage createResetPasswordEmail(String username, String token, String email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setFrom("bejaouinada7@gmail.com");
        helper.setTo(email);
        helper.setSubject("Password Reset Request");
        helper.setText("Hello " + username + ", \n\n" +
                "To reset your password, please use the following link: \n" +
                "http://localhost:4200/PasswordReset?token=" + token + "\n\n" +
                "If you did not request this change, please ignore this email.\n\n" +
                "The Support Team", false);
        helper.setSentDate(new Date());
        return message;
    }
    // Send password reset email
    //  public void sendResetPasswordEmail(String token, String email) throws MessagingException, IOException, TemplateNotFoundException, TemplateException {
    //    MimeMessage message = createResetPasswordEmail(token, email);
    //  emailSender.send(message);
    //}

   /* private MimeMessage createResetPasswordEmail(String token, String email) throws MessagingException, IOException, TemplateNotFoundException, TemplateException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("your-email@gmail.com");
        helper.setTo(email);
        helper.setSubject("Reset Password Request");
        String emailContent = getResetEmailContent(token);
        helper.setText(emailContent, true);
        helper.setSentDate(new Date());
        return message;
    }*/

    private String getResetEmailContent(String token) throws IOException, TemplateException, TemplateNotFoundException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("token", token);
        configuration.getTemplate("ResetEmail.ftlh").process(model, stringWriter);
        return stringWriter.toString();
    }

    // Send welcome email
    public void sendWelcomeEmail(User user) throws MessagingException, IOException, TemplateNotFoundException, TemplateException {
        MimeMessage message = createWelcomeEmail(user);
        emailSender.send(message);
    }

    private MimeMessage createWelcomeEmail(User user) throws MessagingException, IOException, TemplateNotFoundException, TemplateException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("yassine.aloui@esprit.tn");
        helper.setTo(user.getEmail());
        helper.setSubject("Welcome to MASTERCLASS CARS");
        String emailContent = getWelcomeEmailContent(user);
        helper.setText(emailContent, true);
        helper.setSentDate(new Date());
        return message;
    }

    private String getWelcomeEmailContent(User user) throws IOException, TemplateException, TemplateNotFoundException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        configuration.getTemplate("WelcomeEmail.ftlh").process(model, stringWriter);
        return stringWriter.toString();
    }

    // Send simple email with attachment
    public void sendSimpleMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("yassine.aloui@esprit.tn");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment(file.getFilename(), file);
        emailSender.send(message);
    }

    // Send simple email
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yassine.aloui@esprit.tn");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }


    public void sendEmailsToUsers(EmailDto emailDTO) throws MessagingException {
        List<User> users = userRepository.findAllById(emailDTO.getUserIds());

        for (User user : users) {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("bejaouinada7@gmail.com"); // Set your email
            helper.setTo(user.getEmail());
            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getBody(), true);

            emailSender.send(message);
        }


    }
}