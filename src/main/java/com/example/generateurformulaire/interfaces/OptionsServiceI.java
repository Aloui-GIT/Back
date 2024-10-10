package com.example.generateurformulaire.interfaces;

import com.example.generateurformulaire.entities.Options;

import java.util.List;
import java.util.Optional;

public interface OptionsServiceI {
    List<Options> getAllOptions();

    Optional<Options> getOptionsById(Long id);

    Options createOptions(Options options);


    void deleteOptionsById(Long id);

    Options createBlankOption(Long questionId);

    List<Options> getOptionsByQuestionId(Long questionId);

    Options updateOption(Long optionId, Options updatedOption);
}
