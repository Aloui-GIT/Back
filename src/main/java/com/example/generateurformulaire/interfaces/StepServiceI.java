package com.example.generateurformulaire.interfaces;

import com.example.generateurformulaire.entities.Step;

import java.util.List;
import java.util.Optional;

public interface StepServiceI {


    List<Step> getStepsByFormId(Long formId);




    Step updateStep(Long id, Step updatedStep);

    void deleteStepById(Long id);

    Step addStepToForm(Long formId, Step step);

    Step createBlankStep(Long formId);


    long getStepCountByFormId(Long formId);
}
