package com.example.generateurformulaire.interfaces;

import com.example.generateurformulaire.entities.Config;

import java.util.List;
import java.util.Optional;

public interface ConfigServiceI {
    List<Config> getAllConfigs();

    Optional<Config> getConfigById(Long id);

    Config createConfig(Config config);

    Config updateConfig(Config config);

    void deleteConfigById(Long id);
    // Create operation

}
