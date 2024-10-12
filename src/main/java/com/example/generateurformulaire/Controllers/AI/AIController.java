package com.example.generateurformulaire.Controllers.AI;

import com.example.generateurformulaire.AI.CohereAIService;
import com.example.generateurformulaire.DTO.AI.SuggestionRequest;
import com.example.generateurformulaire.repository.QuestionRepository;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")  // Enable CORS for frontend calls
public class AIController {

    @Autowired
    private CohereAIService cohereAIService;

    @Autowired
    private QuestionRepository questionRepository;

    // POST endpoint to get suggestions based on form input
    @PostMapping("/suggestions")
    public ResponseEntity<String> generateSuggestions(@RequestBody SuggestionRequest formData) {
        try {
            // Fetch the question text by question ID from the database
            String questionText = questionRepository.findById(formData.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"))
                    .getQuestion();  // Assuming getQuestion() retrieves the text of the question

            // Ensure question exists
            if (questionText == null) {
                return ResponseEntity.badRequest().body("Question text is empty.");
            }

            // Generate suggestions using CohereAIService
            String suggestions = cohereAIService.generateSuggestions(
                    formData.getTitle(),
                    formData.getDescription(),
                    questionText  // Use the text of the fetched question
            );

            // Return the suggestions to the frontend
            return ResponseEntity.ok(suggestions);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IOException e) {
            // Handle potential errors
            return ResponseEntity.status(500).body("Error generating suggestions: " + e.getMessage());
        } catch (ParseException e) {
            return ResponseEntity.status(500).body("Error parsing the AI response: " + e.getMessage());
        }
    }
}
