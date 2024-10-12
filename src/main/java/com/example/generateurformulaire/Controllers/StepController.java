package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.entities.Step;
import com.example.generateurformulaire.services.StepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/steps")
@CrossOrigin(origins = "*")
public class StepController {

    @Autowired
    private StepService stepService;

    @PostMapping("/{formId}") // Endpoint for adding a step to a specific form
    public Step addStepToForm(@PathVariable Long formId, @RequestBody Step step) {
        return stepService.addStepToForm(formId, step);
    }

    @GetMapping("/getStepsByFormId/{formId}") // Endpoint for getting steps of a specific form
    public List<Step> getStepsByFormId(@PathVariable Long formId) {
        return stepService.getStepsByFormId(formId);
    }

    @PutMapping("/updateStep/{id}")
    public ResponseEntity<Step> updateStep(@PathVariable Long id, @RequestBody Step step) {
        Step updatedStep = stepService.updateStep(id, step);
        return ResponseEntity.ok(updatedStep);
    }

    @DeleteMapping("/deleteStepById/{id}")
    public void deleteStepById(@PathVariable Long id) {
        stepService.deleteStepById(id);
    }

    @PostMapping("/createBlankStep/{formId}")
    public ResponseEntity<Step> createBlankStep(@PathVariable Long formId) {
        try {
            Step createdStep = stepService.createBlankStep(formId);
            return new ResponseEntity<>(createdStep, HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/count/{formId}")
    public ResponseEntity<Long> getStepCount(@PathVariable Long formId) {
        long count = stepService.getStepCountByFormId(formId);
        return ResponseEntity.ok(count);
    }
}
