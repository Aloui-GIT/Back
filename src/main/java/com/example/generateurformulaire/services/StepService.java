package com.example.generateurformulaire.services;

import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.entities.Question;
import com.example.generateurformulaire.entities.Step;
import com.example.generateurformulaire.interfaces.StepServiceI;
import com.example.generateurformulaire.repository.FormRepository;
import com.example.generateurformulaire.repository.QuestionRepository;
import com.example.generateurformulaire.repository.StepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
@Service
@Slf4j
public class StepService  implements StepServiceI {

    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private FormRepository formRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public List<Step> getStepsByFormId(Long idForm) {
        return stepRepository.findByFormId(idForm);
    }

    @Override
    public Step updateStep(Long id, Step updatedStep) {
        Optional<Step> existingStepOpt = stepRepository.findById(id);
        if (!existingStepOpt.isPresent()) {
            throw new EntityNotFoundException("Step not found with ID: " + id);
        }

        Step existingStep = existingStepOpt.get();
        existingStep.setTitle(updatedStep.getTitle());
        existingStep.setStepDescription(updatedStep.getStepDescription());
        // Set other fields as necessary

        return stepRepository.save(existingStep);
    }


    @Override
    public void deleteStepById(Long id) {
        stepRepository.deleteById(id);
    }

    @Override
    public Step addStepToForm(Long idForm, Step step) {
        Optional<Form> formOptional = formRepository.findById(idForm);
        if (formOptional.isPresent()) {
            Form form = formOptional.get();
            step.setForm(form);
            return stepRepository.save(step);
        } else {
            throw new RuntimeException("Form not found");
        }
    }
    /*public Step createBlankStep(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));

        Step step = new Step();
        step.setTitle("Default Title");  // or leave as null/blank based on your requirements
        step.setStepDescription("Default Description");  // or leave as null/blank based on your requirements
        step.setForm(form);

        return stepRepository.save(step);
    }*/
    @Override

    public Step createBlankStep(Long formId) throws ResourceNotFoundException {
        // Find the form to associate the step with
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id " + formId));

        // Create a new step and associate it with the form
        Step newStep = new Step();
        newStep.setForm(form);
        newStep.setTitle("New Step"); // Example title, adjust as needed
        Step savedStep = stepRepository.save(newStep);

        // Create a default question for this step
        Question defaultQuestion = new Question();
        defaultQuestion.setQuestion(""); // Default question text
        defaultQuestion.setStep(savedStep);
        questionRepository.save(defaultQuestion); // Save the question

        // Return the step with the default question
        return savedStep;
    }
    @Override
    public long getStepCountByFormId(Long formId) {
        return stepRepository.countStepsByFormId(formId);
    }
}