package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.entities.Options;
import com.example.generateurformulaire.services.OptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/options")
@CrossOrigin(origins = "*")
public class OptionsController {

    @Autowired
    private OptionsService optionsService;

    @PostMapping("/createBlankOption/{questionId}")
    public ResponseEntity<Options> createBlankOption(@PathVariable Long questionId) {
        Options option = optionsService.createBlankOption(questionId);
        return ResponseEntity.status(201).body(option);
    }

    @GetMapping("/getOptionsByQuestionId/{questionId}")
    public ResponseEntity<List<Options>> getOptionsByQuestionId(@PathVariable Long questionId) {
        List<Options> options = optionsService.getOptionsByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @PutMapping("/updateOption/{optionId}")
    public ResponseEntity<Options> updateOption(@PathVariable Long optionId, @RequestBody Options updatedOption) {
        Options option = optionsService.updateOption(optionId, updatedOption);
        return ResponseEntity.ok(option);
    }

    @DeleteMapping("/deleteOption/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long optionId) {
        optionsService.deleteOptionsById(optionId);
        return ResponseEntity.noContent().build();
    }
}