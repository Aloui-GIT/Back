package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.entities.Input;
import com.example.generateurformulaire.services.InputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inputs")
@CrossOrigin(origins = "*")
public class InputController {

    @Autowired
    private InputService inputService;

    @GetMapping("/getAllInputs")
    public List<Input> getAllInputs() {
        return inputService.getAllInputs();
    }



    @GetMapping("/getInputById/{id}")
    public ResponseEntity<Input> getInputById(@PathVariable Long id) {
        Input input = inputService.getInputById(id);
        if (input != null) {
            return ResponseEntity.ok(input);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Input createInput(@RequestBody Input input) {
        return inputService.createInput(input);
    }

    @PutMapping("/{id}")
    public Input updateInput(@PathVariable Long id, @RequestBody Input input) {
        input.setIdInput(id); // Ensure the ID is set
        return inputService.updateInput(input);
    }

    @DeleteMapping("/{id}")
    public void deleteInputById(@PathVariable Long id) {
        inputService.deleteInputById(id);
    }
}