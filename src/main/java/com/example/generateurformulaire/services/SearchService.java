package com.example.generateurformulaire.services;

import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserRepository;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.repository.FormRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class SearchService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FormRepository formRepository;

    public Map<String, Object> searchAll(String query) {
        List<User> users = userRepository.searchUsersAcrossFields(query);
        List<Form> forms = formRepository.searchFormsAcrossFields(query);

        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("forms", forms);

        return result;
    }
}
