package com.example.generateurformulaire.services;

import com.example.generateurformulaire.entities.Config;
import com.example.generateurformulaire.interfaces.ConfigServiceI;
import com.example.generateurformulaire.repository.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ConfigService implements ConfigServiceI {

    private final ConfigRepository configRepository;

    @Autowired
    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public List<Config> getAllConfigs() {
        return configRepository.findAll();
    }

    @Override
    public Optional<Config> getConfigById(Long id) {
        return configRepository.findById(id);
    }

    @Override
    public Config createConfig(Config config) {
        return configRepository.save(config);
    }

    @Override
    public Config updateConfig(Config config) {
        return configRepository.save(config);
    }

    @Override
    public void deleteConfigById(Long id) {
        configRepository.deleteById(id);
    }
}
