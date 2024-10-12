package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.entities.Config;
import com.example.generateurformulaire.services.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/configs")
@CrossOrigin(origins = "*")
public class ConfigController {

    private final ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<List<Config>> getAllConfigs() {
        List<Config> configs = configService.getAllConfigs();
        return new ResponseEntity<>(configs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Config> getConfigById(@PathVariable("id") Long id) {
        return configService.getConfigById(id)
                .map(config -> new ResponseEntity<>(config, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Config> createConfig(@RequestBody Config config) {
        Config createdConfig = configService.createConfig(config);
        return new ResponseEntity<>(createdConfig, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Config> updateConfig(@PathVariable("id") Long id, @RequestBody Config config) {
        if (!configService.getConfigById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        config.setIdConfig(id);
        Config updatedConfig = configService.updateConfig(config);
        return new ResponseEntity<>(updatedConfig, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable("id") Long id) {
        configService.deleteConfigById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}