package com.example.generateurformulaire.services;

import com.example.generateurformulaire.entities.Options;
import com.example.generateurformulaire.entities.Question;
import com.example.generateurformulaire.interfaces.OptionsServiceI;
import com.example.generateurformulaire.repository.OptionsRepository;
import com.example.generateurformulaire.repository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@Slf4j
public class OptionsService implements OptionsServiceI {
    @Autowired
    private OptionsRepository optionsRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public List<Options> getAllOptions() {
        return optionsRepository.findAll();
    }

    @Override
    public Optional<Options> getOptionsById(Long id) {
        return optionsRepository.findById(id);
    }

    @Override
    public Options createOptions(Options options) {
        return optionsRepository.save(options);
    }


    @Override
    public void deleteOptionsById(Long id) {
        optionsRepository.deleteById(id);
    }

    @Override
    public Options createBlankOption(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        Options option = new Options();
        option.setQuestion(question);
        return optionsRepository.save(option);
    }
    @Override
    public List<Options> getOptionsByQuestionId(Long questionId) {
        return optionsRepository.findByQuestionId(questionId);
    }
    @Override
    public Options updateOption(Long optionId, Options updatedOption) {
        Options option = optionsRepository.findById(optionId).orElseThrow(() -> new ResourceNotFoundException("Option not found"));
        option.setOption(updatedOption.getOption());
        option.setOptionValue(updatedOption.getOptionValue());
        option.setOptionOrder(updatedOption.getOptionOrder());
        return optionsRepository.save(option);
    }

}
