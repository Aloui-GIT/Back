package com.example.generateurformulaire.AI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CohereAIService {

    private static final String API_KEY = "3yt9Z4jThvSpqrNwetgb6byJhlORRGfTPayPEv08"; // Consider using environment variables for sensitive information
    private static final String API_URL = "https://api.cohere.ai/generate";
    private static final Logger logger = LoggerFactory.getLogger(CohereAIService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Generate suggestions method
    public String generateSuggestions(String title, String description, String question) throws IOException, ParseException {

        // Build the prompt dynamically and sanitize input
        String prompt = String.format(
                "Form Title: %s\nForm Description: %s\nQuestion: %s\nGenerate relevant suggestions for the user:",
                sanitizeString(title), sanitizeString(description), sanitizeString(question)
        );

        // Create the JSON request payload using a Map
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("model", "command-xlarge-nightly");
        requestPayload.put("prompt", prompt);
        requestPayload.put("max_tokens", 150);
        requestPayload.put("temperature", 0.7);

        // Convert the Map to a JSON string
        String jsonBody = objectMapper.writeValueAsString(requestPayload);

        // Log the JSON body for debugging
        logger.debug("Sending JSON to Cohere API: {}", jsonBody);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicRequestBuilder requestBuilder = ClassicRequestBuilder.post(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .setEntity(new StringEntity(jsonBody));

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(requestBuilder.build())) {
                int statusCode = response.getCode();

                // Log the status code received
                logger.debug("Received status code: {}", statusCode);

                // Parse the response body
                String responseBody = EntityUtils.toString(response.getEntity());

                // Log the raw response body for debugging
                logger.debug("Received response from Cohere API: {}", responseBody);

                if (statusCode == 200) {
                    return parseSuggestions(responseBody);
                } else {
                    throw new IOException("Error: Received status code " + statusCode + " from Cohere API. Response: " + responseBody);
                }
            }
        }
    }

    // Sanitize input strings to avoid invalid JSON syntax
    private String sanitizeString(String str) {
        if (str == null) return null;
        return str.replaceAll("\\n", " ") // Replace newlines with spaces
                .replace("\r", "")      // Remove carriage returns
                .trim();                // Trim leading and trailing whitespace
    }

    private String parseSuggestions(String responseBody) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseBody);

        // Log the parsed tree for debugging
        logger.debug("Parsed JSON response tree: {}", rootNode);

        // Check if the text node exists in the response
        if (rootNode.has("text")) {
            String fullText = rootNode.get("text").asText();

            // Split the text by newlines
            String[] suggestions = fullText.split("\n");

            // Create a StringBuilder to hold limited suggestions
            StringBuilder limitedSuggestions = new StringBuilder();
            int count = 0;

            for (String suggestion : suggestions) {
                if (!suggestion.trim().isEmpty() && count < 3) { // Check for non-empty suggestions
                    if (limitedSuggestions.length() > 0) {
                        limitedSuggestions.append("\n"); // Append a newline for separation
                    }
                    limitedSuggestions.append(suggestion.trim()); // Append trimmed suggestions
                    count++;
                }
            }

            if (count == 0) {
                throw new IOException("Error: No valid suggestions found in response.");
            }

            return limitedSuggestions.toString(); // Convert StringBuilder to String
        }

        throw new IOException("Error: Invalid response format received from Cohere API. Response: " + responseBody);
    }


}
