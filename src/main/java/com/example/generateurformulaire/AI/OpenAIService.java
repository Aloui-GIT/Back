package com.example.generateurformulaire.AI;

import com.example.generateurformulaire.repository.QuestionRepository;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OpenAIService {
    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${openai.api.key}")  // Store API key in application.properties
    private String apiKey;

    @Autowired
    private QuestionRepository questionRepository;

    public List<String> getSuggestions(String title, String description, String question, String userInput) {
        String prompt = generatePrompt(title, description, question, userInput);
        return callOpenAIAPI(prompt);
    }

    // Create the dynamic prompt that describes the current form's context
    private String generatePrompt(String title, String description, String question, String userInput) {
        return String.format(
                "Form Title: %s\nForm Description: %s\nQuestion: %s\nUser Input: %s\nGenerate relevant suggestions for the user:",
                title, description, question, userInput
        );
    }

    // Call the OpenAI API to get dynamic suggestions
    private List<String> callOpenAIAPI(String prompt) {
        int retries = 0;
        int maxRetries = 6; // Maximum number of retries
        long waitTime = 2000; // Initial wait time (2 seconds)

        while (retries < maxRetries) {
            try {
                RestTemplate restTemplate = new RestTemplate();

                // Set up request headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);  // Use injected API key

                // Create the request body
                String requestBody = createRequestBody(prompt);
                logger.debug("Request Body: {}", requestBody);  // Debugging line

                // Create the HTTP entity (request) with headers and body
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

                // Make the POST request
                ResponseEntity<String> response = restTemplate.exchange(
                        OPENAI_API_URL, HttpMethod.POST, entity, String.class
                );

                // Parse the response and extract the suggestions
                return parseOpenAIResponse(response.getBody());

            } catch (HttpClientErrorException e) {
                logger.error("HTTP error: {}, Response Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    String responseBody = e.getResponseBodyAsString();
                    // Check if it's a quota-related error
                    if (responseBody.contains("insufficient_quota")) {
                        logger.error("Quota exceeded. Please check your OpenAI account for billing details.");
                        return Collections.singletonList("Error: Quota exceeded. Please check your OpenAI account for billing details.");
                    }

                    // Handle other 429 errors
                    retries++;
                    logger.warn("Received 429 error. Retrying... Attempt: {}", retries);
                    try {
                        Thread.sleep(waitTime); // Wait before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // Restore the interrupted status
                        break; // Exit the loop if interrupted
                    }
                    waitTime *= 2; // Double the wait time for exponential backoff
                } else {
                    logger.error("Error during OpenAI API call: {}", e.getMessage());
                    return Collections.singletonList("Error: Unable to fetch suggestions from OpenAI. Status code: " + e.getStatusCode());
                }
            } catch (Exception e) {
                logger.error("Unexpected error: {}", e.getMessage());
                return Collections.singletonList("Error: Unable to fetch suggestions from OpenAI.");
            }
        }

        logger.error("Maximum retries reached. Unable to fetch suggestions from OpenAI.");
        return Collections.singletonList("Error: Maximum retries reached. Unable to fetch suggestions from OpenAI.");
    }


    // Create the JSON request body for the OpenAI API
    private String createRequestBody(String prompt) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonBody = objectMapper.createObjectNode();
        jsonBody.put("model", "gpt-3.5-turbo");
        ArrayNode messages = jsonBody.putArray("messages");
        messages.add(objectMapper.createObjectNode().put("role", "system").put("content", "You are an assistant."));
        messages.add(objectMapper.createObjectNode().put("role", "user").put("content", prompt));

        return jsonBody.toString();
    }

    // Parse the response from OpenAI and extract the suggestions
    private List<String> parseOpenAIResponse(String responseBody) throws Exception {
        // Use Jackson to parse the JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);

        // Extract the message content (which contains the AI-generated text)
        String suggestionsText = root.path("choices").get(0).path("message").path("content").asText();

        // Split the suggestions into separate lines and filter out empty suggestions
        List<String> suggestions = new ArrayList<>();
        Collections.addAll(suggestions, suggestionsText.split("\n"));
        suggestions.removeIf(String::isEmpty);  // Remove any empty suggestions

        return suggestions;
    }
}
