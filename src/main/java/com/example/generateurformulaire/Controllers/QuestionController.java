package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.entities.Input;
import com.example.generateurformulaire.entities.Question;
import com.example.generateurformulaire.services.InputService;
import com.example.generateurformulaire.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private InputService inputService;



    @GetMapping("/getAllQuestions")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable Long id) {
        Optional<Question> questionOptional = questionService.getQuestionById(id);
        return questionOptional.orElse(null);
    }

    @PostMapping("/createQuestion")
    public Question createQuestion(@RequestBody Question question) {
        return questionService.createQuestion(question);
    }

   /* @PutMapping("/updateQuestion/{id}")
    public Question updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        question.setIdQuestion(id); // Ensure the ID is set
        return questionService.updateQuestion(id ,question);
    }*/
    @PutMapping("/updateQuestion/{questionId}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody Question question) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, question));
    }

    @DeleteMapping("/deleteQuestionById/{id}")
    public void deleteQuestionById(@PathVariable Long id) {
        questionService.deleteQuestionById(id);
    }

    @PostMapping("/createBlankQuestion/{stepId}")
    public ResponseEntity<Question> createBlankQuestion(@PathVariable Long stepId) {
        try {
        Question createdQuestion = questionService.createBlankQuestion(stepId);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    } catch(
    ResourceNotFoundException ex)

    {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch(
    Exception ex)

    {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

    @GetMapping("/getQuestionsByStepId/{stepId}")
    public List<Question> getQuestionsByStepId(@PathVariable Long stepId) {
        return questionService.getQuestionsByStepId(stepId);
    }

    @PostMapping("/assignInputToQuestion/{questionId}/{inputId}")
    public ResponseEntity<Question> assignInputToQuestion(
            @PathVariable Long questionId,
            @PathVariable Long inputId) {
        return ResponseEntity.ok(questionService.assignInputToQuestion(questionId, inputId));
    }


    @GetMapping("/getInputIdForQuestion/{id}")
    public Long getInputIdForQuestion(@PathVariable Long id) {
        return questionService.getInputIdForQuestion(id);
    }


    @GetMapping("/getInputByQuestionId/{questionId}")
    public ResponseEntity<Input> getInputByQuestionId(@PathVariable Long questionId) {
        Input input = questionService.getInputByQuestionId(questionId);
        return ResponseEntity.ok(input);
    }

    @GetMapping("/getQuestionsByFormId/{formId}")
    public ResponseEntity<List<Question>> getQuestionsByFormId(@PathVariable Long formId) {
        List<Question> questions = questionService.getQuestionsByFormId(formId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{idQuestion}/required")
    public ResponseEntity<Boolean> isQuestionRequired(@PathVariable Long idQuestion) {
        boolean isRequired = questionService.isQuestionRequired(idQuestion);
        return ResponseEntity.ok(isRequired);
    }
}
