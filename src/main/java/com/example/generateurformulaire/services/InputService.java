package com.example.generateurformulaire.services;

import com.example.generateurformulaire.entities.Input;
import com.example.generateurformulaire.interfaces.InputServiceI;
import com.example.generateurformulaire.repository.InputRepository;
import com.example.generateurformulaire.repository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InputService implements InputServiceI {
    @Autowired
    private InputRepository inputRepository;
    @Autowired
    QuestionRepository questionRepository ;
    @Override
    public List<Input> getAllInputs() {
        return inputRepository.findAll();
    }

    @Override
    public Input getInputById(Long inputId) {
        return inputRepository.findById(inputId).orElse(null);
    }

    @Override
    public Input createInput(Input input) {
        return inputRepository.save(input);
    }

    @Override
    public Input updateInput(Input input) {
        return inputRepository.save(input);
    }

    @Override
    public void deleteInputById(Long id) {
        inputRepository.deleteById(id);
    }



}
