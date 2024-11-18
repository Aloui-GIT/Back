package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.DTO.CommentDto;
import com.example.generateurformulaire.entities.Comment;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.repository.FormRepository;
import com.example.generateurformulaire.repository.LikeDislikeRepository;
import com.example.generateurformulaire.services.FormService;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = "*")
public class FormController {

    @Autowired
    private FormService formService;
    @Autowired
    private FormRepository formRepository;

    @Autowired
    private LikeDislikeRepository likeDislikeRepository;
   @PostMapping("/createBlankForm")
    public ResponseEntity<Form> createBlankForm(@RequestParam Long adminId) {
        Form createdForm = formService.createBlankForm(adminId);
        return new ResponseEntity<>(createdForm, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteForm/{idForm}")
    public ResponseEntity<Void> deleteForm(
            @RequestParam Long userId,
            @PathVariable Long idForm
    ) {
        formService.deleteForm(userId, idForm);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getAllForms")
    public ResponseEntity<List<Form>> getAllForms() {
        List<Form> forms = formService.getAllForms();
        return new ResponseEntity<>(forms, HttpStatus.OK);
    }


    @GetMapping("/getFormById/{idForm}")
    public ResponseEntity<Form> getFormById(@PathVariable Long idForm) {
        Form form = formService.getFormById(idForm);
        if (form != null) {
            return new ResponseEntity<>(form, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addForm")
    public ResponseEntity<Form> addForm(@RequestBody Form form) {
        Form addedForm = formService.addForm(form);
        return new ResponseEntity<>(addedForm, HttpStatus.CREATED);
    }


    @PutMapping("/updateForm/{idForm}")
    public ResponseEntity<Form> updateForm(@PathVariable Long idForm, @RequestBody Form form) {
        try {
            // Fetch the existing form by id
            Form existingForm = formService.getFormById(idForm);
            if (existingForm == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Update title, description, and modification date
            existingForm.setTitle(form.getTitle());
            existingForm.setDescription(form.getDescription());
            existingForm.setLastModificationDate(new Date());

            // Handle screenshot update if provided
            if (form.getScreenshotPath() != null && !form.getScreenshotPath().isEmpty()) {
                // Extract base64 data from screenshot (after the comma)
                String base64Screenshot = form.getScreenshotPath().split(",")[1];
                byte[] screenshotBytes = DatatypeConverter.parseBase64Binary(base64Screenshot);

                // Save screenshot using the form ID
                String screenshotPath = saveScreenshot(screenshotBytes, idForm); // Use idForm instead of form.getIdForm()
                existingForm.setScreenshotPath(screenshotPath);
            }

            // Update the form in the database
            Form updatedForm = formService.updateForm(idForm, existingForm);
            return new ResponseEntity<>(updatedForm, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    private static final String UPLOAD_DIR = "uploads/";

    // Fixed dimensions for the screenshots (e.g., 800x600 pixels)
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private String saveScreenshot(byte[] screenshotBytes, Long formId) throws Exception {
        Path path = Paths.get(UPLOAD_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Generate the file path for the screenshot
        String screenshotFileName = formId + "_screenshot.png";
        String fullPath = UPLOAD_DIR + screenshotFileName;

        // Convert the byte array into a BufferedImage
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(screenshotBytes));

        // Resize the image to the fixed dimensions
        BufferedImage resizedImage = resizeImage(originalImage, WIDTH, HEIGHT);

        // Save the resized image to the file
        try (FileOutputStream outputStream = new FileOutputStream(fullPath)) {
            ImageIO.write(resizedImage, "png", outputStream);
        }

        return fullPath;
    }

    // Helper method to resize the image to fixed dimensions
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(resultingImage, 0, 0, null);
        graphics2D.dispose();

        return resizedImage;
    }

    @GetMapping("/{formId}/screenshot")
    public ResponseEntity<Resource> getScreenshot(@PathVariable Long formId) {
        // Construct the file path for the screenshot
        Path screenshotPath = Paths.get(UPLOAD_DIR, formId + "_screenshot.png");

        // Check if file exists
        if (!Files.exists(screenshotPath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Consider returning a more detailed error body or message
        }

        // Load the file as a resource
        Resource resource = new FileSystemResource(screenshotPath.toFile());

        // Return the resource with the appropriate content type
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png") // PNG format
                .body(resource);
    }




    @GetMapping("/getFormsByUserId/{userId}")
    public List<Form> getFormsByUserId(@PathVariable Long userId) {
        return formService.getFormsByUserId(userId);
    }

    @PutMapping("/{id}/screenshot")
    public ResponseEntity<?> saveFormScreenshot(@PathVariable Long id, @RequestBody String screenshotData) throws IOException {
        // Save the screenshot (screenshotData) associated with the form ID (id)
        formService.saveScreenshot(id, screenshotData);
        return ResponseEntity.ok("Screenshot saved successfully");
    }

    @PutMapping("/{id}/responses")
    public ResponseEntity<?> updateFormResponsesStatus(
            @PathVariable("id") Long formId,
            @RequestBody Map<String, Boolean> body) {
        Boolean isAcceptingResponses = body.get("isAccepting");
        formService.updateFormResponsesStatus(formId, isAcceptingResponses);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{formId}/like/{userId}")
    public void likeForm(@PathVariable Long formId, @PathVariable Long userId) {
        formService.likeForm(formId, userId);
    }

    @PutMapping("/{formId}/dislike/{userId}")
    public void dislikeForm(@PathVariable Long formId, @PathVariable Long userId) {
        formService.dislikeForm(formId, userId);
    }

    @PostMapping("/addComment/{formId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long formId, @RequestBody CommentDto commentDto, @RequestParam Long userId) {
        Comment newComment = formService.addComment(formId, commentDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    @GetMapping("/getComments/{formId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long formId) {
        List<Comment> comments = formService.getCommentsByForm(formId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/getFormsByIds")
    public ResponseEntity<List<Form>> getFormsByIds(@RequestParam List<Long> ids) {
        List<Form> forms = formService.getFormsByIds(ids);
        return ResponseEntity.ok(forms);
    }
    @PutMapping("/{formId}/max-submissions/{maxSubmissions}")
    public ResponseEntity<?> setMaxSubmissions(@PathVariable Long formId, @PathVariable int maxSubmissions) {
        formService.updateMaxSubmissions(formId, maxSubmissions);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Maximum submissions updated successfully"));
    }

    @GetMapping("/{formId}/likes-dislikes")
    public Map<String, Integer> getLikesAndDislikes(@PathVariable Long formId) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new RuntimeException("Form not found"));

        int likeCount = likeDislikeRepository.countLikesByForm(form);
        int dislikeCount = likeDislikeRepository.countDislikesByForm(form);

        return Map.of("likes", likeCount, "dislikes", dislikeCount);
    }
}
