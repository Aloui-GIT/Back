package com.example.generateurformulaire.interfaces;

import com.example.generateurformulaire.entities.Input;
import com.example.generateurformulaire.entities.Question;

import java.util.List;
import java.util.Optional;

public interface InputServiceI {
    List<Input> getAllInputs();


    Input getInputById(Long inputId);

    Input createInput(Input input);

    Input updateInput(Input input);

    void deleteInputById(Long id);


}
