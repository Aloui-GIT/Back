package com.example.generateurformulaire.services;

import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserRepository;
import com.example.generateurformulaire.AppUser.UserService;
import com.example.generateurformulaire.DTO.CommentDto;
import com.example.generateurformulaire.Exceptions.NotFoundException;
import com.example.generateurformulaire.entities.AdminPermission;
import com.example.generateurformulaire.entities.Comment;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.entities.LikeDislike;
import com.example.generateurformulaire.interfaces.FormServiceI;
import com.example.generateurformulaire.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class FormService implements FormServiceI {
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;



    @Autowired
    private LikeDislikeRepository likeDislikeRepository;

    @Autowired
    private CommentRepository commentRepository;



    @Override
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @Override
    public Form getFormById(Long idForm) {
        Optional<Form> formOptional = formRepository.findById(idForm);
        return formOptional.orElse(null);
    }

    @Override
    public Form addForm(Form form) {
        form.setCreateDate(new Date()); // Set the current date

        return formRepository.save(form);
    }

    @Override
    public Form updateForm(Long formId, Form updatedForm) {
        Optional<Form> optionalForm = formRepository.findById(formId);

        if (optionalForm.isPresent()) {
            Form existingForm = optionalForm.get();

            // Update the fields of the existingForm with the updatedForm
            existingForm.setTitle(updatedForm.getTitle());
            existingForm.setDescription(updatedForm.getDescription());
            // Update other fields as needed

            // Save the updated form
            return formRepository.save(existingForm);
        } else {
            throw new RuntimeException("Form not found with id: " + formId);
        }
    }

    @Override
    public void deleteForm(Long userId, Long idForm) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the user has the DELETE permission
        if (!userService.hasPermission(user, AdminPermission.DELETE_FORM)) { // Use UserService's hasPermission
            throw new NotFoundException("User does not have permission to delete forms");
        }

        // Check if the form exists before attempting to delete
        if (!formRepository.existsById(idForm)) {
            throw new NotFoundException("Form not found");
        }

        // Delete the form
        formRepository.deleteById(idForm);
    }



    @Override
    public Form createBlankForm(Long userId) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the user has the ADD permission
        if (!userService.hasPermission(user, AdminPermission.ADD_FORM)) { // Use UserService's hasPermission
            throw new NotFoundException("User does not have permission to add forms");
        }

        // Create a new form and set the user
        Form form = new Form();
        form.setUser(user);

        // Set the current date and time for CreateDate
        form.setCreateDate(new Date()); // Set the current date

        // Save and return the form
        return formRepository.save(form);
    }


    @Override
    public List<Form> getFormsByUserId(Long userId) {
        // Fetch and return forms by userId
        return formRepository.findByUserId(userId);
    }

    public void saveScreenshot(Long formId, String screenshotData) {
        // Check if screenshotData is valid
        if (screenshotData == null || !screenshotData.startsWith("data:image/")) {
            throw new IllegalArgumentException("Invalid screenshot data");
        }

        // Extract the base64 encoded image data and determine the format
        String[] parts = screenshotData.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid screenshot data format");
        }

        String base64Data = parts[1];
        String mimeType = screenshotData.split(":")[1].split(";")[0];
        String fileExtension = mimeType.split("/")[1]; // Extract file extension

        // Decode Base64 encoded image data
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to decode base64 data", e);
        }

        // Ensure the screenshots directory exists
        Path directoryPath = Paths.get("src/resources/img");
        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create screenshots directory", e);
        }

        // Define the path for the screenshot file
        Path destinationFile = directoryPath.resolve(formId + "." + fileExtension);

        // Write the image bytes to the file
        try {
            Files.write(destinationFile, imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write screenshot to file", e);
        }

    }


    public void updateFormResponsesStatus(Long formId, boolean isAcceptingResponses) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found with id " + formId));
        form.setAcceptingResponses(isAcceptingResponses);
        formRepository.save(form);
    }

    // Add or update like/dislike
    public void likeForm(Long formId, Long userId) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new RuntimeException("Form not found"));

        // Check for existing LikeDislike entry for this user and form
        Optional<LikeDislike> existingLikeDislike = likeDislikeRepository.findByFormIdAndUserId(formId, userId);

        if (existingLikeDislike.isPresent()) {
            LikeDislike likeDislike = existingLikeDislike.get();
            // Update if the existing entry is a dislike
            if (!likeDislike.isLike()) {
                likeDislike.setLike(true);
                likeDislikeRepository.save(likeDislike);
            }
        } else {
            // Create new LikeDislike entry
            LikeDislike likeDislike = new LikeDislike();
            likeDislike.setForm(form);
            likeDislike.setUser(new User(userId)); // Fetch the user properly if needed
            likeDislike.setLike(true);
            likeDislikeRepository.save(likeDislike);
        }

        // Update likes and dislikes count
        updateFormCounts(form);
    }

    public void dislikeForm(Long formId, Long userId) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new RuntimeException("Form not found"));

        // Check for existing LikeDislike entry for this user and form
        Optional<LikeDislike> existingLikeDislike = likeDislikeRepository.findByFormIdAndUserId(formId, userId);

        if (existingLikeDislike.isPresent()) {
            LikeDislike likeDislike = existingLikeDislike.get();
            // Update if the existing entry is a like
            if (likeDislike.isLike()) {
                likeDislike.setLike(false);
                likeDislikeRepository.save(likeDislike);
            }
        } else {
            // Create new LikeDislike entry
            LikeDislike likeDislike = new LikeDislike();
            likeDislike.setForm(form);
            likeDislike.setUser(new User(userId)); // Fetch the user properly if needed
            likeDislike.setLike(false);
            likeDislikeRepository.save(likeDislike);
        }

        // Update likes and dislikes count
        updateFormCounts(form);
    }

    private void updateFormCounts(Form form) {
        form.setLikesCount(likeDislikeRepository.countLikesByForm(form));
        form.setDislikesCount(likeDislikeRepository.countDislikesByForm(form));
        formRepository.save(form);
    }

    // Add comment with bad word filtering
    public Comment addComment(Long formId, CommentDto commentDto, Long userId) {
        String filteredCommentText = filterBadWords(commentDto.getCommentText());

        Comment comment = new Comment();
        comment.setForm(formRepository.findById(formId).orElseThrow());
        comment.setUser(new User(userId)); // Assumed you have a User entity
        comment.setCommentText(filteredCommentText);
        comment.setTimestamp(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    // Filter bad words
    private String filterBadWords(String text) {
        List<String> badWords = Arrays.asList("Fuck", "bitch"); // Add your bad words here
        String filteredText = text;
        for (String badWord : badWords) {
            filteredText = filteredText.replaceAll("(?i)" + badWord, "****");
        }
        return filteredText;
    }

    public List<Comment> getCommentsByForm(Long formId) {
        return commentRepository.findByFormId(formId);
    }

    public List<Form> getFormsByIds(List<Long> ids) {
        return formRepository.findAllById(ids);
    }

    public void updateMaxSubmissions(Long formId, int maxSubmissions) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found for this id :: " + formId));

        form.setMaxSubmissionsPerUser(maxSubmissions);
        formRepository.save(form);
    }
}