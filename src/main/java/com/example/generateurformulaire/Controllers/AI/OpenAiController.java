package com.example.generateurformulaire.Controllers.AI;

import com.example.generateurformulaire.AI.CohereAIService;
import com.example.generateurformulaire.AI.OpenAIService;
import com.example.generateurformulaire.DTO.AI.SuggestionRequest;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.entities.Question;
import com.example.generateurformulaire.repository.QuestionRepository;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/openai")
@CrossOrigin(origins = "*")
public class OpenAiController {
    private final OpenAIService openAIService;
    private final QuestionRepository questionRepository;

    @Autowired
    private CohereAIService cohereAIService;



    public OpenAiController(OpenAIService openAIService, QuestionRepository questionRepository) {
        this.openAIService = openAIService;
        this.questionRepository = questionRepository;
    }


    @PostMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestBody SuggestionRequest request) {
        // Log the incoming request for debugging
        System.out.println("Received Request: Title - " + request.getTitle() + ", Description - " + request.getDescription() + ", QuestionId - " + request.getQuestionId());

        // Fetch the question text by question ID from the database
        String questionText = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"))
                .getQuestion();  // Assuming getQuestion() retrieves the text of the question

        // Fetch suggestions based on form's title, description, question text
        List<String> suggestions = openAIService.getSuggestions(
                request.getTitle(),
                request.getDescription(),
                questionText,
                ""  // No user input
        );

        return ResponseEntity.ok(suggestions);
    }




    @PostMapping("/suggestions/cohere")
    public ResponseEntity<Map<String, Object>> generateSuggestions(@RequestBody SuggestionRequest formData) {
        try {
            // Fetch the question text by question ID from the database
            String questionText = questionRepository.findById(formData.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"))
                    .getQuestion();  // Assuming getQuestion() retrieves the text of the question

            // Ensure question exists
            if (questionText == null || questionText.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Question text is empty."));
            }

            // Generate suggestions using CohereAIService
            String suggestions = cohereAIService.generateSuggestions(
                    formData.getTitle(),
                    formData.getDescription(),
                    questionText  // Use the text of the fetched question
            );

            // Create a response map to return suggestions as JSON
            Map<String, Object> response = new HashMap<>();
            response.put("suggestions", suggestions.split("\n"));  // Split by newlines if you want an array

            // Return the suggestions to the frontend
            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            // Log the exception details
            System.err.println("IOException: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error generating suggestions: " + e.getMessage()));
        } catch (ParseException e) {
            // Log the parsing exception details
            System.err.println("ParseException: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error parsing the AI response: " + e.getMessage()));
        }
    }


}
