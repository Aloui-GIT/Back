package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> searchAll(@RequestParam String query) {
        Map<String, Object> result = searchService.searchAll(query);
        return ResponseEntity.ok(result);
    }
}
