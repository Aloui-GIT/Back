package com.example.generateurformulaire.interfaces;

import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.repository.FormRepository;

import java.util.List;
import java.util.Optional;

public interface FormServiceI {


    List<Form> getAllForms();

    Form getFormById(Long idForm);

    Form addForm(Form form);



    Form updateForm(Long formId, Form updatedForm);



    void deleteForm(Long userId, Long idForm);

    /* @Override
             public Form saveForm(Form form) {
                 return formRepository.save(form);
             }
             @Override
             public void deleteFormById(Long id) {
                 formRepository.deleteById(id);
             }*/
    Form createBlankForm(Long Userid);

    List<Form> getFormsByUserId(Long userId);
}
